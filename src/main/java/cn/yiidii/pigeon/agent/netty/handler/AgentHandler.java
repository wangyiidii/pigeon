package cn.yiidii.pigeon.agent.netty.handler;

import cn.yiidii.pigeon.agent.netty.Agent;
import cn.yiidii.pigeon.agent.netty.AgentPool;
import cn.yiidii.pigeon.agent.netty.AgentRequestCache;
import cn.yiidii.pigeon.agent.netty.dto.AgentRequest;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.Date;

@Slf4j
public class AgentHandler extends SimpleChannelInboundHandler<AgentRequest> {
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, AgentRequest request)
            throws Exception {
        //放入采集结果的缓存
        AgentRequestCache.addAgentRequest(request);
    }

    /**
     * 在channel被启用的时候触发
     * 封装成Agent并放入AgentPool,后续只需在pool里取出ctx，调用相关方法(如：writeAndFlush())即可
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        String clientHost = getHost(ctx);
        Agent agent = new Agent(clientHost, ctx, new Date());
        AgentPool.getInstance().addAgent(agent);
        log.info("Agent whoes host is {} has connected.", clientHost);
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.warn("Agent whoes host is {} disconnected in channelInactive.", getHost(ctx));
        AgentPool pool = AgentPool.getInstance();
        pool.removeAgent(getHost(ctx));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.warn("Agent whoes host is {} disconnected in exceptionCaught.e: {}", getHost(ctx), cause.getCause());
        AgentPool.getInstance().removeAgent(getHost(ctx));
    }

    /**
     * 从ctx获取host
     *
     * @param ctx
     * @return
     */
    private String getHost(ChannelHandlerContext ctx) {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        return socketAddress.getAddress().getHostAddress();
    }
}
