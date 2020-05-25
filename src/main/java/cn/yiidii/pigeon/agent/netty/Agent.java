package cn.yiidii.pigeon.agent.netty;

import io.netty.channel.ChannelHandlerContext;
import io.swagger.annotations.Api;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Date;

/**
 * Agent实体类，包含agent的相关信息
 */
@Data
@AllArgsConstructor
public class Agent {
    private String host;
    private ChannelHandlerContext ctx;
    private Date startTime;
}
