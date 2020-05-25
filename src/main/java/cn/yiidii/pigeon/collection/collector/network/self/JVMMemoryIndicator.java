package cn.yiidii.pigeon.collection.collector.network.self;

import cn.yiidii.pigeon.cmdb.dto.IndicatorValue;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.collection.collector.ICollector;
import cn.yiidii.pigeon.collection.collector.network.utils.JVMMemoryUtils;

public class JVMMemoryIndicator implements ICollector {

    @Override
    public IndicatorValue collect(Indicator indicator) {
        IndicatorValue iv = new IndicatorValue();
        iv.addValue("Heap Memory Usage", JVMMemoryUtils.getHeapMemoryUsage().getUsedPercent() * 100);
        iv.addValue("NonHeap Memory Usage", JVMMemoryUtils.getNonHeapMemoryUsage().getUsedPercent() * 100);
        return iv;
    }
}
