package cn.yiidii.pigeon.agent.netty.server;

import cn.yiidii.pigeon.agent.netty.handler.AgentHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.TimeUnit;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();

        pipeline.addLast(new IdleStateHandler(1, 0, 0, TimeUnit.SECONDS));

        // 解码 和 编码
        pipeline.addLast("decoder", new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));
        pipeline.addLast("encoder", new ObjectEncoder());

        // 业务Handler
        pipeline.addLast("handler", new AgentHandler());
    }
}
