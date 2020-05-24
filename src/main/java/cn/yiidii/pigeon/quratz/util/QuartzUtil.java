package cn.yiidii.pigeon.quratz.util;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class QuartzUtil {

    @Autowired
//    @Qualifier("scheduler")
    private Scheduler scheduler;

    /**
     * 添加Cron任务
     *
     * @param jobName        job名 称
     * @param jobGroup       job组
     * @param cronExpression cron表达式
     * @param extraParam     执行参数
     * @param jobClass       执行类class
     * @throws SchedulerException
     */
    public void addCronJob(String jobName, String jobGroup, String cronExpression, Map<String, Object> extraParam, Class<? extends Job> jobClass) throws SchedulerException {
        JobDetail jobDetail = JobBuilder
                .newJob(jobClass)
                .withIdentity(jobName, jobGroup)
                .build();

        if (extraParam != null) {
            jobDetail.getJobDataMap().putAll(extraParam);
        }
        CronTrigger cronTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity(jobName, jobGroup)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionFireAndProceed())
                .build();
        scheduler.scheduleJob(jobDetail, cronTrigger);
    }

    /**
     * 修改cron 任务
     *
     * @param jobName        job名称
     * @param jobGroup       job组
     * @param cronExpression cron表达式
     * @param params         执行参数
     * @throws SchedulerException
     */
    public void modifyCronJob(String jobName, String jobGroup, String cronExpression, Map<String, Object> params) throws SchedulerException {
        if (!CronExpression.isValidExpression(cronExpression)) {
            return;
        }
        TriggerKey triggerKey = TriggerKey.triggerKey(jobName, jobGroup);
        JobKey jobKey = new JobKey(jobName, jobGroup);
        if (scheduler.checkExists(jobKey) && scheduler.checkExists(triggerKey)) {
            CronTrigger trigger = (CronTrigger) scheduler.getTrigger(triggerKey);
            //表达式调度构建器,不立即执行
            CronScheduleBuilder scheduleBuilder = CronScheduleBuilder.cronSchedule(cronExpression).withMisfireHandlingInstructionDoNothing();
            //按新的cronExpression表达式重新构建trigger
            trigger = trigger.getTriggerBuilder().withIdentity(triggerKey)
                    .withSchedule(scheduleBuilder).build();
            //修改参数
            trigger.getJobDataMap().putAll(params);
            //按新的trigger重新设置job执行
            scheduler.rescheduleJob(triggerKey, trigger);
        } else {
            log.warn("未找到相关任务");
        }
    }

    /**
     * 获取Job状态
     *
     * @param jobName  job名称
     * @param jobGroup job组
     * @return
     * @throws SchedulerException
     */
    public String getJobState(String jobName, String jobGroup) throws SchedulerException {
        TriggerKey triggerKey = new TriggerKey(jobName, jobGroup);
        return scheduler.getTriggerState(triggerKey).name();
    }

    /**
     * 暂停所有任务
     *
     * @throws SchedulerException
     */
    public void pauseAllJob() throws SchedulerException {
        scheduler.pauseAll();
    }

    /**
     * 暂停单个任务
     *
     * @param jobName  job名称
     * @param jobGroup job组
     * @return
     * @throws SchedulerException
     */
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

    /**
     * 恢复所有任务
     *
     * @throws SchedulerException
     */
    public void resumeAllJob() throws SchedulerException {
        scheduler.resumeAll();
    }

    /**
     * 恢复单个任务
     *
     * @param jobName  job名称
     * @param jobGroup job组
     * @throws SchedulerException
     */
    public void resumeJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (!Objects.isNull(jobDetail)) {
            scheduler.resumeJob(jobKey);
        }
    }

    /**
     * 删除单个任务
     *
     * @param jobName  job名称
     * @param jobGroup job组
     * @throws SchedulerException
     */
    public void deleteJob(String jobName, String jobGroup) throws SchedulerException {
        JobKey jobKey = new JobKey(jobName, jobGroup);
        JobDetail jobDetail = scheduler.getJobDetail(jobKey);
        if (jobDetail == null) {
            log.error("jobDetail is null");
        } else if (!scheduler.checkExists(jobKey)) {
            log.error("jobKey is not exists");
        } else {
            scheduler.deleteJob(jobKey);
        }
    }

}