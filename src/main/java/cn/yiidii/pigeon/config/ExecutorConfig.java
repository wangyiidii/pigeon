package cn.yiidii.pigeon.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author yiidii Wang
 * @desc 线程池配置
 */
@Configuration
@EnableAsync
public class ExecutorConfig {

    /**
     * 采集和分析线程的定时线程池（一般就两个线程）
     *
     * @return
     */
    @Bean("collectionScheduleExcutor")
    public Executor collectionScheduleExcutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("pool-collectionScheduleExcutor-");
        executor.setMaxPoolSize(5);
        executor.setCorePoolSize(2);
        executor.setQueueCapacity(0);
        executor.setRejectedExecutionHandler(new MyTimerCollectionRejectedExecutionHandler());
        return executor;
    }

    /**
     * CollectProxy线程的线程池
     *
     * @return
     */
    @Bean("timerCollectExecutor")
    public Executor timerCollectExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setThreadNamePrefix("pool-timerCollectExecutor-");
        executor.setMaxPoolSize(1);
        executor.setCorePoolSize(1);
        executor.setQueueCapacity(0);
        executor.setRejectedExecutionHandler(new MyTimerCollectionRejectedExecutionHandler());
        return executor;
    }

    /**
     * @author yiidii Wang
     * @desc 采集器线程池拒绝策略
     */
    private static final class MyTimerCollectionRejectedExecutionHandler implements RejectedExecutionHandler {
        @Override
        public void rejectedExecution(Runnable r, ThreadPoolExecutor executor) {
            // 启用新的线程
            try {
                final Thread t = new Thread(r, "Temporary task executor");
                t.start();
            } catch (Throwable e) {
                throw new RejectedExecutionException(
                        "Failed to start a new thread", e);
            }
        }
    }
}
