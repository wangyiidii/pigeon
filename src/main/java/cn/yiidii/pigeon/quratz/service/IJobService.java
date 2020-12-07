package cn.yiidii.pigeon.quratz.service;


import cn.yiidii.pigeon.quratz.entity.Job;
import cn.yiidii.pigeon.quratz.service.dto.JobDTO;

import java.util.List;

public interface IJobService {
    Integer insertJob(Job job);

    JobDTO queryJobById(Integer id);

    List<JobDTO> queryAllJobByUid(Integer uid);

    List<JobDTO> queryAllJob();

    void updateJob(JobDTO job);

    void deleteJobById(Integer id);
}
