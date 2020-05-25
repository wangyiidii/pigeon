package cn.yiidii.pigeon.agent.netty.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * Agent4Pigeon Server端
 */
public class Server {
    /**
     * 服务端监听的端口地址
     */
    private static final int portNumber = 7878;

    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup);
            b.channel(NioServerSocketChannel.class);
//            b.option(ChannelOption.SO_BACKLOG,128);
//            b.option(ChannelOption.TCP_NODELAY,true);
//            b.childOption(ChannelOption.SO_KEEPALIVE, true);
            b.childHandler(new ServerInitializer());

            // 服务器绑定端口监听
            ChannelFuture f = b.bind(portNumber).sync();
            // 监听服务器关闭监听
            f.channel().closeFuture().sync();

        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
