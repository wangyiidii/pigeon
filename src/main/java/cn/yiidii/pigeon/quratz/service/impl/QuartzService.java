package cn.yiidii.pigeon.quratz.service.impl;

import cn.yiidii.pigeon.base.exception.ServiceException;
import cn.yiidii.pigeon.quratz.entity.QuartzTask;
import cn.yiidii.pigeon.quratz.mapper.QuartzTaskMapper;
import cn.yiidii.pigeon.quratz.service.IQuartzService;
import cn.yiidii.pigeon.shiro.entity.User;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
public class QuartzService implements IQuartzService {

    @Autowired
    private QuartzTaskMapper quartzTaskMapper;

    @Override
    public Integer insertQuartzTask(QuartzTask quartzTask) {
        QuartzTask qt = quartzTaskMapper.queryQuartzTaskByJobName(quartzTask.getJobName());
        if (null != qt) {
            throw new ServiceException("任务名称已存在");
        }
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        quartzTask.setCreateTime(new Date());
        quartzTask.setUid(user.getId());
        Integer row = quartzTaskMapper.insert(quartzTask);
        if (row != 1) {
            throw new ServiceException("添加任务发生异常");
        }
        return row;
    }

    @Override
    public QuartzTask queryQuartTaskzById(Integer id) {
        return quartzTaskMapper.queryQuartzTaskById(id);
    }

    @Override
    public List<QuartzTask> queryALlQuartzTaskByUid(Integer uid) {
        return quartzTaskMapper.queryALlQuartzTaskByUid(uid);
    }

    @Override
    public List<QuartzTask> queryALlQuartzTask() {
        return quartzTaskMapper.queryALlQuartzTask();
    }

    @Override
    public Integer updateQuartzTask(QuartzTask quartzTask) {
        return quartzTaskMapper.update(quartzTask);
    }

    @Override
    public Integer deleteQuartzTaskById(Integer id) {
        return quartzTaskMapper.delete(id);
    }
}
