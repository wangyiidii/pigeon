package cn.yiidii.pigeon.collection.collector.generic;

import cn.yiidii.pigeon.cmdb.codefine.DefineProxy;
import cn.yiidii.pigeon.cmdb.codefine.MetricDefine;
import cn.yiidii.pigeon.cmdb.dto.IndicatorValue;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.entity.Res;
import cn.yiidii.pigeon.cmdb.service.impl.CMDBService;
import cn.yiidii.pigeon.collection.collector.ICollector;
import cn.yiidii.pigeon.common.util.server.SpringContextUtil;
import cn.yiidii.pigeon.common.util.ssh.JSchExecutor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class SSHCollector implements ICollector {

    private static final String SSH_USERNAME = "param.ssh.username";
    private static final String SSH_PASSWORD = "param.ssh.password";
    private static final String SSH_PORT = "param.ssh.port";

    private static final String SCRIPT_DIR = "scripts";


    @Autowired
    private CMDBService cmdbService;

    @Override
    public IndicatorValue collect(Indicator indicator) {
        if (Objects.isNull(cmdbService)) {
            cmdbService = SpringContextUtil.getBean(CMDBService.class);
        }
        Res res = cmdbService.getResByIndicator(indicator.getName());
        IndicatorValue iv = null;
        if (checkSshInfo(res)) {
            Map<String, String> params = cmdbService.getResParamsByName(res.getName());
            String username = params.get(SSH_USERNAME);
            String password = params.get(SSH_PASSWORD);
            Integer port = Integer.parseInt(params.get(SSH_PORT));
            JSchExecutor jSchExecutor = null;
            String remoteDir = null;
            String remoteScript = null;
            try {
                jSchExecutor = new JSchExecutor(username, password, res.getHost(), port);
                jSchExecutor.connect();
                remoteDir = jSchExecutor.getHome() + "/scriptTemp";
                remoteScript = indicator.getDefName() + ".sh";
                jSchExecutor.uploadFile(SCRIPT_DIR + File.separator + indicator.getDefName() + ".sh", remoteDir + "/" + remoteScript);
                List<String> returnMsg = jSchExecutor.execCmd("sh " + remoteDir + "/" + remoteScript);
                iv = handleSshReturnMsg(returnMsg, indicator);
            } catch (Exception e) {
                log.error("SSHCOll {} e: {}", indicator.getName(), e.toString());
                iv = new IndicatorValue();
                iv.setFailureResult(e.getMessage());
            } finally {
                try {
                    if (jSchExecutor.getSession().isConnected()) {
                        String scriptPath = remoteDir + "/" + remoteScript;
                        jSchExecutor.rm(remoteDir, new String[]{remoteScript});
                        jSchExecutor.disconnect();
                    }
                } catch (Exception e) {
                    log.error("remove {}.sh exception:{}", indicator.getDefName(), e.toString());
                }
            }
        } else {
            iv = new IndicatorValue();
            iv.setFailureResult("params of res " + res.getName() + " is unable to ssh collect,");
        }
        return iv;
    }

    protected boolean checkSshInfo(Res res) {
        Map<String, String> params = cmdbService.getResParamsByName(res.getName());
        if (params.containsKey(SSH_USERNAME) && params.containsKey(SSH_PASSWORD) && params.containsKey(SSH_PORT)) {
            return true;
        }
        return false;
    }

    protected IndicatorValue handleSshReturnMsg(List<String> retrunMsg, Indicator indicator) {
        if (retrunMsg.size() <= 0) {
            return null;
        }
        IndicatorValue iv = new IndicatorValue();
        //格式必须按行输出，并且返回值和采集结果要key=value的形式
        boolean canParse = true;
        for (int i = 0; i < retrunMsg.size(); i++) {
            if (!StringUtils.contains(retrunMsg.get(i), "=")) {
                canParse = false;
                break;
            }
        }
        if (!canParse) {
            iv.setFailureResult("parse returnMsg error");
            iv.addValue("statusDesc", iv.getStatusDesc());
        } else {
            Map<String, MetricDefine> ms = DefineProxy.getCoDefineMetric(indicator.getDefName());
            for (int i = 0; i < retrunMsg.size(); i++) {
                String[] kv = retrunMsg.get(i).split("=");
                String key = kv[0];
                String val = kv[1];
                if (ms.containsKey(key) || "statusDesc".equals(key)) {
                    iv.addValue(key, val);
                }
            }
        }
        return iv;
    }
}
