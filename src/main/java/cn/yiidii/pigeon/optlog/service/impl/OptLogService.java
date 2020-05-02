package cn.yiidii.pigeon.optlog.service.impl;

import cn.yiidii.pigeon.annotation.PagingQuery;
import cn.yiidii.pigeon.optlog.entity.OptLog;
import cn.yiidii.pigeon.optlog.mapper.OptLogMapper;
import cn.yiidii.pigeon.optlog.service.IOptLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OptLogService implements IOptLogService {

    @Autowired
    private OptLogMapper optLogMapper;

    @Override
    @PagingQuery
    public Object queryLogWithoutUid(Integer page, Integer pageSize) {
        return optLogMapper.queryLogWithoutUid();
    }

    @Override
    @PagingQuery
    public Object queryLogByUid(Integer uid, Integer page, Integer pageSize) {
        return optLogMapper.queryLogByUid(uid);
    }

    @Override
    public Integer insert(OptLog optLog) {
        return optLogMapper.insert(optLog);
    }
}
