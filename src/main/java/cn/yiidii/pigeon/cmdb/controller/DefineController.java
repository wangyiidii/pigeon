package cn.yiidii.pigeon.cmdb.controller;

import cn.yiidii.pigeon.cmdb.codefine.DefineProxy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("define")
@Slf4j
public class DefineController {

    @GetMapping("/all")
    public Object getAllDefine(@RequestParam("extend") String extend) {
        return DefineProxy.getCODefineByExtend(extend);
    }

}
