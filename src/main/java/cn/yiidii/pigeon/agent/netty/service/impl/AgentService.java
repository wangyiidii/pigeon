package cn.yiidii.pigeon.agent.netty.service.impl;

import cn.yiidii.pigeon.agent.netty.Agent;
import cn.yiidii.pigeon.agent.netty.AgentPool;
import cn.yiidii.pigeon.agent.netty.dto.AgentResponse;
import cn.yiidii.pigeon.agent.netty.CMDType;
import cn.yiidii.pigeon.agent.netty.service.IAgentService;
import cn.yiidii.pigeon.agent.netty.util.ScriptLoadUtil;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.entity.Res;
import cn.yiidii.pigeon.cmdb.service.impl.CMDBService;
import cn.yiidii.pigeon.common.util.GzipUtil;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class AgentService implements IAgentService {

    @Autowired
    private CMDBService cmdbService;

    @Override
    public List<Agent> getAllAgent() {
        List<Agent> agents = new ArrayList<>();
        AgentPool.getInstance().getPool().forEach((k, v) -> {
            agents.add(v);
        });
        return agents;
    }

    @Override
    public void issuedScript(Indicator indicator) {
        try {
            Res res = cmdbService.getResByIndicator(indicator.getName());
            if (Objects.isNull(res)) {
                return;
            }
            String host = res.getHost();
            Agent agent = AgentPool.getInstance().getAgent(host);
            if (Objects.isNull(agent)) {
                return;
            }
            StringBuilder scriptFileName = new StringBuilder(indicator.getDefName());
            if (StringUtils.equals(res.getDefName(), "Windows")) {
                scriptFileName.append(".vbs");
            }
            if (StringUtils.equals(res.getDefName(), "Linux")) {
                scriptFileName.append(".sh");
            }
            FileInputStream fis = ScriptLoadUtil.getResFileStream(scriptFileName.toString());
            byte[] data = new byte[fis.available()];
            fis.read(data);
            fis.close();
            byte[] compression = GzipUtil.gzip(data);
            AgentResponse response = new AgentResponse();
            response.setCmdType(CMDType.ISSUED_FILE);
            response.setAttachment(compression);

            agent.getCtx().writeAndFlush(response);
        } catch (FileNotFoundException fne) {

        } catch (IOException ioe) {

        }

    }

}
