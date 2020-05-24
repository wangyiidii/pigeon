package cn.yiidii.pigeon.quratz.job;

import cn.yiidii.pigeon.annotation.OptLogAnnotation;
import cn.yiidii.pigeon.common.util.http.HttpClientUtil;
import cn.yiidii.pigeon.common.util.http.dto.HttpClientResult;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.HashMap;
import java.util.Map;

@Slf4j
public class LTSign implements Job {

    static Map<String, String> headers = new HashMap<>();

    static {
        headers.put("Accept", "text/html, application/xhtml+xml, image/jxr, */*");
        headers.put("User-Agent", "Mozilla/5.0 (Linux; Android 9; MI MAX 3 Build/PKQ1.180729.001; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/74.0.3729.157 Mobile Safari/537.36; unicom{version:android@7.0000,desmobile:15690507717};devicetype{deviceBrand:Xiaomi,deviceModel:MI MAX 3}");
        // headers.put("Cookie", "MUT_S=android5.1.1; login_type=06; u_account=15600015800; city=011|110; ecs_acc=CnLuYDFX/43AgeFimU8srxgEaYHoDE3URgaRjwBwKeCY1k96NEzwYNaj3TItgRc5X/dQ+tPFNN+YQDnteXIblgF7u0QuMDvh3xrN0SKmIf0fMlblEjBm3IcZzbeR8ZSsq4psbKQxEypoxxUFs80VDd3dpbrYIHvzoJUylCc+tLc=; cw_mutual=6ff66a046d4cb9a67af6f2af5f74c321c9895801d4ddcc180fe19683242bc6bb04cd94e7f0bb9e7797b4093fe7aed8c4ed347e9f0c173c52c1f3956980531cd7; ecs_token=eyJkYXRhIjoiNWVjMzc1MzNjZDhiYmJhZTEwYWQ1NDMzYjIyNDJkODc2M2Q4ZWU2M2U4ZjAxYTk5OGEzNTQ2NDcwMDFmNzI3NGIxNzc3OTQwMzQ3YWY4ODFhYmUxYTNjYTJjNjY4MGUxNjlhYzVkYmJiYTc5ZjFkZDEzOGZlNjIyYjRjOTdjMzFkNTBjOTk3NTc0OGI5YmQxMDAyZTIzNDhkMGUxNDgzYTk5NDQ2YWI0MmViZDk4ZDRkY2RjNzQ4YjlhN2I0MzVkIiwidmVyc2lvbiI6IjAwIn0=; jwt=eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJtb2JpbGUiOiIxNTYwMDAxNTgwMCIsInBybyI6IjAxMSIsImNpdHkiOiIxMTAiLCJpZCI6ImQ5ZjM2Zjk4NGRkMDFjZTJkYzRhNDUyMTVjMWNmZjI5In0.6tR0gzwL0c8pAtk3SSC86xaSUbwyjMo65nAja-vauBg; SHAREJSESSIONID=04E3854FD7EB54E7CE58F4E546347445X-Requested-With: com.sinovatech.unicom.ui");
    }

    @Override
    @OptLogAnnotation(desc = "联通签到任务")
    public void execute(JobExecutionContext context) throws JobExecutionException {
        JobDataMap data = context.getTrigger().getJobDataMap();
        String invokeParam = (String) data.get("invokeParam");
        JSONObject paramsJo = JSONObject.parseObject(invokeParam);
        String phone = paramsJo.getString("phone");
        String cookie = paramsJo.getString("cookie");
        try {
            sign(phone, cookie);
            getCoin();
        } catch (Exception e) {
            log.error("Ex occured");
        }
    }

    private void sign(String phone, String cookie) throws Exception {
        String url = "https://act.10010.com/SigninApp/signin/daySign.do";
        //params
        Map<String, String> params = new HashMap<>();
        params.put("className", "signinIndex");
        //headers
        headers.put("Referer", "https://act.10010.com/SigninApp/signin/querySigninActivity.htm?desmobile=" + phone + "&version=android@7.0000");
        headers.put("Cookie", cookie);
        //doPost
        HttpClientResult result = HttpClientUtil.doPost(url, headers, params);
        //parse response
        if (result.getCode() == 200) {
            JSONObject jo = JSONObject.parseObject(result.getContent());
            String msgCode = jo.getString("msgCode");
            if (StringUtils.equals("0000", msgCode)) {
                log.info("签到成功！获得了 " + jo.getString("continuCount") + " 个金币");
            } else if (StringUtils.equals("0008", msgCode)) {
                log.info("签到失败,今天已签到!");
            } else {
                log.error("签到失败,cookie过期。");
            }
        } else {
            log.error("签到失败。状态码: {}. content: {}.", result.getCode(), result.getContent().replaceAll("\n", "").substring(0, 50));
        }
    }

    private void getCoin() throws Exception {
        String url = "https://act.10010.com/SigninApp/signin/goldTotal.do?vesion=0.019762071301";
        HttpClientResult result = HttpClientUtil.doPost(url, headers, null);
        if (result.getCode() == 200) {
            log.info("当前金币个数：" + result.getContent());
        } else {
            log.error("查询金币失败。状态码: {}. content: {}.", result.getCode(), result.getContent().replaceAll("\n", "").substring(0, 50));
        }
    }
}