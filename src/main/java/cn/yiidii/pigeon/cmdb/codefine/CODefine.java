package cn.yiidii.pigeon.cmdb.codefine;

import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class CODefine {

    private String name;
    private String alias;
    private boolean category = false;
    private String extend;

    private Map<String, ParamDefine> params = new ConcurrentHashMap<>();
    private Map<String, IndicatorDefine> indicators = new ConcurrentHashMap<>();

    public void addParam(ParamDefine param) {
        this.params.put(param.getName(), param);
    }

    public void addIndicator(IndicatorDefine indicatorDefine) {
        this.indicators.put(indicatorDefine.getName(), indicatorDefine);
    }
}
