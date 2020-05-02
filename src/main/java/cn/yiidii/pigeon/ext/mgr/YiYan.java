package cn.yiidii.pigeon.ext.mgr;

import cn.yiidii.pigeon.common.util.http.HttpClientUtil;
import cn.yiidii.pigeon.common.util.http.dto.HttpClientResult;
import com.alibaba.fastjson.JSONObject;

public class YiYan {

    public static String[] randomYiYan() {
        String[] yiyan = new String[2];
        try {
            HttpClientResult result = HttpClientUtil.doGet("https://v1.hitokoto.cn/", null, null);
            JSONObject jo = ((JSONObject) JSONObject.toJSON(result)).getJSONObject("content");
            yiyan[0] = jo.getString("hitokoto");
            yiyan[1] = jo.getString("from");
        } catch (Exception e) {
            yiyan = new String[]{"据说啊 叹气会让幸福溜走的", "再见了 我们的幼儿园"};
        }
        return yiyan;
    }

}
