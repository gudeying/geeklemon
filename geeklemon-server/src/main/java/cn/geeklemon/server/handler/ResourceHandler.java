package cn.geeklemon.server.handler;

import cn.geeklemon.core.util.ResourceUtils;
import cn.geeklemon.server.TemporaryDataHolder;
import cn.geeklemon.server.response.HttpResponseUtil;
import cn.hutool.core.io.resource.NoResourceException;
import cn.hutool.core.io.resource.ResourceUtil;
import cn.hutool.core.text.StrBuilder;
import cn.hutool.core.util.NumberUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.core.util.URLUtil;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.handler.stream.ChunkedNioFile;
import io.netty.util.AsciiString;
import io.netty.util.ReferenceCounted;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.activation.MimetypesFileTypeMap;
import java.io.*;
import java.net.FileNameMap;
import java.net.URLConnection;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/20 17:00 Modified by : kavingu
 */
public class ResourceHandler extends ChannelInboundHandlerAdapter {
    public static final Logger LOGGER = LoggerFactory.getLogger(ResourceHandler.class);

    @Override
    public void channelRead(ChannelHandlerContext channelHandlerContext, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            HttpRequest request = (HttpRequest) msg;
            String url = TemporaryDataHolder.loadLemonRequest().URI();

            try {
                url = URLUtil.getPath(url);
                /*有一些路径会为null*/
                if (!url.contains(".")) {
                    super.channelRead(channelHandlerContext, msg);
                    return;
                }
            } catch (Exception e) {
                FullHttpResponse response = HttpResponseUtil.getForbiddenResponse();
                channelHandlerContext.writeAndFlush(response, channelHandlerContext.newPromise()).addListener(ChannelFutureListener.CLOSE);
                release(msg);
                return;
            }
            String fileName = "static" + url;

            String shufix = url.substring(url.lastIndexOf(".") + 1, url.length());
            if (!ResourceUtils.fileAccept(shufix)) {
                super.channelRead(channelHandlerContext, msg);
                return;
            }
            String mimeType = getMimeType(shufix);

            InputStream inputStream = null;
            try {
                inputStream = ResourceUtil.getStream(fileName);
            } catch (NoResourceException ignored) {
                // 只是项目下的文件
            }

            if (ObjectUtil.isNotNull(inputStream)) {
                projectFile(inputStream, channelHandlerContext, request, mimeType);
                if (msg instanceof FullHttpRequest) {
                    FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
                    fullHttpRequest.content().release();
                }
                return;
            }
            File file = ResourceUtils.getFile(url);

            if (ObjectUtil.isNotNull(file) && file.isFile()) {
                RandomAccessFile accessFile = new RandomAccessFile(file, "r");
                long fileLength = accessFile.length();
                String rangeHeader = request.headers().get("Range");
                String range = rangeRes(rangeHeader);

                HttpResponse response;
                DefaultFileRegion fileRegion;
                if (range != null) {
                    long[] longs = getRange(range, fileLength);
                    long position = longs[0];
                    long count = longs[1] - position;

                    response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.PARTIAL_CONTENT);

                    response.headers().add(HttpHeaderNames.CONTENT_LENGTH, count);
                    response.headers().add(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);
                    fileRegion = new DefaultFileRegion(accessFile.getChannel(), position, count);
                    String rangeHeaderVal = getRangeHeader(range, position, longs[1], fileLength);
                    response.headers().add(HttpHeaderNames.CONTENT_RANGE, rangeHeaderVal);
                } else {
                    response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
                    response.headers().add(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                    response.headers().add(HttpHeaderNames.ACCEPT_RANGES, HttpHeaderValues.BYTES);
                    fileRegion = new DefaultFileRegion(accessFile.getChannel(), 0, fileLength);

                }
                response.headers().add(HttpHeaderNames.CONTENT_TYPE, mimeType);
                channelHandlerContext.write(response);
                ChannelFuture sendFileFuture = channelHandlerContext.write(fileRegion,
                        channelHandlerContext.newProgressivePromise());
                // 添加传输监听
                sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                    @Override
                    public void operationProgressed(ChannelProgressiveFuture future, long progress, long total) {
//                        if (total < 0) { // total unknown
//                            System.err.println("Transfer progress: " + progress);
//                        } else {
//                            System.err.println("Transfer progress: " + progress + " / " + total);
//                        }
                    }

                    @Override
                    public void operationComplete(ChannelProgressiveFuture future) throws Exception {
//                        System.out.println("Transfer complete.");
                    }
                });
                // 如果使用Chunked编码，最后则需要发送一个编码结束的看空消息体，进行标记，表示所有消息体已经成功发送完成。
                // ChannelFuture lastContentFuture =
                // channelHandlerContext.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
                if (close(request)) {
                    sendFileFuture.addListener(ChannelFutureListener.CLOSE);
                }
                channelHandlerContext.flush();
                if (msg instanceof FullHttpRequest) {
                    FullHttpRequest fullHttpRequest = (FullHttpRequest) msg;
                    fullHttpRequest.content().release();
                }
            } else {
                super.channelRead(channelHandlerContext, msg);
            }
        }
    }

    /**
     * chunk传输文件，适用于无法直接获取到File对象的文件，比如打为jar包，jar包中的文件
     *
     * @param inputStream 文件 inputStream，不能为空
     * @param context     ChannelHandlerContext，用于发送response
     * @param request
     * @throws IOException {@link BufferedInputStream#read()}
     */
    private void projectFile(final InputStream inputStream, ChannelHandlerContext context, HttpRequest request,
                             String contentType) throws IOException {

        int contentLength = inputStream.available();
        byte[] bytes;// 缓冲器1M，传输大文件时候也没有出现问题，小的缓冲器传输大文件时出现了无法读取的问题
        if (contentLength > 1024 * 10000) {
            // 大于10M
            bytes = new byte[1024 * 10000];
        } else {
            bytes = new byte[1024 * 1000];
        }
        final BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.headers().set(HttpHeaderNames.CONTENT_LENGTH, contentLength);
        // response.headers().set(HttpHeaderNames.TRANSFER_ENCODING,
        // HttpHeaderValues.CHUNKED);
        // response.headers().set(HttpHeaderNames.CONTENT_DISPOSITION, new
        // AsciiString("inline"));
        ChannelFuture f = context.write(response);
        int read = 0;
        while (bufferedInputStream.available() > 0) {
            read = bufferedInputStream.read(bytes);
            // DefaultHttpContent httpContent = new
            // DefaultHttpContent(Unpooled.wrappedBuffer(bytes, 0, read));
            context.writeAndFlush(Unpooled.wrappedBuffer(bytes, 0, read));
        }
        ChannelFuture lastContentFuture = context.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT,
                context.newPromise());
        lastContentFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {

                try {
                    inputStream.close();
                    bufferedInputStream.close();
                } catch (IOException e) {
                }
            }
        });
        if (close(request)) {
            lastContentFuture.addListener(ChannelFutureListener.CLOSE);
        }
    }

    /**
     * 如果http请求本身不支持keep-alive，那么需要传输完成之后直接关闭channel。比如 HTTP_1_0， HTTP_1_1
     * 默认是支持keep-alive的，只有显性标注close才会关闭连接。 传输完成之后如果没有添加
     * {@link ChannelFutureListener#CLOSE} 这个监听器<br/>
     * 经过测试，访问一个网页加载资源，比如js、css以及本身的请求使用的都是一个channel并且F5刷新也是使用的已经建立起来的那个连接
     *
     * @param request request
     * @return 是否需要关闭连接
     */
    private boolean close(HttpRequest request) {
        boolean result = request.headers().contains(HttpHeaderNames.CONNECTION, "close", true)
                || (request.protocolVersion().equals(HttpVersion.HTTP_1_0)
                && !request.headers().contains(HttpHeaderNames.CONNECTION, "keep-alive", true));

        return result;
    }

    /**
     * @param string 后缀名
     * @return
     */
    private String getMimeType(String string) {

        return ResourceUtils.getFIleContentType(string);
    }

    private DefaultFileRegion getFileRegion(RandomAccessFile file) {
        return null;
    }

    /**
     * @param rangeHeader
     * @param fileLength
     * @return
     */
    private long[] getRange(String rangeHeader, long fileLength) {
        if (rangeHeader.startsWith("-")) {
            /*最后i个*/
            rangeHeader = StrUtil.removePrefix(rangeHeader, "-");
            long i = NumberUtil.parseLong(rangeHeader);
            long[] result = new long[2];
            result[0] = fileLength - i;
            result[1] = fileLength;
            return result;
        }
        if (rangeHeader.endsWith("-")) {
            /*从i开始到结束*/
            rangeHeader = StrUtil.removeSuffix(rangeHeader, "-");
            long i = NumberUtil.parseLong(rangeHeader);
            long[] result = new long[2];
            result[0] = i;
            result[1] = fileLength;
            return result;
        }
        String[] split = rangeHeader.split("-");
        int start = Integer.parseInt(split[0]);
        int end = Integer.parseInt(split[1]);
        return new long[]{start, end};
    }

    /**
     * 如果为空，range错误
     *
     * @param rangeHeader
     * @return
     */
    private String rangeRes(String rangeHeader) {
        if (StrUtil.isNotBlank(rangeHeader)) {
            try {
                String trim = StrUtil.removePrefix(rangeHeader, "bytes=").trim();
                if (trim.contains("-")) {
                    return trim;
                }
            } catch (Exception e) {
                return null;
            }
        }
        return null;
    }

    private String getRangeHeader(String rangerRequest, long start, long count, long fileLength) {
        StrBuilder builder = new StrBuilder("bytes ");
        if (!rangerRequest.startsWith("-") && !rangerRequest.endsWith("-")) {
            builder.append(rangerRequest).append("/").append(fileLength);
            return builder.toString();
        }
        builder.append(start).append("-").append(count - 1).append("/").append(fileLength);
        return builder.toString();
    }

    private void release(Object msg) {
        if (msg instanceof ReferenceCounted) {
            ReferenceCounted referenceCounted = (ReferenceCounted) msg;
            referenceCounted.release();
        }
    }
}