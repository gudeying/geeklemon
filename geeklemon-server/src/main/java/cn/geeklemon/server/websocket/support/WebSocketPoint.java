package cn.geeklemon.server.websocket.support;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/21 17:38
 * Modified by : kavingu
 */
public interface WebSocketPoint {

    void onMessage(ChannelHandlerContext context, WebSocketFrame frame);

    void onError(ChannelHandlerContext context, WebSocketFrame frame);
}
