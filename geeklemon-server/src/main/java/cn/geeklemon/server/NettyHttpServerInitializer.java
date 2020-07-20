package cn.geeklemon.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cn.geeklemon.server.context.WebContext;
import cn.geeklemon.server.controller.ControllerDispatcher;
import cn.geeklemon.server.handler.DataStoreHandler;
import cn.geeklemon.server.handler.FilterHandler;
import cn.geeklemon.server.handler.ResourceHandler;
import cn.geeklemon.server.response.ResponseWriter;
import cn.geeklemon.server.websocket.WebSocketServerHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * @author : Kavin Gu Project Name : geeklemon Description :
 * @version : ${VERSION} 2019/9/20 16:37 Modified by : kavingu
 */
public class NettyHttpServerInitializer extends ChannelInitializer<SocketChannel> {
    public static final Logger LOGGER = LoggerFactory.getLogger(NettyHttpServerInitializer.class);

    private WebContext webContext;
    private boolean useWebSocket;

    public NettyHttpServerInitializer(WebContext context, boolean useWebSocket) {
        this.webContext = context;
        this.useWebSocket = useWebSocket;
    }

    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline line = channel.pipeline();

        /* 解析http协议 */
        line.addLast(new HttpServerCodec());
        /* 请求聚合,后面收到的都是完整的httpRequest */
        line.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        /* 启用chunk */
        line.addLast(new ChunkedWriteHandler());

        /*--webSocket--*/
        if (useWebSocket) {
            line.addLast(new WebSocketServerHandler(webContext));
        }

        line.addLast(new DataStoreHandler());

        line.addLast(new FilterHandler(webContext));
        /*--webSocket--*/
        line.addLast(new ResourceHandler());

        line.addLast(new ControllerDispatcher(webContext));

        line.addLast(new ResponseWriter());

    }
}
