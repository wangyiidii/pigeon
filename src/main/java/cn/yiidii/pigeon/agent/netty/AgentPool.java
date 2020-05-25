package cn.yiidii.pigeon.agent.netty;


import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Agent 连接池
 */
@Slf4j
public class AgentPool {
    /**
     * 单例
     */
    private static AgentPool instance = new AgentPool();
    /**
     * agent连接池 <host,agent>
     */
    private static Map<String, Agent> pool = new ConcurrentHashMap<>();

    private AgentPool() {
    }

    public static AgentPool getInstance() {
        return instance;
    }

    public Map<String, Agent> getPool() {
        return pool;
    }

    public Agent getAgent(String host) {
        return pool.get(host);
    }

    public void addAgent(Agent agent) {
        if (Objects.isNull(agent)) {
            return;
        }
        String host = agent.getHost();
        if (pool.containsKey(host)) {
            log.warn("found a conflict agent whose host is {} started at {}", host, agent.getStartTime());
        }
        pool.put(host, agent);
    }

    public void removeAgent(String host) {
        Agent agent = pool.get(host);
        if (!Objects.isNull(agent)) {
            pool.remove(host);
        }
    }
}
