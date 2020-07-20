package cn.geeklemon.server.handler;

import cn.geeklemon.core.util.PropsUtil;
import cn.geeklemon.server.TemporaryDataHolder;
import cn.hutool.core.util.ObjectUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.FullHttpRequest;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/29 10:26 Modified by : kavingu
 */
public class DataStoreHandler extends ChannelInboundHandlerAdapter {

	private final static Logger LOGGER = LoggerFactory.getLogger(DataStoreHandler.class);

	/**
	 * 由于加了HttpObjectAggregator，所以可以接收到fullHttpRequest
	 *
	 * @param ctx
	 * @param msg
	 */
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) {
		if (msg instanceof FullHttpRequest) {
			FullHttpRequest request = (FullHttpRequest) msg;
			String ico = "/favicon.ico";
			String value = PropsUtil.getInstance().getValue(String.class, "favicon.ico");
			if (ObjectUtil.isNotNull(value)) {
				ico = value;
			}
			TemporaryDataHolder.removeAll();
			// 不是图标请求，其实有resourceHandler，已经被过滤了。
			// 将request和context存储到ThreadLocal中去，便于后期在其他地方获取并使用
			TemporaryDataHolder.storeHttpRequest(request);
			TemporaryDataHolder.storeFullHttpRequest(request);
			TemporaryDataHolder.storeContext(ctx);
			TemporaryDataHolder.storeCookies(request);

		}
		/*
		 * 提交给下一个ChannelHandler去处理
		 * 并且不需要调用ReferenceCountUtil.release(msg);来释放引用计数
		 */
		ctx.fireChannelRead(msg);
	}

	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) {
		ctx.flush();
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		ctx.close();
		TemporaryDataHolder.removeAll();
		if (!(cause instanceof IOException)) {
			LOGGER.error("ctx close,cause:", cause);
		}
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		TemporaryDataHolder.removeAll();
		super.channelInactive(ctx);
	}
}
