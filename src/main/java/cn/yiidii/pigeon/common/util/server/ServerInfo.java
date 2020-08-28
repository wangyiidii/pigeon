package cn.yiidii.pigeon.common.util.server;

import org.hyperic.sigar.CpuPerc;
import org.hyperic.sigar.Mem;
import org.hyperic.sigar.Sigar;
import org.hyperic.sigar.SigarException;

import java.math.BigDecimal;

public class ServerInfo {

    public static void main(String[] args) throws Exception {
        System.out.println(cpu());
        System.out.println(memory());
    }

    public static double cpu() throws SigarException {
        Double util = 0.0;
        Sigar sigar = new Sigar();
        CpuPerc[] cpuList = sigar.getCpuPercList();
        for (int i = 0; i < cpuList.length; i++) {
            util += cpuList[i].getCombined();
        }
        return parseDouble(util / cpuList.length * 100);

    }

    public static double memory() throws SigarException {
        Sigar sigar = new Sigar();
        Mem mem = sigar.getMem();

        double total = mem.getTotal();
        double used = mem.getUsed();
        return parseDouble(used / total * 100);
    }


    private static double parseDouble(double target) {
        BigDecimal bg = new BigDecimal(target);
        target = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return Double.isNaN(target) ? Double.NaN : target;
    }

}
