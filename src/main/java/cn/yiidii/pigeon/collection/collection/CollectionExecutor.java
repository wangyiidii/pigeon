package cn.yiidii.pigeon.collection.collection;

import cn.yiidii.pigeon.cmdb.codefine.DefineProxy;
import cn.yiidii.pigeon.cmdb.dto.IndicatorValue;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.entity.Res;
import cn.yiidii.pigeon.cmdb.service.ICMDBService;
import cn.yiidii.pigeon.cmdb.service.impl.CMDBService;
import cn.yiidii.pigeon.collection.CollectionConstant;
import cn.yiidii.pigeon.collection.collector.ICollector;
import cn.yiidii.pigeon.collection.perf.PerfHelper;
import cn.yiidii.pigeon.common.util.server.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class CollectionExecutor {

    /**
     * 采集的线程池
     */
    @Autowired
    @Resource(name = "timerCollectExecutor")
    private ThreadPoolTaskExecutor timerCollectExecutor;

    private static Map<Future<IndicatorValue>, CollectorProxy> futures = new ConcurrentHashMap<>();
    private static LinkedBlockingDeque<Future<IndicatorValue>> queue = new LinkedBlockingDeque<>(1000);

    /**
     * 每一秒执行一次
     */
    @Async("collectionScheduleExcutor")
    @Scheduled(cron = "0/1 * * * * ?")
    public void startTimerCollect() {
        String name = Thread.currentThread().getName();
        Thread.currentThread().setName(name.replaceAll("-\\d", "-".concat("timerCollectThread")));
        ICMDBService cmdbService = SpringContextUtil.getBean(CMDBService.class);
        List<Indicator> indicators = cmdbService.getIndicatorsWillBeCollect();
        indicators.forEach(indicator -> {
            //获取指标采集器，并判断是否可用
            String indCollector = DefineProxy.getIndDefineMap().get(indicator.getDefName()).getCollector();
            if (StringUtils.isBlank(indCollector)) {
                log.info("{} collector cannot be used", indicator.getName());
                return;
            }
            ICollector collector = null;
            boolean exFlag = false;
            try {
                Class collectorClass = Class.forName(indCollector);
                collector = (ICollector) collectorClass.newInstance();
            } catch (Exception e) {
                exFlag = true;
            }
            if (exFlag || Objects.isNull(collector)) {
                log.error("collector of {} cannot be worked.name={}", indicator.getDefName(), indicator.getName());
                return;
            }
            //可用的线程会用CollectorProxy执行采集
            Res res = cmdbService.getResByIndicator(indicator.getName());
            CollectorProxy proxy = new CollectorProxy(res, indicator, collector);

            //
            indicator.setLastCollectTime(new Date(proxy.getStartTime()));
            cmdbService.updateIndicator4Collect(indicator);

            Future<IndicatorValue> future = timerCollectExecutor.submit(proxy);
            futures.put(future, proxy);//结果和线程放入缓存，方便对采集结果的分析及存储
            queue.offer(future);
        });
    }

    /**
     * 每一秒执行一次
     */
    @Async("collectionScheduleExcutor")
    @Scheduled(cron = "0/1 * * * * ?")
    public void startAnalyzeFutures() {
        String name = Thread.currentThread().getName();
        Thread.currentThread().setName(name.replaceAll("-\\d", "-".concat("analyzeFuturesThread")));
        ICMDBService cmdbService = SpringContextUtil.getBean(CMDBService.class);
        if (queue.size() <= 0) {
            return;
        }
        Future<IndicatorValue> future = queue.poll();
        CollectorProxy proxy = futures.get(future);
        Indicator indicator = proxy.getIndicator();
        IndicatorValue iv = null;
        try {
            iv = future.get(indicator.getTimeout(), TimeUnit.SECONDS);
        } catch (Exception e) {
            future.cancel(true);
            log.error("Collect {} timeout.", indicator.getAlias());
            iv = getTimeoutIndicatorValue(proxy);
            indicator.setDesc(iv.getStatusDesc());
            indicator.setLastCollectTime(new Date(proxy.getStartTime()));
        }

        //更新指标状态，储存性能数据
        cmdbService.updateIndicator4Collect(indicator);
        PerfHelper.savePerfValues(indicator, iv);
        futures.remove(future);
    }

    private IndicatorValue getTimeoutIndicatorValue(CollectorProxy proxy) {
        IndicatorValue iv = new IndicatorValue();
        iv.setFailureResult(CollectionConstant.COLLECT_TIMEOUT);
        long currentTimeMillis = System.currentTimeMillis();
        iv.addValue("time", currentTimeMillis);
        iv.addValue("private.timeConsumed", currentTimeMillis - proxy.getStartTime());
        return iv;
    }

}
