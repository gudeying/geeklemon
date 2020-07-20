package cn.geekelmon.cluster.bootstrap;

import cn.geekelmon.cluster.context.ServiceWatcher;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.stream.ChunkedWriteHandler;

/**
 * 集群主服务器初始化
 *
 * @author : Kavin Gu
 * Project Name : geeklemon
 * Description :
 * @version : ${VERSION} 2019/10/12 13:35
 * Modified by : kavingu
 */
public class MasterServerInitializer extends ChannelInitializer<SocketChannel> {
    private ServiceWatcher serviceWatcher;

    public MasterServerInitializer(ServiceWatcher serviceWatcher) {
        this.serviceWatcher = serviceWatcher;
    }


    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpContentCompressor());
        pipeline.addLast(new HttpObjectAggregator(Integer.MAX_VALUE));
        pipeline.addLast(new ChunkedWriteHandler());
        pipeline.addLast(new MasterServerHandler(serviceWatcher));
    }
}
