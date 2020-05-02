package cn.yiidii.pigeon.common.util;

import cn.yiidii.pigeon.common.util.http.HttpClientUtil;
import cn.yiidii.pigeon.common.util.http.dto.HttpClientResult;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

/**
 * @author yiidii
 * @date 2020/3/26 23:38:39
 * @desc This class is used to ...
 */
public class IPUtil {

    private static final String UNKNOWN = "unknown";
    private static final String LOCALHOST = "127.0.0.1";
    private static final String SEPARATOR = ",";
    private static Map<String, String> headers = new HashMap<>();

    static {
        headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/80.0.3987.163 Safari/537.36");
    }

    public static String getIpAddr(HttpServletRequest request) {
        String ipAddress;
        try {
            ipAddress = request.getHeader("x-forwarded-for");
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ipAddress == null || ipAddress.length() == 0 || UNKNOWN.equalsIgnoreCase(ipAddress)) {
                ipAddress = request.getRemoteAddr();
                if (LOCALHOST.equals(ipAddress)) {
                    InetAddress inet = null;
                    try {
                        inet = InetAddress.getLocalHost();
                    } catch (UnknownHostException e) {
                        e.printStackTrace();
                    }
                    ipAddress = inet.getHostAddress();
                }
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP,多个IP按照','分割
            // "***.***.***.***".length()
            if (ipAddress != null && ipAddress.length() > 15) {
                if (ipAddress.indexOf(SEPARATOR) > 0) {
                    ipAddress = ipAddress.substring(0, ipAddress.indexOf(","));
                }
            }
        } catch (Exception e) {
            ipAddress = "";
        }
        return ipAddress;
    }

    public static String getUA(HttpServletRequest request) {
        String ua = request.getHeader("User-Agent");
        return ua.length() > 0 ? ua : "UNKNOW UA";
    }

    public static String getLocationByIP(String ip) {
        String location = "UNKNOWN";
        try {
            String url = "http://whois.pconline.com.cn/ipJson.jsp?callback=testJson&ip=" + ip;
            HttpClientResult result = HttpClientUtil.doGet(url, headers, null);

            if (result.getCode() == 200) {
                String respText = result.getContent().trim();
                JSONObject respJo = JSONObject.parseObject(respText.substring(30, respText.length() - 3));
                location = respJo.getString("addr");
            }
        } catch (Exception e) {
            location = "UNKNOWN";
        }
        return location.trim();
    }

    private static String parseLocaStr(String str) {
        return (StringUtils.equals("XX", str) || StringUtils.isEmpty(str)) ? "" : str + ",";
    }

    public static void main(String[] args) {
        System.out.println(getLocationByIP("39.144.16.26"));
    }
}