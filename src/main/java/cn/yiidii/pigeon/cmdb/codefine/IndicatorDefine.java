package cn.yiidii.pigeon.cmdb.codefine;


import lombok.Data;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Data
public class IndicatorDefine {
    private String name;
    private String alias;
    private String ref;
    private String extend;
    private String collector;
    private String method;
    private String collScript;

    private Map<String, ParamDefine> params = new ConcurrentHashMap<>();
    private Map<String, MetricDefine> metrics = new ConcurrentHashMap<>();

    public void addParam(ParamDefine param) {
        this.params.put(param.getName(), param);
    }

    public void addMetric(MetricDefine metric) {
        this.metrics.put(metric.getName(), metric);
    }

}
