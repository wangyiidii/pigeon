package cn.yiidii.pigeon.collection.collector;

import cn.yiidii.pigeon.cmdb.dto.IndicatorValue;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import org.springframework.stereotype.Component;

@Component
public interface ICollector {
    IndicatorValue collect(Indicator indicator);
}
