package cn.yiidii.pigeon.quratz.service.impl;

import cn.yiidii.pigeon.base.exception.ServiceException;
import cn.yiidii.pigeon.quratz.entity.Job;
import cn.yiidii.pigeon.quratz.entity.JobParam;
import cn.yiidii.pigeon.quratz.mapper.JobMapper;
import cn.yiidii.pigeon.quratz.mapper.JobParamMapper;
import cn.yiidii.pigeon.quratz.service.IJobService;
import cn.yiidii.pigeon.quratz.service.dto.JobDTO;
import cn.yiidii.pigeon.shiro.entity.User;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class JobService implements IJobService {

    @Autowired
    private JobMapper jobMapper;
    @Autowired
    private JobParamMapper jobParamMapper;

    @Override
    public Integer insertJob(Job job) {
        List<Job> tasks = jobMapper.selectList(new QueryWrapper<Job>().eq("jobName", job.getJobName()));
        if (Objects.isNull(tasks) && tasks.size() > 0) {
            throw new ServiceException("任务名称已存在");
        }
        User user = (User) SecurityUtils.getSubject().getPrincipal();
        job.setCreateTime(new Date());
        job.setUid(user.getId());
        Integer row = jobMapper.insert(job);
        if (row != 1) {
            throw new ServiceException("添加任务发生异常");
        }
        return row;
    }

    @Override
    public JobDTO queryJobById(Integer id) {
        JobDTO jobDTO = new JobDTO();
        Job job = jobMapper.selectById(id);
        BeanUtils.copyProperties(job, jobDTO);
        return jobDTO;
    }

    @Override
    public List<JobDTO> queryALlJobByUid(Integer uid) {
        List<Job> jobs = jobMapper.selectList(new QueryWrapper<Job>().eq("uid", uid));
        List<JobDTO> jobDTOs = new ArrayList<>();
        jobs.forEach(job -> {
            JobDTO jobDTO = new JobDTO();
            BeanUtils.copyProperties(job, jobDTO);
            assignParam4JobDTO(job, jobDTO);
            jobDTOs.add(jobDTO);
        });
        return jobDTOs;
    }

    @Override
    public List<JobDTO> queryALlJob() {
        List<Job> jobs = jobMapper.selectList(null);
        List<JobDTO> jobDTOs = new ArrayList<>();
        jobs.forEach(job -> {
            JobDTO jobDTO = new JobDTO();
            BeanUtils.copyProperties(job, jobDTO);
            assignParam4JobDTO(job, jobDTO);
            jobDTOs.add(jobDTO);
        });
        return jobDTOs;
    }

    @Override
    public void updateJob(JobDTO jobDTO) {
        Job job = new Job();
        BeanUtils.copyProperties(jobDTO, job);
        jobMapper.updateById(job);
        Map<String, Object> paramMap = jobDTO.getParams();
        if (!Objects.isNull(paramMap) && paramMap.size() > 0) {
            paramMap.forEach((k, v) -> {
                JobParam jobParam = new JobParam(job.getId(), k, String.valueOf(v));
                jobParamMapper.delete(new QueryWrapper<JobParam>().eq("jid", job.getId()));
                jobParamMapper.insert(jobParam);
            });
        }
    }

    @Override
    public void deleteJobById(Integer jid) {
        jobMapper.deleteById(jid);
        jobParamMapper.delete(new QueryWrapper<JobParam>().eq("jid", jid));
    }

    private void assignParam4JobDTO(Job job, JobDTO jobDTO) {
        List<JobParam> jobParams = jobParamMapper.selectList(new QueryWrapper<JobParam>().eq("jid", job.getId()));
        Map<String, Object> paramMap = new HashMap<>();
        jobParams.forEach(jobParam -> {
            paramMap.put(jobParam.getName(), jobParam.getValue());
        });
        jobDTO.setParams(paramMap);
    }
}
