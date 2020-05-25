package cn.yiidii.pigeon.collection.collector.generic;

import cn.yiidii.pigeon.cmdb.dto.IndicatorValue;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.service.impl.CMDBService;
import cn.yiidii.pigeon.collection.collector.ICollector;
import cn.yiidii.pigeon.common.util.http.HttpClientUtil;
import cn.yiidii.pigeon.common.util.http.dto.HttpClientResult;
import cn.yiidii.pigeon.common.util.server.SpringContextUtil;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Objects;

public class HTTPCollector implements ICollector {

    private static final String REQUEST_URL = "Request URL";
    private static final String REQUEST_METHOD = "Request Method";
    private static final String REQUEST_PARAMS = "Request Params";

    @Override
    public IndicatorValue collect(Indicator indicator) {
        CMDBService cmdbService = SpringContextUtil.getBean(CMDBService.class);
        Map<String, String> params = cmdbService.getResParamsByName(indicator.getName());
        String url = params.get(REQUEST_URL);
        String method = params.get(REQUEST_METHOD);
        String reqParams = params.get(REQUEST_PARAMS);
        Map<String, String> reqMap = JSONObject.parseObject(reqParams, Map.class);
        return doRequest(url, method, reqMap, null);
    }

    private IndicatorValue doRequest(String url, String method, Map<String, String> params, Map<String, String> header) {
        IndicatorValue iv = new IndicatorValue();
        HttpClientResult result = null;
        if (StringUtils.equals(method, "GET")) {
            try {
                result = HttpClientUtil.doGet(url);
            } catch (Exception e) {
                iv.setFailureResult(e.toString());
                return iv;
            }
        } else if (StringUtils.equals(method, "POST")) {
            try {
                result = HttpClientUtil.doPost(url, params);
            } catch (Exception e) {
                iv.setFailureResult(e.toString());
                return iv;
            }
        }
        if (!Objects.isNull(result)) {
            iv.addValue("Status Code", result.getCode());
            iv.addValue("Response Body", result.getContent());
            return iv;
        }
        return null;
    }
}
