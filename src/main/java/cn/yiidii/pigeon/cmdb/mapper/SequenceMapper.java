package cn.yiidii.pigeon.cmdb.mapper;

import cn.yiidii.pigeon.cmdb.entity.KeyInfo;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface SequenceMapper extends BaseMapper<KeyInfo> {
}
