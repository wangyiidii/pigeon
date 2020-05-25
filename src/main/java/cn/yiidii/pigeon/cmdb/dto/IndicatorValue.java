package cn.yiidii.pigeon.cmdb.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@Data
public class IndicatorValue {
    public static final String PARA_RESULT = "private.result";
    private static final String RESULT_SUCCESS = "0";
    private static final String RESULT_FAILED = "1";

    private Map<String, Object> values = new HashMap<>();

    private String statusDesc;

    public IndicatorValue() {
        setSuccessResult();
    }

    public void setSuccessResult() {
        this.values.put("private.result", "0");
    }

    public void setFailureResult() {
        this.values.put("private.result", "1");
    }

    public void setFailureResult(String statusDesc) {
        this.values.put("statusDesc", statusDesc);
        this.values.put("private.result", "1");
    }

    public void addValue(String key, Object value) {
        this.values.put(key, String.valueOf(value instanceof Double ? parseDouble((Double) value) : value));
    }

    public String getValue(String paramString) {
        return (String) this.values.get(paramString);
    }

    public Map<String, Object> getValues() {
        return this.values;
    }

    public void setValues(Map<String, Object> paramMap) {
        this.values = paramMap;
    }

    public String toString() {
        String str = "";
        for (Map.Entry<String, Object> entry : this.values.entrySet())
            str = str + "<" + (String) entry.getKey() + "," + (String) entry.getValue() + ">";
        return str;
    }

    private double parseDouble(double target) {
        BigDecimal bg = new BigDecimal(target);
        target = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return Double.isNaN(target) ? Double.NaN : target;
    }
}
