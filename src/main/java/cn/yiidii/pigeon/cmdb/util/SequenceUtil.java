package cn.yiidii.pigeon.cmdb.util;

import cn.yiidii.pigeon.cmdb.entity.KeyInfo;
import cn.yiidii.pigeon.cmdb.mapper.SequenceMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import javax.annotation.PostConstruct;
import java.util.Objects;

@Data
@Configuration
public class SequenceUtil {

    @Autowired
    SequenceMapper sequenceMapper;

    private static SequenceUtil instance = new SequenceUtil();

    @PostConstruct
    public void init() {
        instance = this;
        instance.sequenceMapper = this.sequenceMapper;
    }

    public static SequenceUtil getInstance() {
        return instance;
    }

    public synchronized Integer next(String name) {
        KeyInfo keyInfo = sequenceMapper.selectOne(new QueryWrapper<KeyInfo>().eq("name", name));
        Integer key = 0;
        if (Objects.isNull(keyInfo)) {
            keyInfo = new KeyInfo(name, key);
            sequenceMapper.insert(keyInfo);
        } else {
            key = keyInfo.getKey();
            key++;
            sequenceMapper.update(keyInfo, new UpdateWrapper<KeyInfo>().eq("name", name));
        }
        return key;
    }

    public synchronized void saveSequence(String name, Integer key) {
        KeyInfo keyInfo = new KeyInfo(name, key);
        sequenceMapper.update(keyInfo, new UpdateWrapper<KeyInfo>().eq("name", name));
    }

}
