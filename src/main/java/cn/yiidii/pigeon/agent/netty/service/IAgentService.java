package cn.yiidii.pigeon.agent.netty.service;

import cn.yiidii.pigeon.agent.netty.Agent;
import cn.yiidii.pigeon.cmdb.entity.Indicator;

import java.util.List;

public interface IAgentService {

    /**
     * 获取所有Agent
     *
     * @return
     */
    List<Agent> getAllAgent();

    /**
     * 下发采集指标的脚本
     *
     * @param indicator
     */
    void issuedScript(Indicator indicator);

}
