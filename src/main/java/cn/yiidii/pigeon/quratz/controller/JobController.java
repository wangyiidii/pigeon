package cn.yiidii.pigeon.quratz.controller;

import cn.yiidii.pigeon.base.exception.ServiceException;
import cn.yiidii.pigeon.base.vo.Result;
import cn.yiidii.pigeon.base.vo.ResultCodeEnum;
import cn.yiidii.pigeon.quratz.controller.vo.JobTypeVo;
import cn.yiidii.pigeon.quratz.entity.JobType;
import cn.yiidii.pigeon.quratz.entity.QuartzTask;
import cn.yiidii.pigeon.quratz.service.impl.QuartzService;
import cn.yiidii.pigeon.quratz.util.JobTypeMgr;
import cn.yiidii.pigeon.quratz.util.JobUtil;
import cn.yiidii.pigeon.shiro.entity.User;
import cn.yiidii.pigeon.shiro.util.SecurityUtil;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/job")
@Slf4j
@Api(tags = "任务调度")
public class JobController {
    @Autowired
    private JobUtil jobUtil;
    @Autowired
    private QuartzService quartzService;
    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping(value = "/type")
    @ApiOperation(value = "获取支持的定时任务类型", notes = "")
    public Object getSetAllJobTypes() throws Exception {
        Set<JobType> types = JobTypeMgr.getInstance();
        Set<JobTypeVo> typesVO = new HashSet<>();
        types.forEach(type -> {
            JobTypeVo jobTypeVo = new JobTypeVo();
            BeanUtils.copyProperties(type, jobTypeVo);
            typesVO.add(jobTypeVo);
        });
        return typesVO;
    }

    @GetMapping(value = "/all")
    @ApiOperation(value = "获取所有的定时任务", notes = "")
    public JSONArray getJobs() throws Exception {
        User currUser = securityUtil.getCurrUser();
        List<QuartzTask> tasks = quartzService.queryALlQuartzTaskByUid(currUser.getId());
        JSONArray ja = JSONArray.parseArray(JSON.toJSONString(tasks));
        for (Object o : ja) {
            JSONObject jo = (JSONObject) o;
            String jobName = jo.getString("jobName");
            String jobNGroup = jo.getString("jobGroup");
            jo.put("state", jobUtil.getJobState(jobName, jobNGroup));
        }
        return ja;
    }

    @GetMapping(value = "/state")
    @ApiOperation(value = "获取指定ID定时任务的状态", notes = "")
    public Object getjobstate(@RequestParam(name = "id", required = true) Integer id) throws Exception {
        QuartzTask quartzTask = quartzService.queryQuartTaskzById(id);
        String state = jobUtil.getJobState(quartzTask.getJobName(), quartzTask.getJobGroup());
        JSONObject jo = new JSONObject();
        jo.put("state", state);
        return Result.success(jo);
    }

    //添加一个job
    @PostMapping(value = "/addJob")
    @ApiOperation(value = "新增任务", notes = "")
    public Result addjob(QuartzTask quartzTask) throws Exception {
        quartzService.insertQuartzTask(quartzTask);
        log.debug("quartzTask: " + JSONObject.toJSON(quartzTask));
        String result = jobUtil.addJob(quartzTask);
        if (!StringUtils.equals("success", result)) {
            throw new ServiceException(result);
        }
        return Result.success(ResultCodeEnum.SUCCESS.getMsg());
    }

    //暂停job
    @PostMapping(value = "/pauseJob")
    @ApiOperation(value = "暂停任务", notes = "")
    public Result pausejob(@RequestBody Integer[] quartzIds) throws Exception {
        QuartzTask quartzTask = null;
        if (quartzIds.length > 0) {
            for (Integer quartzId : quartzIds) {
                quartzTask = quartzService.queryQuartTaskzById(quartzId);
                jobUtil.pauseJob(quartzTask.getJobName(), quartzTask.getJobGroup());
            }
            return Result.success(ResultCodeEnum.SUCCESS.getMsg());
        } else {
            return Result.success(ResultCodeEnum.OPT_FAIL.getMsg());
        }
    }

    //恢复job
    @PostMapping(value = "/resumeJob")
    @ApiOperation(value = "恢复任务", notes = "")
    public Result resumejob(@RequestBody Integer[] quartzIds) throws Exception {
        QuartzTask quartzTask = null;
        if (quartzIds.length > 0) {
            for (Integer quartzId : quartzIds) {
                quartzTask = quartzService.queryQuartTaskzById(quartzId);
                jobUtil.resumeJob(quartzTask.getJobName(), quartzTask.getJobGroup());
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
        QuartzTask quartzTask = null;
        for (Integer quartzId : quartzIds) {
            quartzTask = quartzService.queryQuartTaskzById(quartzId);
            String ret = jobUtil.deleteJob(quartzTask);
            if ("success".equals(ret)) {
                quartzService.deleteQuartzTaskById(quartzId);
            }
        }
        return Result.success(ResultCodeEnum.SUCCESS.getMsg());
    }

    //修改
    @RequestMapping(value = "/updateJob", method = RequestMethod.PUT)
    @ApiOperation(value = "修改任务", notes = "")
    public Result modifyJob(@RequestBody QuartzTask quartzTask) throws Exception {
        String ret = jobUtil.modifyJob(quartzTask);
        if ("success".equals(ret)) {
            quartzService.updateQuartzTask(quartzTask);
            return Result.success(ResultCodeEnum.SUCCESS.getMsg());
        } else {
            return Result.success(ResultCodeEnum.OPT_FAIL.getMsg());
        }
    }

    //暂停所有
    @PostMapping(value = "/pauseAll")
    @ApiOperation(value = "暂停所有任务", notes = "")
    public Result pauseAllJob() throws Exception {
        jobUtil.pauseAllJob();
        return Result.success(ResultCodeEnum.SUCCESS.getMsg());
    }

    //恢复所有
    @PostMapping(value = "/repauseAll")
    @ApiOperation(value = "恢复所有任务", notes = "")
    public Result repauseAllJob() throws Exception {
        jobUtil.resumeAllJob();
        return Result.success(ResultCodeEnum.SUCCESS.getMsg());
    }

}