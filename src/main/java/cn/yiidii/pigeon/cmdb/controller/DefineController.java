package cn.yiidii.pigeon.cmdb.controller;

import cn.yiidii.pigeon.cmdb.codefine.COConstant;
import cn.yiidii.pigeon.cmdb.codefine.DefineProxy;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("codefine")
@Slf4j
@Api(tags = "资源定义")
public class DefineController {

    @GetMapping("/all")
    @ApiOperation(value = "获取所有资源资源定义")
    public Object getAllDefine(@RequestParam("extend") String extend) {
        if (StringUtils.isBlank(extend)) {
            extend = COConstant.CO_ROOT;
        }
        return DefineProxy.getCoDefineByExtend(extend);
    }

}
