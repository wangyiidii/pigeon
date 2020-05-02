package cn.yiidii.pigeon.ext.controller;

import cn.yiidii.pigeon.annotation.OptLogAnnotation;
import cn.yiidii.pigeon.base.vo.Result;
import cn.yiidii.pigeon.common.util.ServerInfo;
import cn.yiidii.pigeon.common.util.ServerUtil;
import cn.yiidii.pigeon.common.util.mail.dto.ServerInfoModel;
import cn.yiidii.pigeon.common.util.mail.service.impl.MailService;
import cn.yiidii.pigeon.ext.mgr.YiYan;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/common")
@Api(tags = "开放API")
public class OpenController {

    @Autowired
    private ServerUtil serverUtil;
    @Autowired
    private MailService mailService;

    @GetMapping("/yiyan")
    @ApiOperation(value = "一言接口")
    @OptLogAnnotation
    public Object randomYiYan() {
        String[] yiyan = YiYan.randomYiYan();
        JSONObject jo = new JSONObject();
        jo.put("content", yiyan[0]);
        jo.put("from", yiyan[1]);
        return jo;
    }

    @GetMapping("/addr")
    @OptLogAnnotation
    @ApiOperation(value = "当前服务器地址", notes = "http://ip:port/")
    public Object address() {
        String url = serverUtil.getUrl();
        JSONObject jo = new JSONObject();
        jo.put("addr", url);
        return jo;
    }

    @GetMapping("/serverHealth")
    @OptLogAnnotation
    @ApiOperation(value = "", notes = "")
    public Object serverHealth(@RequestParam String email) throws Exception {
        ServerInfoModel serverInfoModel = new ServerInfoModel();
        double cpuUtil = ServerInfo.cpu();
        double memUtil = ServerInfo.memory();
        String mainHeader = "服务器运行正常";
        serverInfoModel.setCpuUtil(cpuUtil);
        serverInfoModel.setMemUtil(memUtil);
        if (cpuUtil > 60) {
            mainHeader = "CPU使用率过高: " + cpuUtil + "%";
        } else if (memUtil > 60) {
            mainHeader = "内存使用率过高: " + memUtil + "%";
        }
        serverInfoModel.setTitle(mainHeader);
        serverInfoModel.setMainHeader(mainHeader);

        mailService.sendTemplateHtmlMail(mainHeader, "", new String[]{email}, "serverInfoTemplate.ftl", serverInfoModel);
        return Result.success("发送成功");
    }

}
