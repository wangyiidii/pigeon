package cn.yiidii.pigeon.common.util.page;

import cn.yiidii.pigeon.common.util.page.dto.PageResult;
import com.alibaba.fastjson.JSONObject;

public class PageUtil {

    public static String parsePageResult(PageResult<?> obj) {
        JSONObject jo = new JSONObject();
        jo.put("total", obj.getTotal());
        //jo.put("pageCount", obj.getPageCount());
        jo.put("rows", obj.getList());
        //jo.put("page", obj.getCurrent());
        return jo.toString();
    }

}
