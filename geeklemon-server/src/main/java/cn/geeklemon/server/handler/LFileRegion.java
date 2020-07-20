package cn.geeklemon.server.handler;

import io.netty.channel.DefaultFileRegion;

import java.nio.channels.FileChannel;

public class LFileRegion extends DefaultFileRegion {
    public LFileRegion(FileChannel file, long position, long count) {
        super(file, position, count);
    }

    @Override
    protected void deallocate() {

    }
}
