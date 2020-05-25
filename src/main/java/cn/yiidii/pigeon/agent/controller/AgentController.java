package cn.yiidii.pigeon.agent.controller;

import cn.yiidii.pigeon.agent.netty.Agent;
import cn.yiidii.pigeon.agent.netty.AgentPool;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedList;
import java.util.List;

/**
 * Agent
 */
@Api(tags = "Agent探针")
@RestController
@Slf4j
@RequestMapping("agent")
public class AgentController {

    @RequestMapping("all")
    @ApiOperation(value = "获取连接上的Agent")
    public Object getAllAgent() {
        List<Agent> agents = new LinkedList<>();
        AgentPool.getInstance().getPool().values().forEach(agent -> {
            agents.add(agent);
        });
        return agents;
    }

}
