package cn.geeklemon.server.response.writer;

import cn.geeklemon.server.response.HttpResponseUtil;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.*;

import java.io.IOException;
import java.io.OutputStream;

public class HttpOutputStream extends OutputStream implements HttpResponseWriter {
    private ByteBuf buffer;
    private ChannelHandlerContext context;

    private boolean actionStarted = false;
    private boolean headSended = false;
    ByteBufAllocator pool = new PooledByteBufAllocator();

    public HttpOutputStream(ChannelHandlerContext context) {
        super();
        this.context = context;
        pool = new PooledByteBufAllocator();
        buffer = pool.buffer(1025);
    }

    /**
     * 大多数程序都使用缓冲，不会直接调用此方法
     */
    @Override
    public void write(int b) throws IOException {
        actionStarted = true;
        checkOneChunk();
        buffer.writeByte(b);
    }

    private void checkOneChunk() {
        if (buffer.readableBytes() > 1024) {
            try {
                write(buffer.array());
            } catch (IOException e) {
                e.printStackTrace();
            }
            buffer.clear();
            buffer.release();
            buffer = pool.buffer(1025);
        }
    }

    @Override
    public HttpResponseWriter writeData(byte[] data) throws Exception {
        write(data, 0, data.length);
        return this;
    }

    @Override
    public void complete() {
        try {
            if (actionStarted) {
                context.writeAndFlush(new DefaultLastHttpContent(), context.newPromise());
            }
            try {
                this.buffer.clear();
                this.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } finally {
            buffer.clear();
            buffer.release();
        }
        this.buffer = null;
        this.context = null;
    }

    /**
     * 所有的内容转到这里
     */
    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        writeBufferFirst();
        if (!actionStarted) {
            actionStarted = true;
            writeResponse();
            context.writeAndFlush(new DefaultHttpContent(Unpooled.wrappedBuffer(b, off, len)));
        } else {
            context.writeAndFlush(new DefaultHttpContent(Unpooled.wrappedBuffer(b, off, len)));
        }
    }

    private void writeResponse() {
        HttpResponseStatus status = HttpResponseUtil.getStatus();
        DefaultHttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, status);
        response.headers().add(HttpHeaderNames.TRANSFER_ENCODING, HttpHeaderValues.CHUNKED);
        HttpResponseUtil.addHeaderAndCookie(response);
        context.write(response);
    }

    @Override
    public void write(byte[] b) throws IOException {
        write(b, 0, b.length);
    }

    /**
     * buffer中的内容都是write(int b)写入的，其他的方法传输的都被直接发送出去了 大多数情况下不会进到这个地方
     */

    private void writeBufferFirst() {
        if (buffer.readableBytes() < 1) {
            return;
        }
        if (!actionStarted) {
            actionStarted = true;
            writeResponse();
            context.writeAndFlush(new DefaultHttpContent(buffer));
            buffer = pool.buffer(1025);
        } else {
            context.writeAndFlush(new DefaultHttpContent(buffer));
            buffer = pool.buffer(1025);
        }
    }

}
