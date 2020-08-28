package cn.yiidii.pigeon.quratz.job;

import cn.yiidii.pigeon.common.util.http.HttpClientUtil;
import cn.yiidii.pigeon.common.util.http.dto.HttpClientResult;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class QuanMinKGe implements Job {

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();
        String cookie = jobDataMap.getString("cookie");
        Integer openKey = jobDataMap.getInt("openKey");
        String url = "https://node.kg.qq.com/webapp/proxy?format=json&outCharset=utf-8&g_tk=" + openKey + "&g_tk_openkey=" + openKey;

        String msg = "";
        try {
            HttpClientResult result = HttpClientUtil.doJsonPost(url, wrapReqHeader(cookie), wrapReqParam(openKey, cookie).toString());
            if (result.getCode() == 200) {
                JSONObject awardsJo = JSONObject.parseObject(result.getContent()).getJSONObject("data").getJSONObject("task.revisionSignInGetAward");
                if (awardsJo.getInteger("total") > 0) {
                    Integer totalFlower = 0;
                    for (Object o : awardsJo.getJSONArray("awards")) {
                        JSONObject award = (JSONObject) o;
                        totalFlower += award.getInteger("num");
                    }
                } else {
                    msg = awardsJo.getString("msg");
                }
            }
        } catch (Exception e) {
            log.error("job execute ex: {}", e.toString());
            msg = "cookie 失效";
        }
        JobKey key = context.getJobDetail().getKey();
        String jobInfo = key.getName() + " - " + key.getGroup();
        log.info("{} resultMsg: {}", jobInfo, msg);
    }


    private Map<String, String> wrapReqHeader(String cookie) {
        Map<String, String> header = new LinkedHashMap<>();
        header.put("Cookie", cookie);
        header.put("User-Agent", "Mozilla/5.0 (iPhone; CPU iPhone OS 13_3_1 like Mac OS X) AppleWebKit/605.1.15 (KHTML, like Gecko) Mobile/15E148 QQJSSDK/1.0.0 Hippy/1.0 qua/V1_IPH_KG_6.21.8_449_APP_A qmkege/6.21.8 GDTMobSDK/4.10.36");
        header.put("Content-Type", "application/json");
        header.put("Referer", "https://kg.qq.com/vMission/index.html?hippy=vMission");
        header.put("Host", "node.kg.qq.com");
        header.put("No-Chunked", "true");
        return header;
    }

    private JSONObject wrapReqParam(Integer openKey, String cookie) {
        JSONObject param = new JSONObject(new LinkedHashMap<>());
        param.put("g_tk_openkey", openKey);
        param.put("t_uid", getUidFromCookie(cookie));
        param.put("t_show_entry", 0);
        JSONObject t_mapExtInfo = new JSONObject(new LinkedHashMap<>());
        t_mapExtInfo.put("device_id", "");
        param.put("t_mapExtInfo", t_mapExtInfo);
        param.put("t_vctAppId", new JSONArray());
        param.put("ns", "KG_TASK");
        param.put("cmd", "task.revisionSignInGetAward");
        param.put("ns_inbuf", "");
        JSONObject mapExt = new JSONObject();
        mapExt.put("file", "taskJce");
        mapExt.put("cmdName", "GetSignInAwardReq");
        JSONObject l5api = new JSONObject(new LinkedHashMap<>());
        l5api.put("modid", 503937);
        l5api.put("cmd", 589824);
        mapExt.put("l5api", l5api);
        JSONObject l5api_exp1 = new JSONObject(new LinkedHashMap<>());
        l5api_exp1.put("modid", 817089);
        l5api_exp1.put("cmd", 3801088);
        mapExt.put("l5api_exp1", l5api_exp1);
        param.put("mapExt", mapExt);
        return param;
    }

    private String getUidFromCookie(String cookie) {
        String uid = "";
        String[] kvs = cookie.split(";");
        for (int i = 0; i < kvs.length; i++) {
            String[] kv = kvs[i].split("=");
            if ("uid".equals(kv[0].trim())) {
                uid = kv[1];
            }
        }
        return uid;
    }
}
