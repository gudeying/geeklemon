package cn.geekelmon.example.ioc.websocket;

import cn.geeklemon.server.websocket.annotation.WebSocketEventHandler;
import cn.geeklemon.server.websocket.context.WebSocketEvent;
import io.netty.channel.Channel;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;


/**
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/22 12:56
 * Modified by : kavingu
 */
@cn.geeklemon.server.websocket.annotation.ServerEndPoint
public class ServerEndPointTest {

    @WebSocketEventHandler
    public void message(WebSocketFrame frame, Channel channel) {
        if (frame instanceof TextWebSocketFrame) {
            TextWebSocketFrame textWebSocketFrame = (TextWebSocketFrame) frame;
            System.out.println("收到消息：");
            String text = textWebSocketFrame.text();
            System.out.println(text);
            TextWebSocketFrame response = new TextWebSocketFrame("收到消息：" + text);
            channel.writeAndFlush(response);

        }
        if (frame instanceof BinaryWebSocketFrame) {
            BinaryWebSocketFrame socketFrame = (BinaryWebSocketFrame) frame;

        }
        System.out.println(frame.toString());
    }

    @WebSocketEventHandler(WebSocketEvent.ON_OPEN)
    public void onOpen(Channel channel) {
        System.out.println("socket 连接");
        System.out.println(channel);
    }

}
