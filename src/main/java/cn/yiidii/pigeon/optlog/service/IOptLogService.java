package cn.yiidii.pigeon.optlog.service;


import cn.yiidii.pigeon.optlog.entity.OptLog;

import java.util.List;

public interface IOptLogService {

    Object queryLogWithoutUid(Integer page, Integer pageSize);

    Object queryLogByUid(Integer uid, Integer page, Integer pageSize);

    Integer insert(OptLog optLog);

}
