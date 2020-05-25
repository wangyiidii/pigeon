package cn.yiidii.pigeon.cmdb.service;

import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.entity.Res;

import java.util.List;
import java.util.Map;

public interface ICMDBService {
    //add
    Integer addRes(Res res, Map<String, String> paramMap);

    Integer addIndicator(Indicator indicator);

    //get
    List<Res> getAllRes();

    Res getResByName(String name);

    Map<String, String> getResParamsByName(String name);

    Res getResByIndicator(String name);

    List<Indicator> getAllIndicatorsOfRes(String name);

    List<Indicator> getIndicatorsWillBeCollect();

    Indicator getIndicatorByName(String name);

    Map<String, String> getIndParamByName(String name);

    //update

    Integer updateIndicatorInfo(Indicator indicator);

    Integer updateCollectStatus(String name, Integer collectStatus);

    public Integer updateIndicator4Collect(Indicator indicator);

    //delete
    Integer deleteResByName(String name);

    Integer deleteIndicatorByName(String name);

    //other


}
