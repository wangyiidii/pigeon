package cn.yiidii.pigeon.collection.collector.host.windows;

import cn.yiidii.pigeon.cmdb.dto.IndicatorValue;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.entity.Res;
import cn.yiidii.pigeon.cmdb.service.impl.CMDBService;
import cn.yiidii.pigeon.collection.collector.generic.GenericSnmpCollector;
import cn.yiidii.pigeon.collection.util.SnmpUtil;
import cn.yiidii.pigeon.common.util.server.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.snmp4j.smi.VariableBinding;
import org.snmp4j.util.RetrievalEvent;
import org.snmp4j.util.TableEvent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Slf4j
public class WindowsMemorySNMPIndicator extends GenericSnmpCollector {

    private static final String PARAM_SNMP_COMMUNITY = "snmp.community";

    String[] oids = {".1.3.6.1.2.1.25.2.3.1.2", ".1.3.6.1.2.1.25.2.3.1.4",
            ".1.3.6.1.2.1.25.2.3.1.5", ".1.3.6.1.2.1.25.2.3.1.6"};

    @Override
    public IndicatorValue collect(Indicator indicator) {
        CMDBService cmdbService = SpringContextUtil.getBean(CMDBService.class);
        Res res = cmdbService.getResByIndicator(indicator.getName());
        Map<String, String> params = cmdbService.getResParamsByName(res.getName());
        SnmpUtil snmpUtil = new SnmpUtil(res.getHost(), params.get(PARAM_SNMP_COMMUNITY));
        IndicatorValue indValues = new IndicatorValue();
        try {
            List<TableEvent> tableEvents = snmpUtil.snmpTableGet(oids);
            boolean succ = false;
            long total = 0, used = 0, buffAndCache = 0;
            if (tableEvents.size() > 0) {
                for (int i = 0; i < tableEvents.size(); i++) {
                    TableEvent te = tableEvents.get(i);
                    if (te.getStatus() == RetrievalEvent.STATUS_OK) {
                        VariableBinding[] vb = te.getColumns();
                        if (vb.length == oids.length) {
                            String type = vb[0].getVariable().toString();
                            long unit = vb[1].getVariable().toLong();
                            if (type.equals("1.3.6.1.2.1.25.2.1.2")) {
                                total = vb[2].getVariable().toLong() * unit / 1024
                                        / 1024;
                                used = vb[3].getVariable().toLong() * unit / 1024
                                        / 1024;
                                succ = true;
                            } else if (type.equals("1.3.6.1.2.1.25.2.1.1")) {
                                if (vb[3] != null) {
                                    buffAndCache += vb[3].getVariable().toLong()
                                            * unit / 1024 / 1024;
                                }
                            }
                        }
                    } else {
                        break;
                    }
                }
            }
            if (succ) {
                long free = total - used;
                if (buffAndCache < used) {
                    free += buffAndCache;
                    used -= buffAndCache;
                }
                indValues.addValue("Mem Util", parseDouble((double) used / total * 100));
            } else {
                indValues.setFailureResult("Snmp query failed.");
            }
        } catch (Exception e) {
            log.error("Exception occured when collect {}.", indicator.getName());
        }
        return indValues;
    }

    private double parseDouble(double target) {
        BigDecimal bg = new BigDecimal(target);
        target = bg.setScale(2, BigDecimal.ROUND_HALF_UP).doubleValue();
        return Double.isNaN(target) ? Double.NaN : target;
    }
}
