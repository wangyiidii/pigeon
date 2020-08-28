package cn.yiidii.pigeon.collection.collector.generic;

import cn.yiidii.pigeon.agent.netty.Agent;
import cn.yiidii.pigeon.agent.netty.AgentPool;
import cn.yiidii.pigeon.agent.netty.AgentRequestCache;
import cn.yiidii.pigeon.agent.netty.CMDType;
import cn.yiidii.pigeon.agent.netty.dto.AgentRequest;
import cn.yiidii.pigeon.agent.netty.dto.AgentResponse;
import cn.yiidii.pigeon.agent.netty.util.ScriptLoadUtil;
import cn.yiidii.pigeon.cmdb.codefine.DefineProxy;
import cn.yiidii.pigeon.cmdb.codefine.IndicatorDefine;
import cn.yiidii.pigeon.cmdb.codefine.MetricDefine;
import cn.yiidii.pigeon.cmdb.codefine.ParamDefine;
import cn.yiidii.pigeon.cmdb.dto.IndicatorValue;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.entity.Res;
import cn.yiidii.pigeon.cmdb.service.impl.CMDBService;
import cn.yiidii.pigeon.collection.CollectionConstant;
import cn.yiidii.pigeon.collection.collector.ICollector;
import cn.yiidii.pigeon.common.util.GzipUtil;
import cn.yiidii.pigeon.common.util.server.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import java.util.*;

@Component
@Slf4j
public class AgentCollector implements ICollector {

    @Override
    public IndicatorValue collect(Indicator indicator) {
        CMDBService cmdbService = SpringContextUtil.getBean(CMDBService.class);
        Res res = cmdbService.getResByIndicator(indicator.getName());
        String host = res.getHost();
        Agent agent = AgentPool.getInstance().getAgent(host);
        IndicatorValue iv = new IndicatorValue();
        if (Objects.isNull(agent)) {
            iv.setFailureResult(CollectionConstant.AGENT_DISCONNECTED);
            return iv;
        }
        //下发脚本
        String id = issuedScript(indicator);
        AgentRequest request = getAgentRequestById(id, indicator.getTimeout());
        boolean isIssuedSuccess = Objects.isNull(request) || (!Objects.isNull(request) && request.isSuccess());
        if (!isIssuedSuccess) {
            iv.setFailureResult(CollectionConstant.ISSUED_SCRIPT_TIMEOUT);
            return iv;
        }

        //采集
        id = executeCollect(indicator);
        request = getAgentRequestById(id, indicator.getTimeout());
        //log.info("executeCollect request: {}", request);
        boolean isCollSuccess = Objects.isNull(request) || (!Objects.isNull(request) && request.isSuccess());
        if (isCollSuccess) {
            return parseIndicatorValue(indicator, request.getOutput());
        } else {
            iv.setFailureResult(CollectionConstant.COLLECT_FAIL);
            return iv;
        }
    }

    /**
     * 下发脚本
     *
     * @param indicator
     * @return 唯一id，后续用来判断是否下发成功（成功返回request）
     */
    private String issuedScript(Indicator indicator) {
        String id = UUID.randomUUID().toString();
        try {
            IndicatorDefine indicatorDefine = DefineProxy.getIndDefineByName(indicator.getDefName());
            String scriptName = indicatorDefine.getCollScript();

            AgentResponse response = new AgentResponse();
            response.setId(id);
            response.setFileName(scriptName);
            response.setCmdType(CMDType.ISSUED_FILE);
            response.setAttachment(GzipUtil.gzip(ScriptLoadUtil.getFileByte(scriptName)));

            CMDBService cmdbService = SpringContextUtil.getBean(CMDBService.class);
            Res res = cmdbService.getResByIndicator(indicator.getName());
            Agent agent = AgentPool.getInstance().getAgent(res.getHost());
            agent.getCtx().writeAndFlush(response);
        } catch (Exception e) {

        }
        return id;
    }

    private String executeCollect(Indicator indicator) {
        AgentResponse response = new AgentResponse();
        IndicatorDefine indicatorDefine = DefineProxy.getIndDefineByName(indicator.getDefName());
        String id = UUID.randomUUID().toString();
        String scriptName = indicatorDefine.getCollScript();
        response.setId(id);
        response.setCmdType(CMDType.DO_WIN_SCRIPT);
        response.setFileName(scriptName);
        response.setInvokeParam(getCollParams(indicator));

        CMDBService cmdbService = SpringContextUtil.getBean(CMDBService.class);
        Res res = cmdbService.getResByIndicator(indicator.getName());
        Agent agent = AgentPool.getInstance().getAgent(res.getHost());
        agent.getCtx().writeAndFlush(response);
        return id;
    }

    private IndicatorValue parseIndicatorValue(Indicator indicator, List<String> output) {
        IndicatorDefine indicatorDefine = DefineProxy.getIndDefineByName(indicator.getDefName());
        Map<String, MetricDefine> defineMap = indicatorDefine.getMetrics();
        IndicatorValue iv = new IndicatorValue();
        output.forEach(line -> {
            String[] kv = line.split("=");
            String key = kv[0];
            String value = kv[1];
            if (defineMap.containsKey(key) || StringUtils.equals("statusDesc", key)) {
                iv.addValue(key, value);
            }
        });
        return iv;
    }

    /**
     * @param id      唯一id
     * @param timtout 超时时间
     * @return request，可能为空
     */
    private AgentRequest getAgentRequestById(String id, long timeout) {
        AgentRequest request = null;
        long start = System.currentTimeMillis() / 1000;
        while (true) {
            request = AgentRequestCache.getAndRmRequest(id);
            if (!Objects.isNull(request)) {
                break;
            }
            long pass = System.currentTimeMillis() / 1000 - start;
            if (pass >= timeout) {
                break;
            }
        }
        return request;
    }

    /**
     * 获取指标采集参数
     *
     * @param indicator
     * @return
     */
    private String[] getCollParams(Indicator indicator) {
        IndicatorDefine indicatorDefine = DefineProxy.getIndDefineByName(indicator.getDefName());
        Map<String, ParamDefine> paramDefineMap = indicatorDefine.getParams();
        CMDBService cmdbService = SpringContextUtil.getBean(CMDBService.class);
        Map<String, String> params = cmdbService.getIndParamByName(indicator.getName());
        List<String> paramList = new LinkedList<>();
        paramDefineMap.forEach((k, v) -> {
            if (params.containsKey(k)) {
                paramList.add(params.get(k));
            }
        });
        String[] paramArr = new String[paramList.size()];
        return paramList.toArray(paramArr);
    }

}
