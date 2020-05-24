package cn.yiidii.pigeon.quratz.entity;


import lombok.Data;

import java.util.List;

@Data
public class JobType {
    private Integer type;//唯一，对应quartzTask的type
    private String alias;
    private String desc;
    private String className;
    private List<JobParam> jobParams;
}
