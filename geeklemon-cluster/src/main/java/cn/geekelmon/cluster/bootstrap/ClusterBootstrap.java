package cn.geekelmon.cluster.bootstrap;

import cn.geekelmon.cluster.client.ZookeeperClient;
import cn.geekelmon.cluster.context.ServiceWatcher;
import cn.geekelmon.cluster.context.ZookeeperServiceWatcher;
import cn.geekelmon.cluster.node.ClusterRootConfig;
import cn.geeklemon.core.bean.factory.InitializingBean;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Bean;
import cn.hutool.log.Log;
import cn.hutool.log.LogFactory;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;


/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 11:11
 * Modified by : kavingu
 */
public class ClusterBootstrap implements InitializingBean {
    private static final Log LOGGER = LogFactory.get();

    @Autowired
    private ClusterRootConfig rootConfig;

    private ServiceWatcher serviceWatcher;


    @Override
    public void afterPropsSet() {
        serviceWatcher = new ZookeeperServiceWatcher(ZookeeperClient.getClient(rootConfig.getZookeeperAddress()), rootConfig);
        serviceWatcher.watch();
        start();
    }


    private void start() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("boss", true));
        EventLoopGroup workerGroup = new NioEventLoopGroup(4, new DefaultThreadFactory("worker", true));
        try {
            long start = System.currentTimeMillis();
            ServerBootstrap b = new ServerBootstrap();
            b.option(ChannelOption.SO_BACKLOG, 1024);
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
//             .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new MasterServerInitializer(serviceWatcher));

            ChannelFuture future = b.bind(rootConfig.getPort()).sync();
            long cost = System.currentTimeMillis() - start;
            LOGGER.info("[MasterServer] Startup at port:{} cost:{}[ms]", rootConfig.getPort(), cost);

            // 等待服务端Socket关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("InterruptedException:", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
