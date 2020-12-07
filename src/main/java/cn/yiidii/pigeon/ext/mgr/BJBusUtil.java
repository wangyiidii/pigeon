package cn.yiidii.pigeon.ext.mgr;

import cn.yiidii.pigeon.ext.mgr.dto.Item;
import cn.yiidii.pigeon.common.util.http.HttpClientUtil;
import cn.yiidii.pigeon.common.util.http.dto.HttpClientResult;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * 北京公交查询
 *
 * @see <a href="http://www.bjbus.com/">北京公交官网</a>
 */
public class BJBusUtil {

    //所有线路
    private static final String URL_BUSLINE = "http://www.bjbus.com/home/fun_rtbus.php?uSec=00000160&uSub=00000162";
    private static final String URL_BUS_BASE = "http://www.bjbus.com/home/ajax_rtbus_data.php";

    public static void main(String[] args) throws Exception {
        System.out.println(getRealTimeBus("1", "5187806150879113338", "1"));
    }

    /**
     * 查询实时公交
     *
     * @param bid 公交id
     * @param rid 上下行id
     * @param sid 站点id
     * @return
     * @throws Exception
     */
    public static JSONObject getRealTimeBus(String bid, String rid, String sid) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("act", "busTime");
        params.put("selBLine", bid);
        params.put("selBDir", rid);
        params.put("selBStop", sid);
        HttpClientResult result = HttpClientUtil.doGet(URL_BUS_BASE, null, params);
        // 解析并返回给前台html
        JSONObject jo = JSONObject.parseObject(result.getContent());
        Document doc = Jsoup.parse(StringEscapeUtils.unescapeHtml4(jo.getString("html")));
        doc.getElementsByClass("inquiry_footer").remove();
        JSONObject realTimeJo = new JSONObject();
        String busInfo = doc.getElementsByClass("left fixed").get(0).getElementsByTag("h3").get(0).text();
        String rowInfo = doc.getElementsByClass("inner").get(0).getElementsByTag("h2").get(0).text();
        List<Element> pTags = doc.getElementsByTag("article").get(0).getElementsByTag("p");
        String stationInfo = pTags.get(0).text();
        String realtimeBusInfo = pTags.get(1).text();
        if (StringUtils.contains(realtimeBusInfo, "预计")) {
            stationInfo += "<br/><span class='text-success'>" + realtimeBusInfo + "</span>";
        } else {
            stationInfo += "<br/><span class='grey-text'>" + realtimeBusInfo + "</span>";
        }
        doc.getElementsByClass("inquiry_header").remove();
        realTimeJo.put("busInfo", busInfo);
        realTimeJo.put("rowInfo", rowInfo);
        realTimeJo.put("stationInfo", stationInfo);
        realTimeJo.put("html", doc.getElementsByTag("body").toString().replaceAll("\\.\\.\\.", ""));
        return realTimeJo;
    }

    /**
     * 获取所有公交线路列表
     */
    public static List<Item> getBuses() throws Exception {
        HttpClientResult result = HttpClientUtil.doGet(URL_BUSLINE, null, null);
        Elements busEles = Jsoup.parse(result.getContent()).getElementById("selBLine").children();
        List<Item> buses = new LinkedList<>();
        for (Element element : busEles) {
            Item item = new Item();
            item.setId(element.text());
            item.setAlias(element.text());
            buses.add(item);
        }
        return buses;
    }

    /**
     * 获取线路上下行id列表
     *
     * @throws Exception
     */
    public static List<Item> getRows(String bid) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("act", "getLineDir");
        params.put("selBLine", bid);
        HttpClientResult result = HttpClientUtil.doGet(URL_BUS_BASE, null, params);
        Elements rowsEles = Jsoup.parse(result.getContent()).getElementsByTag("a");
        List<Item> rows = new LinkedList<>();
        for (Element element : rowsEles) {
            Item item = new Item();
            item.setId(element.attr("data-uuid"));
            item.setAlias(element.text());
            rows.add(item);
        }
        return rows;
    }

    /**
     * 获取所有站点
     */
    public static List<Item> getStations(String bid, String rid) throws Exception {
        Map<String, String> params = new HashMap<>();
        params.put("act", "getDirStation");
        params.put("selBLine", bid);
        params.put("selBDir", rid);
        HttpClientResult result = HttpClientUtil.doGet(URL_BUS_BASE, null, params);
        Elements stationEles = Jsoup.parse(result.getContent()).getElementsByTag("a");
        List<Item> stations = new LinkedList<>();
        for (Element element : stationEles) {
            Item item = new Item();
            item.setId(element.attr("data-seq"));
            item.setAlias(element.text());
            stations.add(item);
        }
        return stations;
    }

}
