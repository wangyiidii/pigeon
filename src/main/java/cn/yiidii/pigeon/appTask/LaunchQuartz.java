package cn.yiidii.pigeon.appTask;

import cn.yiidii.pigeon.quratz.entity.JobType;
import cn.yiidii.pigeon.quratz.service.dto.JobDTO;
import cn.yiidii.pigeon.quratz.service.impl.JobService;
import cn.yiidii.pigeon.quratz.util.JobTypeMgr;
import cn.yiidii.pigeon.quratz.util.QuartzUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
@Order(value = 1)
@Slf4j
public class LaunchQuartz implements ApplicationRunner {

    @Autowired
    private QuartzUtil quartzUtil;
    @Autowired
    private JobService quartzService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
//            addAllJob();
//            quartzUtil.resumeAllJob();
        } catch (Exception e) {
            log.info("Quratz task started failed.");
            e.printStackTrace();
            return;
        }
        log.info("Quratz task started success.");
    }

    private void addAllJob() {

        List<JobDTO> jobDTOS = quartzService.queryALlJob();
        jobDTOS.forEach(jobDTO -> {
            try {
                Integer type = jobDTO.getType();
                Map<Integer, JobType> jobTypeMap = JobTypeMgr.getInstance();
                if (!jobTypeMap.containsKey(type)) {
                    log.warn("type of {} is not in jobTypeDefines.type: {}.", jobDTO.getJobName(), type);
                    return;
                }
                JobType jobType = jobTypeMap.get(type);
                Class clazz = Class.forName(jobType.getClassName());
                quartzUtil.addCronJob(jobDTO.getJobName(), jobDTO.getJobGroup(), jobDTO.getCronExpression(), jobDTO.getParams(), clazz);
            } catch (Exception e) {
                log.warn("launch job of {} failed. Ex: {}.", jobDTO.getJobName(), e.toString());
            }
        });
    }
}
