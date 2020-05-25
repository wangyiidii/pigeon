package cn.yiidii.pigeon.cmdb.codefine;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

public class DefineProxy {
    private static Map<String, CODefine> coDefineMap = new ConcurrentHashMap<>();
    private static Map<String, IndicatorDefine> indDefineMap = new ConcurrentHashMap<>();

    public static Map<String, CODefine> getCODefineMap() {
        return coDefineMap;
    }

    public static Map<String, IndicatorDefine> getIndDefineMap() {
        return indDefineMap;
    }

    protected static void setCODefineMap(Map map) {
        coDefineMap = map;
    }

    protected static void setIndDefineMap(Map map) {
        indDefineMap = map;
    }

    public static Map<String, CODefine> getCODefineByExtend(String extend) {
        Map<String, CODefine> result = new HashMap<>();
        extend = StringUtils.isBlank(extend) ? COConstant.CO_ROOT : extend;
        for (CODefine coDefine : coDefineMap.values()) {
            String coExtend = coDefine.getExtend();
            if (StringUtils.equals(coExtend, extend)) {
                result.put(coDefine.getName(), coDefine);
            }
        }
        return result;
    }

    public static CODefine getCODefineByName(String coDefName) {
        return coDefineMap.get(coDefName);
    }

    public static IndicatorDefine getIndDefineByName(String indDefname) {
        return indDefineMap.get(indDefname);
    }

    public static Map<String, MetricDefine> getCODefineMetric(String indDefineName) {
        IndicatorDefine define = indDefineMap.get(indDefineName);
        if (Objects.isNull(define)) {
            return null;
        }
        return define.getMetrics();
    }
}
