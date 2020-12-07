package cn.yiidii.pigeon.cmdb.service.impl;

import cn.yiidii.pigeon.cmdb.codefine.DefineProxy;
import cn.yiidii.pigeon.cmdb.codefine.ParamDefine;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.entity.Param;
import cn.yiidii.pigeon.cmdb.entity.Res;
import cn.yiidii.pigeon.cmdb.mapper.IndicatorMapper;
import cn.yiidii.pigeon.cmdb.mapper.ParamMapper;
import cn.yiidii.pigeon.cmdb.mapper.ResMapper;
import cn.yiidii.pigeon.cmdb.service.ICMDBService;
import cn.yiidii.pigeon.cmdb.util.SequenceUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@Slf4j
public class CMDBService implements ICMDBService {

    @Autowired
    private ResMapper resMapper;
    @Autowired
    private IndicatorMapper indicatorMapper;
    @Autowired
    private ParamMapper paramMapper;

    @Override
    public Integer addRes(Res res, Map<String, String> paramMap) {
        try {
            Integer key = SequenceUtil.getInstance().next("res");

            String name = res.getDefName() + key;
            Res result = getResByName(name);

            while (!Objects.isNull(result)) {
                key++;
                name = res.getDefName() + key;
                result = getResByName(name);
                SequenceUtil.getInstance().saveSequence("res", key);
            }
            res.setName(name);
            res.setStatus(0);
            res.setCreateTime(new Date());
            List<Param> params = new ArrayList<>();
            if (!Objects.isNull(paramMap) && paramMap.size() > 0) {
                paramMap.forEach((k, v) -> {
                    Param param = new Param(res.getName(), k, v);
                    params.add(param);
                });
            }
            handleResParams(params, name, res.getDefName());

            for (Param p : params) {
                paramMapper.insert(p);
            }
        } catch (Exception e) {
            log.info("{}", e);
        }
        return resMapper.insert(res);
    }

    @Override
    public Integer addIndicator(Indicator indicator) {
        return indicatorMapper.insert(indicator);
    }

    @Override
    public List<Res> getAllRes() {
        return resMapper.selectList(null);
    }

    @Override
    public Res getResByName(String name) {
        return resMapper.selectOne(new QueryWrapper<Res>().eq("name", name));
    }

    @Override
    public Map<String, String> getResParamsByName(String name) {
        List<Param> params = resMapper.getParamByRes(name);
        Map<String, String> map = new HashMap<>();
        params.forEach(p -> {
            map.put(p.getKey(), p.getValue());
        });
        return map;
    }

    @Override
    public Res getResByIndicator(String name) {
        return resMapper.getResByIndicatorName(name);
    }

    @Override
    public List<Indicator> getAllIndicatorsOfRes(String name) {
        return indicatorMapper.getIndicatorsByResName(name);
    }

    @Override
    public List<Indicator> getIndicatorsWillBeCollect() {
        List<Indicator> indicatorList = indicatorMapper.getIndicatorsWillBeCollect();
        return indicatorList;
    }

    @Override
    public Indicator getIndicatorByName(String name) {
        return indicatorMapper.selectOne(new QueryWrapper<Indicator>().eq("name", name));
    }

    @Override
    public Map<String, String> getIndParamByName(String name) {
        List<Param> params = paramMapper.selectList(new QueryWrapper<Param>().eq("name", name));
        Map<String, String> map = new HashMap<>();
        params.forEach(p -> {
            map.put(p.getKey(), p.getValue());
        });
        return map;
    }

    @Override
    public Integer updateIndicatorInfo(Indicator indicator) {
        return indicatorMapper.update(indicator, new UpdateWrapper<Indicator>().eq("name", indicator.getName()));
    }

    @Override
    public Integer updateIndicator4Collect(Indicator indicator) {
        return indicatorMapper.updateIndicator4Collect(indicator);
    }

    @Override
    public Integer updateCollectStatus(String name, Integer collectStatus) {
        Indicator indicator = getIndicatorByName(name);
        indicator.setState(collectStatus);
        return indicatorMapper.update(indicator, new UpdateWrapper<Indicator>().eq("name", name));
    }

    @Override
    public Integer deleteResByName(String name) {
        return resMapper.delete(new UpdateWrapper<Res>().eq("name", name));
    }

    @Override
    public Integer deleteIndicatorByName(String name) {
        return indicatorMapper.delete(new UpdateWrapper<Indicator>().eq("name", name));
    }

    private void handleResParams(List<Param> params, String name, String defName) {
        Map<String, ParamDefine> paramDefMap = DefineProxy.getCoDefineMap().get(defName).getParams();
        boolean include = false;
        for (Map.Entry<String, ParamDefine> entry : paramDefMap.entrySet()) {
            String paramKey = entry.getKey();
            for (Param param : params) {
                if (param.getKey().equals(paramKey) || "param.ip".equals(paramKey)) {
                    include = true;
                    break;
                }
            }
            if (include) {
                continue;
            }
            ParamDefine paramDefine = entry.getValue();
            Param p = new Param(name, paramKey, paramDefine.getDefaultValue());
            params.add(p);
        }

    }

}
