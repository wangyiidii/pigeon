package cn.yiidii.pigeon.agent.netty.dto;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 客户端的请求，其实是客户端向Server返回的采集数据.
 * 需序列化并与Server的包结构一致，否则会序列化异常
 */
@Data
public class AgentRequest implements Serializable {
    private static final long serialVersionUID = -5850884254170136013L;
    private String id;
    private boolean success;
    private List<String> output;
}
