package cn.yiidii.pigeon.cmdb.codefine;

import lombok.Data;

@Data
public class ParamDefine {
    private String name;
    private String alias;
    private boolean required = false;
    private String type;
    private String defaultValue = "";
}
