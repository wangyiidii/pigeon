package cn.yiidii.pigeon.cmdb.mapper;

import cn.yiidii.pigeon.cmdb.entity.Param;
import cn.yiidii.pigeon.cmdb.entity.Res;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ResMapper extends BaseMapper<Res> {
    List<Res> getAllRes();

    Res getResByName(String name);

    Res getResByIndicatorName(String name);

    List<Param> getParamByRes(String name);

    Integer addRes(Res res);

    Integer delResByName(String name);

}
