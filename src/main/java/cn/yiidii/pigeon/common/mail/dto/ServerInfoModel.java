package cn.yiidii.pigeon.common.mail.dto;

import lombok.Data;

@Data
public class ServerInfoModel {
    private String title;
    private String mainHeader;
    private double cpuUtil;
    private double memUtil;
}
