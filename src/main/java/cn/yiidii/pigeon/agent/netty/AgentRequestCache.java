package cn.yiidii.pigeon.agent.netty;

import cn.yiidii.pigeon.agent.netty.dto.AgentRequest;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 采集结果缓存
 */
public class AgentRequestCache {

    private static Map<String, AgentRequest> agentRequestMap = new ConcurrentHashMap<>();

    public static void addAgentRequest(AgentRequest request) {
        agentRequestMap.put(request.getId(), request);
    }

    public static AgentRequest getAndRmRequest(String id) {
        AgentRequest request = null;
        if (agentRequestMap.containsKey(id)) {
            request = agentRequestMap.get(id);
            agentRequestMap.remove(id);
        }
        return request;
    }
}
