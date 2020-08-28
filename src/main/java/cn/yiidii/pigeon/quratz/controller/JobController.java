package cn.yiidii.pigeon.quratz.controller;

import cn.yiidii.pigeon.base.vo.Result;
import cn.yiidii.pigeon.base.vo.ResultCodeEnum;
import cn.yiidii.pigeon.quratz.entity.Job;
import cn.yiidii.pigeon.quratz.entity.JobType;
import cn.yiidii.pigeon.quratz.service.dto.JobDTO;
import cn.yiidii.pigeon.quratz.service.impl.JobService;
import cn.yiidii.pigeon.quratz.util.JobTypeMgr;
import cn.yiidii.pigeon.quratz.util.QuartzUtil;
import cn.yiidii.pigeon.shiro.entity.User;
import cn.yiidii.pigeon.shiro.util.SecurityUtil;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/job")
@Slf4j
@Api(tags = "任务调度")
public class JobController {
    @Autowired
    private QuartzUtil quartzUtil;
    @Autowired
    private JobService quartzService;
    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping(value = "/type")
    @ApiOperation(value = "获取支持的定时任务类型", notes = "")
    public Object getSetAllJobTypes() throws Exception {
        return JobTypeMgr.getInstance();
    }

    @GetMapping(value = "/all")
    @ApiOperation(value = "获取所有的定时任务", notes = "")
    public Object getJobs() throws Exception {
        User currUser = securityUtil.getCurrUser();
        List<JobDTO> jobs = quartzService.queryAllJobByUid(currUser.getId());
        return jobs;
    }

    @GetMapping(value = "/state")
    @ApiOperation(value = "获取指定ID定时任务的状态", notes = "")
    public Object getjobstate(@RequestParam(name = "id", required = true) Integer id) throws Exception {
        JobDTO job = quartzService.queryJobById(id);
        String state = quartzUtil.getJobState(job.getJobName(), job.getJobGroup());
        JSONObject jo = new JSONObject();
        jo.put("state", state);
        return Result.success(jo);
    }

    @PostMapping(value = "/addJob")
    @ApiOperation(value = "新增任务", notes = "")
    public Result addjob(@RequestBody JobDTO jobDTO) throws Exception {
        Job job = new Job();
        BeanUtils.copyProperties(jobDTO, job);
        quartzService.insertJob(job);
        log.debug("quartzTask: " + JSONObject.toJSON(job));
        Integer type = jobDTO.getType();
        Map<Integer, JobType> jobTypeMap = JobTypeMgr.getInstance();
        if (!jobTypeMap.containsKey(type)) {
            log.warn("type of {} is not in jobTypeDefines.type: {}.", jobDTO.getJobName(), type);
        }
        JobType jobType = jobTypeMap.get(type);
        Class clazz = Class.forName(jobType.getClassName());
        quartzUtil.addCronJob(jobDTO.getJobName(), jobDTO.getJobGroup(), jobDTO.getCronExpression(), jobDTO.getParams(), clazz);
        return Result.success(ResultCodeEnum.SUCCESS.getMsg());
    }

    @PostMapping(value = "/pauseJob")
    @ApiOperation(value = "暂停任务", notes = "")
    public Result pausejob(@RequestBody Integer[] jobIds) throws Exception {
        JobDTO job = null;
        if (jobIds.length > 0) {
            for (Integer id : jobIds) {
                job = quartzService.queryJobById(id);
                quartzUtil.pauseJob(job.getJobName(), job.getJobGroup());
            }
            return Result.success(ResultCodeEnum.SUCCESS.getMsg());
        } else {
            return Result.success(ResultCodeEnum.OPT_FAIL.getMsg());
        }
    }

    //恢复job
    @PostMapping(value = "/resumeJob")
    @ApiOperation(value = "恢复任务", notes = "")
    public Result resumejob(@RequestBody Integer[] jobIds) throws Exception {
        JobDTO job = null;
        if (jobIds.length > 0) {
            for (Integer id : jobIds) {
                job = quartzService.queryJobById(id);
                quartzUtil.resumeJob(job.getJobName(), job.getJobGroup());
            }
            return Result.success(ResultCodeEnum.SUCCESS.getMsg());
        } else {
            return Result.success(ResultCodeEnum.OPT_FAIL.getMsg());
        }
    }


    //删除job
    @RequestMapping(value = "/deletJob", method = RequestMethod.DELETE)
    @ApiOperation(value = "删除任务", notes = "")
    public Result deletjob(@RequestBody Integer[] quartzIds) throws Exception {
        JobDTO job = null;
        for (Integer quartzId : quartzIds) {
            job = quartzService.queryJobById(quartzId);
            quartzUtil.deleteJob(job.getJobName(), job.getJobGroup());
        }
        return Result.success(ResultCodeEnum.SUCCESS.getMsg());
    }

    //修改
    @RequestMapping(value = "/updateJob", method = RequestMethod.PUT)
    @ApiOperation(value = "修改任务", notes = "")
    public Result modifyJob(@RequestBody JobDTO job) throws Exception {
        quartzUtil.modifyCronJob(job.getJobName(), job.getJobGroup(), job.getCronExpression(), job.getParams());
        return Result.success(ResultCodeEnum.SUCCESS.getMsg());
    }

    //暂停所有
    @PostMapping(value = "/pauseAll")
    @ApiOperation(value = "暂停所有任务", notes = "")
    public Result pauseAllJob() throws Exception {
        quartzUtil.pauseAllJob();
        return Result.success(ResultCodeEnum.SUCCESS.getMsg());
    }

    //恢复所有
    @PostMapping(value = "/repauseAll")
    @ApiOperation(value = "恢复所有任务", notes = "")
    public Result repauseAllJob() throws Exception {
        quartzUtil.resumeAllJob();
        return Result.success(ResultCodeEnum.SUCCESS.getMsg());
    }

}