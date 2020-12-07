package cn.yiidii.pigeon.collection.collection;

import cn.yiidii.pigeon.cmdb.dto.IndicatorValue;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.entity.Res;
import cn.yiidii.pigeon.collection.CollectionConstant;
import cn.yiidii.pigeon.collection.collector.ICollector;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.Objects;
import java.util.concurrent.Callable;

/**
 * 采集任务线程
 */
@Data
@Slf4j
public class CollectorProxy implements Callable<IndicatorValue> {

    private Res res;
    private Indicator indicator;
    private ICollector collector;
    private long startTime;
    private long endTime;

    /**
     * 禁止外部实例
     */
    private CollectorProxy() {
    }

    public CollectorProxy(Res res, Indicator indicator, ICollector collector) {
        this.res = res;
        this.indicator = indicator;
        this.collector = collector;
        this.startTime = System.currentTimeMillis() - 501L;
    }

    @Override
    public IndicatorValue call() throws Exception {
        String name = Thread.currentThread().getName();
        Thread.currentThread().setName(name.replaceAll("-\\d", "-".concat(this.indicator.getName())));
        IndicatorValue indicatorValue = doCollect();
        return indicatorValue;
    }

    private IndicatorValue doCollect() {
        IndicatorValue indicatorValue = null;
        String statusDesc = CollectionConstant.COLLECT_SUCCESS;
        try {
            indicatorValue = this.collector.collect(this.indicator);
            if (!Objects.isNull(indicatorValue) && !Objects.isNull(indicatorValue.getValues()) && indicatorValue.getValues().containsKey("statusDesc")) {
                statusDesc = (String) indicatorValue.getValues().get(CollectionConstant.STATUS_DESC);
            }
            if (Objects.isNull(indicatorValue)) {
                statusDesc = CollectionConstant.COLLECT_FAIL_NULL_RESULT;
                indicatorValue = new IndicatorValue();
                indicatorValue.setFailureResult(statusDesc);
            }
        } catch (Exception e) {
            statusDesc = CollectionConstant.COLLECT_OCCURED_EXCEPTION;
            indicatorValue = new IndicatorValue();
            indicatorValue.setFailureResult(statusDesc + e.toString());
            log.error("Exception occured when collect {} , e: {}", this.res.getAlias() + "/" + this.indicator.getAlias(), e.toString());
            log.error("e: ", e);
        }
        this.endTime = System.currentTimeMillis();
        // 耗时
        indicatorValue.addValue(CollectionConstant.COLLECT_TIME_CONSUMED, (endTime - startTime));
        // desc && lastCollectTime
        this.indicator.setLastCollectTime(new Date(this.startTime));
        this.indicator.setDesc(statusDesc);

        return indicatorValue;
    }
}
