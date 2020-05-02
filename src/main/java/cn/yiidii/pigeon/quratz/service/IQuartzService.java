package cn.yiidii.pigeon.quratz.service;


import cn.yiidii.pigeon.quratz.entity.QuartzTask;

import java.util.List;

public interface IQuartzService {
    Integer insertQuartzTask(QuartzTask quartzTask);

    QuartzTask queryQuartTaskzById(Integer id);

    List<QuartzTask> queryALlQuartzTaskByUid(Integer uid);

    List<QuartzTask> queryALlQuartzTask();

    Integer updateQuartzTask(QuartzTask quartzTask);

    Integer deleteQuartzTaskById(Integer id);
}
