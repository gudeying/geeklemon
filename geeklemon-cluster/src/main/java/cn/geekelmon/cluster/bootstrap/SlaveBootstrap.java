package cn.geekelmon.cluster.bootstrap;

import cn.geekelmon.cluster.client.ZookeeperClient;
import cn.geekelmon.cluster.node.ClusterRootConfig;
import cn.geekelmon.cluster.node.ServerServiceNode;
import cn.geekelmon.cluster.node.ServiceNode;
import cn.geekelmon.cluster.register.ServiceRegister;
import cn.geekelmon.cluster.register.ZookeeperServiceRegister;
import cn.geeklemon.core.bean.factory.InitializingBean;
import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Value;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.server.LemonServerInterFace;
import cn.geeklemon.server.NettyHttpServerInitializer;
import cn.hutool.core.lang.Assert;
import cn.hutool.core.util.NetUtil;
import cn.hutool.core.util.ObjectUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.apache.curator.framework.CuratorFramework;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 11:13
 * Modified by : kavingu
 */
public class SlaveBootstrap implements LemonServerInterFace, InitializingBean {
    private static final Logger LOGGER = LoggerFactory.getLogger(SlaveBootstrap.class);


    @Value(name = "server.bossGroupSize", defaultValue = "16")
    private Integer bossGroupSize;
    @Value(name = "server.workGroupSize", defaultValue = "32")
    private Integer workGroupSize;

    @Value(name = "server.port", defaultValue = "9096")
    private Integer port;
    /**
     * 上下文
     */
    @Autowired
    private ApplicationContext context;

    /**
     * 主节点配置
     */
    @Autowired
    private ClusterRootConfig rootConfig;

    /**
     * 注册器
     */
    private ServiceRegister serviceRegister;

    private boolean useWebSocket = false;


    /**
     * 再次确认需要的参数不为空
     */
    private void preStart() {

        CuratorFramework client = ZookeeperClient.getClient(rootConfig.getZookeeperAddress());
        serviceRegister = new ZookeeperServiceRegister(client);
        if (ObjectUtil.isNull(bossGroupSize)) {
            bossGroupSize = 2;
            LOGGER.info("[Lemon Slave Server] user Default bossGroupSize:{} ", bossGroupSize);
        }
        if (ObjectUtil.isNull(workGroupSize)) {
            workGroupSize = 4;
            LOGGER.info("[Lemon Slave Server] user Default workGroupSize:{} ", workGroupSize);
        }
        if (ObjectUtil.isNull(port)) {
            port = 9096;
            LOGGER.info("[Lemon Slave Server] user Default port:{} ", port);
        }
        Assert.notNull(serviceRegister);
        Assert.notNull(rootConfig);
    }

    @Override
    public void start() {
        System.out.println(port);
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
            LOGGER.info("[Lemon Slave Server] Startup at port:{} cost:{}[ms]", port, cost);

            // 等待服务端Socket关闭
            future.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            LOGGER.error("[Lemon Slave Server] InterruptedException:", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    @Override
    public void afterPropsSet() {
        LOGGER.info("[Lemon Slave Server] Starting up....");
        preStart();
        register();
        start();

    }

    @Override
    public int getPort() {
        return port;
    }

    private void register() {
        String host = NetUtil.getLocalhostStr();
        ServiceNode serviceNode = new ServerServiceNode(host, port, rootConfig.getPath());

        LOGGER.info("[Lemon Slave Server]注册服务：{}", serviceNode);

        serviceRegister.register(serviceNode);
    }
}
