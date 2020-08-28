package cn.yiidii.pigeon.apptask;

import cn.yiidii.pigeon.agent.netty.server.Server;
import cn.yiidii.pigeon.cmdb.codefine.CODefineLoader;
import cn.yiidii.pigeon.collection.collection.CollectionExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(value = 2)
@Slf4j
public class LaunchCMDB implements ApplicationRunner {
    @Override
    public void run(ApplicationArguments args) {
        //cmdb
        long start = System.currentTimeMillis();
        CODefineLoader.init();
        long timeTake = System.currentTimeMillis() - start;
        log.info("CMDB model init success.Take {} ms", timeTake);


        //agent
        start = System.currentTimeMillis();
        try {
            Server.main(null);
        } catch (InterruptedException e) {
            log.error("agent server start failed.e: {}", e.toString());
        }
        timeTake = System.currentTimeMillis() - start;
        log.info("agent server start success.Take {} ms", timeTake);
    }
}
