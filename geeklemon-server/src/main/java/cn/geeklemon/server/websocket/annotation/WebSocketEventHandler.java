package cn.geeklemon.server.websocket.annotation;

import cn.geeklemon.server.websocket.context.WebSocketEvent;
import io.netty.handler.codec.http.websocketx.WebSocketFrame;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 方法中的参数可以是uri中的参数，也可以是Channel。
 * 如果要使用Channel回传数据，必须封装为WebSocketFrame {@link WebSocketFrame}
 * {@link io.netty.handler.codec.http.websocketx.TextWebSocketFrame}
 * {@link io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame}
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface WebSocketEventHandler {
    WebSocketEvent value() default WebSocketEvent.ON_MESSAGE;
}
