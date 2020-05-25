package cn.yiidii.pigeon.collection.util;

import org.apache.commons.lang3.StringUtils;

import java.util.StringTokenizer;

/**
 * IP地址计算工具类 只支持IPv4
 */
public class IpAlgorithmUtils {
    public static final String IP_DIVIDE = "~";// IP段范围分割符
    private static final int MIN_INT = -2147483648;

    /**
     * 将一个IP地址段转换成一个数组
     *
     * @param beginIp
     * @param endIp
     * @return
     */
    public static String[] getIpLists(String beginIp, String endIp) {
        int beginIpI = getIntIp(beginIp);
        int endIpI = getIntIp(endIp);
        int i = endIpI - beginIpI + 1;
        String[] strings = new String[i];
        for (int j = 0; j < i; j++) {
            int k = beginIpI + j;
            strings[j] = getStringIp(k);

        }
        return strings;
    }

    /**
     * IP段整型格式转换为字符串格式
     *
     * @param intIp
     * @return
     */

    public static String getStringIp(int intIp) {
        if (intIp < 0) {
            intIp = intIp - MIN_INT;
        } else {
            intIp = intIp + MIN_INT;
        }
        String ip1 = "" + ((intIp >> 24) & 0xff);
        String ip2 = "" + ((intIp >> 16) & 0xff);
        String ip3 = "" + ((intIp >> 8) & 0xff);
        String ip4 = "" + (intIp & 0xff);

        return ip1 + "." + ip2 + "." + ip3 + "." + ip4;
    }

    /**
     * 判断IP地址是否合法
     *
     * @param address
     * @return
     */
    public static boolean isIpLegal(String address) {
        int ip1, ip2, ip3, ip4;
        try {
            int dotIndex = address.indexOf("."); // 点的位置
            String seg1 = address.substring(0, dotIndex); // 得第一个
            String tempIp = address.substring(dotIndex + 1,
                    address.lastIndexOf("."));
            String seg2 = tempIp.substring(0, tempIp.indexOf("."));
            String seg3 = tempIp.substring(tempIp.indexOf(".") + 1,
                    tempIp.length());
            String seg4 = address.substring(address.lastIndexOf(".") + 1,
                    address.length());
            ip1 = Integer.parseInt(seg1);
            ip2 = Integer.parseInt(seg2);
            ip3 = Integer.parseInt(seg3);
            ip4 = Integer.parseInt(seg4);
        } catch (Exception ex) {
            return false;
        }
        if ((ip1 < 0 || ip1 > 255) || (ip2 < 0 || ip2 > 255)
                || (ip3 < 0 || ip3 > 255) || (ip4 < 0 || ip4 > 255)) {
            return false;
        }

        if (StringUtils.endsWith(address, ".0")
                || StringUtils.endsWith(address, ".255")) {
            return false;
        }
        return true;
    }

    /**
     * 确定掩码是否合法
     *
     * @param address
     * @return
     */
    public static boolean isMaskLegal(String address) {
        int ip1, ip2, ip3, ip4;
        try {
            int dotIndex = address.indexOf("."); // 点的位置
            String seg1 = address.substring(0, dotIndex); // 得第一个
            String tempIp = address.substring(dotIndex + 1,
                    address.lastIndexOf("."));
            String seg2 = tempIp.substring(0, tempIp.indexOf("."));
            String seg3 = tempIp.substring(tempIp.indexOf(".") + 1,
                    tempIp.length());
            String seg4 = address.substring(address.lastIndexOf(".") + 1,
                    address.length());
            ip1 = Integer.parseInt(seg1);
            ip2 = Integer.parseInt(seg2);
            ip3 = Integer.parseInt(seg3);
            ip4 = Integer.parseInt(seg4);
        } catch (Exception ex) {
            return false;
        }
        if ((ip1 < 0 || ip1 > 255) || (ip2 < 0 || ip2 > 255)
                || (ip3 < 0 || ip3 > 255) || (ip4 < 0 || ip4 > 255)) {
            return false;
        }

        // if (StringUtils.endsWith(address, ".0")
        // || StringUtils.endsWith(address, ".254") ||
        // StringUtils.endsWith(address, ".255")) {
        // return false;
        // }
        return true;
    }

    /**
     * 描述：比较两个String形式的IP地址的大小
     *
     * @return int: 0-ip1=ip2; 1-ip1>ip2; -1-ip1<ip2
     */
    public static int compareIP(String ip1, String ip2) {
        int intIp1 = getIntIp(ip1);
        int intIp2 = getIntIp(ip2);
        if (intIp1 > intIp2) {
            return 1;
        } else if (intIp1 == intIp2) {
            return 0;
        } else {
            return -1;
        }
    }

    /**
     * IP段字符串格式转换为整型格式 要求一个地址大于另一个地址时，整型格式也保持大于关系 如：128.0.0.0 >
     * 127.255.255.255，要求getIntIp(128.0.0.0)>getIntIp(127.255.255.255)
     *
     * @param stringIp
     * @return
     */
    public static int getIntIp(String stringIp) {
        try {
            StringTokenizer st = new StringTokenizer(stringIp, ".");
            String ip1 = st.nextToken();
            String ip2 = st.nextToken();
            String ip3 = st.nextToken();
            String ip4 = st.nextToken();
            int temp = (Integer.parseInt(ip1) << 24)
                    | (Integer.parseInt(ip2) << 16)
                    | (Integer.parseInt(ip3) << 8) | (Integer.parseInt(ip4));
            if (temp < 0) {
                temp = temp - MIN_INT;
            } else {
                temp = temp + MIN_INT;
            }
            return temp;
        } catch (Exception ex) {
            return 0;
        }
    }

    /**
     * 检测某个IP是否在某个IP段内
     *
     * @param beginIp
     * @param endIp
     * @param checkIp
     * @return
     */
    public static boolean isInIPRange(String beginIp, String endIp,
                                      String checkIp) {
        if (beginIp == null || endIp == null || checkIp == null) {
            return false;
        }
        if (getIntIp(beginIp) <= getIntIp(checkIp)
                && getIntIp(endIp) >= getIntIp(checkIp)) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 处理差值32位寄存器使用
     *
     * @param before
     * @param now
     * @return
     */
    public static Long getDifference32(Long before, Long now) {
        Long tmp = now - before;
        if (tmp >= 0) {
            return tmp;

        } else {
            return 4294967296L - before + now;
        }
    }

    /**
     * 处理差值16位寄存器使用
     *
     * @param before
     * @param now
     * @return
     */
    public static Long getDifference16(Long before, Long now) {
        Long tmp = now - before;
        if (tmp >= 0) {
            return tmp;

        } else {
            return 65536L - before + now;
        }
    }

    //测试
    public static void main(String[] args) {

        // 测试IP地址段拆分
        String[] strings = IpAlgorithmUtils.getIpLists("192.168.10.1",
                "192.168.10.254");
        // PingerUtils pingerUtils = null;
        for (int i = 0; i < strings.length; i++) {
            // System.out.println();
            // pingerUtils = new PingerUtils(strings[i], 1, 2000);
            // System.out.println(strings[i] + pingerUtils.isReachable());
        }

        boolean b = IpAlgorithmUtils.isInIPRange("192.168.0.1",
                "192.168.0.254", "192.168.0.8");
        System.out.println("是否包含" + b);

        boolean c = IpAlgorithmUtils.isMaskLegal("255.255.255.0");
        System.out.println("IP地址是否合法" + c);

    }
}