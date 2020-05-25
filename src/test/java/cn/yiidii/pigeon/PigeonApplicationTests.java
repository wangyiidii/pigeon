package cn.yiidii.pigeon;

import cn.yiidii.pigeon.cmdb.entity.KeyInfo;
import cn.yiidii.pigeon.cmdb.mapper.IndicatorMapper;
import cn.yiidii.pigeon.cmdb.mapper.ResMapper;
import cn.yiidii.pigeon.cmdb.mapper.SequenceMapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
@Slf4j
class PigeonApplicationTests {
    @Autowired
    IndicatorMapper indicatorMapper;
    @Autowired
    ResMapper resMapper;
    @Autowired
    SequenceMapper sequenceMapper;

    @Test
    void test() {
        log.info("*******************");
//        System.out.println("wwwww: " + sequenceMapper.selectOne(new QueryWrapper<KeyInfo>().eq("name","res")));
//        System.out.println("wwwww: " + resMapper.selectOne(new QueryWrapper<Res>().eq("name","Windows1")));
        System.out.println(sequenceMapper.selectList(new QueryWrapper<KeyInfo>().eq("name", "res")));
        log.info("-----------------");
    }

}
