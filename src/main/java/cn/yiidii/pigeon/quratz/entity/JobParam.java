package cn.yiidii.pigeon.quratz.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@TableName("job_param")
public class JobParam {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private Integer jid;
    private String name;
    private String value;

    public JobParam(Integer jid, String name, String value) {
        this.jid = jid;
        this.name = name;
        this.value = value;
    }
}
