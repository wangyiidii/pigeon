package cn.yiidii.pigeon.quratz.util;

import cn.yiidii.pigeon.quratz.entity.JobType;
import cn.yiidii.pigeon.quratz.entity.QuartzTask;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.time.DateUtils;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Set;

@Service
@Slf4j
public class JobUtil {

    @Autowired
//    @Qualifier("scheduler")
    private Scheduler scheduler;

    /**
     * 新建一个任务
     */
    public String addJob(QuartzTask quartzTask) throws Exception {

        if (!CronExpression.isValidExpression(quartzTask.getCronExpression())) {
            return "Illegal cron expression";   //表达式格式不正确
        }
        dispatchJob(quartzTask);
        return "success";
    }

    /**
     * 获取Job状态
     *
     * @param jobName
     * @param jobGroup
     * @return
     * @throws SchedulerException
     */
    public String getJobState(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(jobName, jobGroup);
        return scheduler.getTriggerState(triggerKey).name();
    }

    //暂停所有任务
    public void pauseAllJob() throws SchedulerException {
        scheduler.pauseAll();
    }

    //暂停任务
    public String pauseJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return "fail";
        } else {
            scheduler.pauseJob(jobKey);
            return "success";
        }

    }

    //恢复所有任务
    public void resumeAllJob() throws SchedulerException {
        scheduler.resumeAll();
    }

    // 恢复某个任务
    public String resumeJob(String jobName, String jobGroup) throws SchedulerException {

        JobKey jobKey = new JobKey(jobName, jobGroup);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return "fail";
        } else {
            scheduler.resumeJob(jobKey);
            return "success";
        }
    }

    //删除某个任务
    public String deleteJob(QuartzTask QuartzTask) throws SchedulerException {
        JobKey jobKey = new JobKey(QuartzTask.getJobName(), QuartzTask.getJobGroup());
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            return "jobDetail is null";
        } else if (!scheduler.checkExists(jobKey)) {
            return "jobKey is not exists";
        } else {
            scheduler.deleteJob(jobKey);
            return "success";
        }

    }

    //修改任务
    public String modifyJob(QuartzTask QuartzTask) throws SchedulerException {
        if (!CronExpression.isValidExpression(QuartzTask.getCronExpression())) {
            return "Illegal cron expression";
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(QuartzTask.getJobName(), QuartzTask.getJobGroup());
        JobKey jobKey = new JobKey(QuartzTask.getJobName(), QuartzTask.getJobGroup());
        if (scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey)) {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //表达式调度构建器,不立即执行
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(QuartzTask.getCronExpression()).withMisfireHandlingInstructionDoNothing();
            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
                    .withSchedule(scheduleBuilder).build();
            //修改参数
            if (!trigger.getJobDataMap().get("invokeParam").equals(QuartzTask.getInvokeParam())) {
                trigger.getJobDataMap().put("invokeParam", QuartzTask.getInvokeParam());
            }
            //按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
            return "success";
        } else {
            return "job or trigger not exists";
        }
    }

    private void dispatchJob(QuartzTask quartzTask) {
        Set<JobType> types = JobTypeMgr.getInstance();
        for (JobType jobType : types) {
            JobDetail jobDetail = null;
            if (quartzTask.getType().equals(jobType.getType())) {
                try {
                    Date date = DateUtils.parseDate(quartzTask.getStartTime(), "yyyy-MM-dd HH:mm:ss");
                    Class<Job> clazz = (Class<Job>) Class.forName("cn.yiidii.pigeon.quratz.job.LTSign");
                    jobDetail = JobBuilder.newJob(clazz).withIdentity(quartzTask.getJobName(), quartzTask.getJobGroup()).build();
                    //表达式调度构建器(即任务执行的时间,不立即执行)
                    CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(quartzTask.getCronExpression()).withMisfireHandlingInstructionFireAndProceed();

                    //按新的cronExpression表达式构建一个新的trigger
                    CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(quartzTask.getJobName(), quartzTask.getJobGroup()).startAt(date)
                            .withSchedule(scheduleBuilder).build();

                    //传递参数
                    if (quartzTask.getInvokeParam() != null && !"".equals(quartzTask.getInvokeParam())) {
                        trigger.getJobDataMap().put("invokeParam", quartzTask.getInvokeParam());
                    }
                    scheduler.scheduleJob(jobDetail, trigger);
                } catch (Exception e) {
                    log.error("dispatchJob occured exception: " + e.toString() + "; task detail: " + JSONObject.toJSON(quartzTask.toString()));
                }
            }
        }
    }

}