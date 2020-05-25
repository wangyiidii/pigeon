package cn.yiidii.pigeon.cmdb.mapper;


import cn.yiidii.pigeon.cmdb.entity.Indicator;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface IndicatorMapper extends BaseMapper<Indicator> {

    List<Indicator> getIndicatorsByResName(String name);

    List<Indicator> getIndicatorsWillBeCollect();

    Integer addIndicator(Indicator indicator);

    Integer updateIndicator4Collect(Indicator indicator);

    Integer delIndicatorByName(String name);

}
