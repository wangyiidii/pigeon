package cn.yiidii.pigeon.appTask;

import cn.yiidii.pigeon.quratz.entity.QuartzTask;
import cn.yiidii.pigeon.quratz.service.impl.QuartzService;
import cn.yiidii.pigeon.quratz.util.JobUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@Order(value = 1)
@Slf4j
public class LaunchQuartz implements ApplicationRunner {

    @Autowired
    private JobUtil jobUtil;
    @Autowired
    private QuartzService quartzService;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        try {
            addAllJob();
            jobUtil.resumeAllJob();
        } catch (Exception e) {
            log.info("Quratz task started failed.");
            e.printStackTrace();
            return;
        }
        log.info("Quratz task started success.");
    }

    private void addAllJob() {
        List<QuartzTask> tasks = quartzService.queryALlQuartzTask();
        tasks.forEach(task -> {
            try {
                jobUtil.addJob(task);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
