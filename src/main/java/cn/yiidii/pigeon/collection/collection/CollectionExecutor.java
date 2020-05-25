package cn.yiidii.pigeon.collection.collection;

import cn.yiidii.pigeon.cmdb.codefine.DefineProxy;
import cn.yiidii.pigeon.cmdb.dto.IndicatorValue;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.entity.Res;
import cn.yiidii.pigeon.cmdb.service.ICMDBService;
import cn.yiidii.pigeon.cmdb.service.impl.CMDBService;
import cn.yiidii.pigeon.collection.collector.ICollector;
import cn.yiidii.pigeon.collection.perf.PerfHelper;
import cn.yiidii.pigeon.common.util.server.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.concurrent.*;

@Slf4j
public class CollectionExecutor {

    //获取即将被采集的指标（单一线程）
    public static ScheduledExecutorService singleCollectScheduledExecutor;
    //分析futures（单一线程）
    public static ScheduledExecutorService singleAnalyzeFuturesScheduledExecutor;
    //采集线程池, 默认100个线程
    private static ExecutorService timerCollectExecutor;

    private static Map<Future<IndicatorValue>, CollectorProxy> futures = new ConcurrentHashMap<>();
    private static LinkedBlockingDeque<Future<IndicatorValue>> queue = new LinkedBlockingDeque<>(100);


    static {
        singleCollectScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
        timerCollectExecutor = Executors.newFixedThreadPool(100);
        singleAnalyzeFuturesScheduledExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    public static void startTimerCollect() {
        log.info("start TimerCollect thread...");
        singleCollectScheduledExecutor.scheduleAtFixedRate(new Thread("timerCollect") {
            public void run() {
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
        }, 0, 500, TimeUnit.MILLISECONDS);//立即执行，5秒间隔
    }

    public static void startAnalyzeFutures() {
        log.info("start analyzeFutures thread...");
        singleAnalyzeFuturesScheduledExecutor.scheduleAtFixedRate(new Thread() {
            public void run() {
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
        }, 0, 500, TimeUnit.MILLISECONDS);//立即执行，3秒间隔
    }


    public static Map<String, String> getTimerCollectExecutorStatus() {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) timerCollectExecutor;
        Map<String, String> executorInfo = new HashMap<>(3);
        executorInfo.put("pool size ", String.valueOf(tpe.getPoolSize()));
        executorInfo.put("queue size ", String.valueOf(tpe.getQueue().size()));
        executorInfo.put("active count ", String.valueOf(tpe.getActiveCount()));
        return executorInfo;
    }

    public static Map<String, String> getAnalizeExecutorStatus() {
        ThreadPoolExecutor tpe = (ThreadPoolExecutor) singleAnalyzeFuturesScheduledExecutor;
        Map<String, String> executorInfo = new HashMap<>(3);
        executorInfo.put("pool size ", String.valueOf(tpe.getPoolSize()));
        executorInfo.put("queue size ", String.valueOf(tpe.getQueue().size()));
        executorInfo.put("active count ", String.valueOf(tpe.getActiveCount()));
        return executorInfo;
    }

    private static IndicatorValue getTimeoutIndicatorValue(CollectorProxy proxy) {
        IndicatorValue iv = new IndicatorValue();
        iv.setFailureResult("采集超时");
        long currMS = System.currentTimeMillis();
        iv.addValue("time", currMS);
        iv.addValue("private.timeConsumed", currMS - proxy.getStartTime());
        return iv;
    }

}
