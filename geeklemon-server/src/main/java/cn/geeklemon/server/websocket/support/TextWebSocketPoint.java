package cn.geeklemon.server.websocket.support;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/21 17:39
 * Modified by : kavingu
 */
public abstract class TextWebSocketPoint implements WebSocketPoint {
    @Override
    public void onMessage(ChannelHandlerContext context, WebSocketFrame frame) {
        TextWebSocketFrame webSocketFrame = (TextWebSocketFrame) frame;
        TextWebSocketFrame responseFrame = new TextWebSocketFrame(whenMessage(context, webSocketFrame.text()));
        context.writeAndFlush(responseFrame);
    }

    @Override
    public void onError(ChannelHandlerContext context, WebSocketFrame frame) {
        context.channel().close();
    }

    abstract String whenMessage(ChannelHandlerContext context, String message);
}
