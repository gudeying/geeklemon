package cn.geekelmon.cluster.bootstrap;

import cn.geekelmon.cluster.context.ServiceWatcher;
import cn.geekelmon.cluster.node.ServiceNode;
import cn.geeklemon.server.response.HttpResponseUtil;
import cn.hutool.core.util.ObjectUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 13:38
 * Modified by : kavingu
 */
public class MasterServerHandler extends ChannelInboundHandlerAdapter {

    private final static Logger LOGGER = LoggerFactory.getLogger(MasterServerHandler.class);

    private ServiceNode node;
    private Set<Channel> channelSet = new HashSet<>();

    /**
     * 前台 <--->MasterChannel
     */
    private Channel clusterChannel;

    /**
     * Master <--->Service Channel
     */
    private Channel serviceChannel;

    private ChannelFuture serviceConnectFuture;

    private ServiceWatcher serviceWatcher;

    public MasterServerHandler(ServiceWatcher serviceWatcher) {
        this.serviceWatcher = serviceWatcher;
    }

    /**
     * 浏览器的http请求到达master，master负责建立一个与实际服务提供者slaver的连接
     *
     * @param ctx
     * @throws Exception
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        node = serviceWatcher.availableService();
        if (ObjectUtil.isNotNull(node)) {
            clusterChannel = ctx.channel();
            LOGGER.info("http request lead to {}", node);

            Bootstrap bootstrap = new Bootstrap();
            bootstrap.group(clusterChannel.eventLoop())
                    .channel(ctx.channel().getClass())

                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        public void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new HttpClientCodec());
//
                            /*不能使用聚合，否则无法传输文件*/
//                            pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
//                            pipeline.addLast(new ChunkedWriteHandler());
                            pipeline.addLast(new MasterServerServiceResponseHandler(clusterChannel));
                        }
                    });
            bootstrap.option(ChannelOption.AUTO_READ, false);
            //连接ServiceNode
            serviceConnectFuture = bootstrap.connect(node.getHost(), node.getPort());
            // 获取channel
            serviceChannel = serviceConnectFuture.channel();
        }

    }

    /**
     * master读取浏览器请求的request，将该request直接发送给slaver，在MasterServerBackendHandler中转发结果给浏览器
     *
     * @param ctx
     * @param msg
     */
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (ObjectUtil.isNull(node)) {
            ctx.channel().writeAndFlush(HttpResponseUtil.getNoServiceResponse());
            return;
        }

        serviceConnectFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) {
                if (future.isDone()) {
                    // ServiceNode连接完成
                    clusterChannel.read();
                    if (serviceChannel.isActive()) {
                        if (msg instanceof HttpRequest) {
//                            HttpRequest request = (HttpRequest) msg;
                            /*将消息发送给实际负责业务的slaver*/
                            serviceChannel.writeAndFlush(msg).addListener(new ChannelFutureListener() {
                                @Override
                                public void operationComplete(ChannelFuture future) {
//                                    ctx.channel().read();
                                    if (future.isSuccess()) {
                                        // 下一段数据
                                        ctx.channel().read();
                                    } else {
                                        LOGGER.error("write to backend {}:{} error,cause:{}", node.getHost(), node.getPort(), future.cause());
                                        future.channel().close();
                                    }
                                }
                            });
                        } else {
                            closeOnFlush(ctx.channel());
                        }
                    }
                } else {
                    //连接失败
                    LOGGER.error("connect to Service {}:{} error,cause:{}", node.getHost(), node.getPort(), future.cause());
                    clusterChannel.close();
                }
            }
        });

    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) {
        if (serviceChannel != null) {
            closeOnFlush(serviceChannel);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        closeOnFlush(ctx.channel());
    }

    static void closeOnFlush(Channel channel) {
        if (channel.isActive()) {
            channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
