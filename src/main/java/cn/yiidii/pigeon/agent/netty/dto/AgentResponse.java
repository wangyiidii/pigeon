package cn.yiidii.pigeon.agent.netty.dto;

import lombok.Data;

import java.io.Serializable;

/**
 * Srever端的响应，其实是Server向客户端下发的脚本或者采集命令.
 * 需序列化并与Server的包结构一致，否则会序列化异常
 */
@Data
public class AgentResponse implements Serializable {

    private static final long serialVersionUID = -6882800277402209970L;

    private String id;
    private String cmdType;
    private String fileName;
    private byte[] attachment;
    private String[] invokeParam;

}
