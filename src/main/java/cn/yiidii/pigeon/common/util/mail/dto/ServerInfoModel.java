package cn.yiidii.pigeon.common.util.mail.dto;

import cn.yiidii.pigeon.common.util.ServerInfo;
import lombok.Data;

@Data
public class ServerInfoModel {
    private String title;
    private String mainHeader;
    private double cpuUtil;
    private double memUtil;
}
