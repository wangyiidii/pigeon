package cn.yiidii.pigeon.quratz.mapper;

import cn.yiidii.pigeon.quratz.entity.QuartzTask;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface QuartzTaskMapper {

    QuartzTask queryQuartzTaskById(Integer quartzId);

    QuartzTask queryQuartzTaskByJobName(String jobName);

    List<QuartzTask> queryALlQuartzTaskByUid(Integer uid);

    List<QuartzTask> queryALlQuartzTask();

    Integer insert(QuartzTask quartzTask);

    Integer update(QuartzTask quartzTask);

    Integer delete(Integer quartzId);

}
