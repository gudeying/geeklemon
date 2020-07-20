package cn.geeklemon.server;

import cn.geeklemon.core.bean.factory.InitializingBean;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Value;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.util.PropsUtil;
import cn.geeklemon.server.websocket.context.EndPointHelper;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/9/20 16:35
 * Modified by : kavingu
 */
public final class LemonServer implements InitializingBean, LemonServerInterFace {
    private static final Logger LOGGER = LoggerFactory.getLogger(LemonServer.class);
    @Value(name = "server.bossGroupSize", defaultValue = "4")
    private Integer bossGroupSize=4;
    @Value(name = "server.workGroupSize", defaultValue = "16")
    private Integer workGroupSize=16;

    @Value(name = "server.port", defaultValue = "8080")
    private Integer port=8080;

    @Autowired
    private ApplicationContext context;

    private boolean useWebSocket = false;

    public LemonServer() {

    }

    /**
     * 再次确认需要的参数不为空
     */
    private void preStart() {
        if (ObjectUtil.isNull(bossGroupSize)) {
            bossGroupSize = 2;
            LOGGER.info("[LemonServer] user Default bossGroupSize:{} ", bossGroupSize);
        }
        if (ObjectUtil.isNull(workGroupSize)) {
            workGroupSize = 4;
            LOGGER.info("[LemonServer] user Default workGroupSize:{} ", workGroupSize);
        }
        if (ObjectUtil.isNull(port)) {
            port = 80;
            LOGGER.info("[LemonServer] user Default port:{} ", port);
        }
        String value = PropsUtil.getInstance().getValue(String.class, "server.websocket.endpointPackage");
        if (StrUtil.isNotBlank(value)) {
            String webSocketEndpointPackage = value;
            this.useWebSocket = true;
            LOGGER.info("[LemonServer] user websocket ", webSocketEndpointPackage);
            EndPointHelper.scan(value);
        }
    }

    @Override
    public void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(bossGroupSize, new DefaultThreadFactory("boss", true));
        EventLoopGroup workerGroup = new NioEventLoopGroup(workGroupSize, new DefaultThreadFactory("worker", true));
        try {
            long start = System.currentTimeMillis();
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
//             .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new NettyHttpServerInitializer(null, useWebSocket));

            ChannelFuture future = b.bind(port).sync();
            long cost = System.currentTimeMillis() - start;
            LOGGER.info("[LemonServer] Startup at port:{} cost:{}[ms]", port, cost);

            // 等待服务端Socket关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("[LemonServer] InterruptedException:", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void afterPropsSet() {
        LOGGER.info("[LemonServer] Starting up....");
        preStart();
        start();
    }

    @Override
    public int getPort() {
        return port;
    }
}