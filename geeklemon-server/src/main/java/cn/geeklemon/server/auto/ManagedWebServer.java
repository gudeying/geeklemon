package cn.geeklemon.server.auto;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geeklemon.core.context.annotation.Autowired;
import cn.geeklemon.core.context.annotation.Value;
import cn.geeklemon.core.context.support.ApplicationContext;
import cn.geeklemon.core.util.PropsUtil;
import cn.geeklemon.server.LemonServer;
import cn.geeklemon.server.LemonServerInterFace;
import cn.geeklemon.server.NettyHttpServerInitializer;
import cn.geeklemon.server.context.WebContext;
import cn.geeklemon.server.websocket.context.EndPointHelper;
import cn.hutool.core.util.StrUtil;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.util.concurrent.DefaultThreadFactory;

public class ManagedWebServer implements LemonServerInterFace {
	private WebContext webContext;
	private static final Logger LOGGER = LoggerFactory.getLogger(LemonServerInterFace.class);
	private Integer bossGroupSize = 4;
	private Integer workGroupSize = 16;
	private Integer port = 8080;

	private boolean useWebSocket = false;

	public ManagedWebServer(WebContext webContext) {
		this.webContext = webContext;
	}

	@Override
	public void start() {
		presStart();
		EventLoopGroup bossGroup = new NioEventLoopGroup(bossGroupSize, new DefaultThreadFactory("boss", true));
		EventLoopGroup workerGroup = new NioEventLoopGroup(workGroupSize, new DefaultThreadFactory("worker", true));
		try {
			long start = System.currentTimeMillis();
			ServerBootstrap b = new ServerBootstrap();
			b.option(ChannelOption.SO_BACKLOG, 1024);
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					// .handler(new LoggingHandler(LogLevel.INFO))
					.childHandler(new NettyHttpServerInitializer(webContext, useWebSocket));

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

	private void presStart() {
		bossGroupSize = webContext.getServerConfig().bossSize();
		workGroupSize = webContext.getServerConfig().workSize();
		port = webContext.getServerConfig().getPort();
		String value = PropsUtil.getInstance().getValue(String.class, "server.websocket.endpointPackage");
		if (StrUtil.isNotBlank(value)) {
			String webSocketEndpointPackage = value;
			this.useWebSocket = true;
			LOGGER.info("[LemonServer] user websocket ", webSocketEndpointPackage);
			EndPointHelper.scan(value);
		}
	}

	@Override
	public int getPort() {
		return webContext.getServerConfig().getPort();
	}
}
