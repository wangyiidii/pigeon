package cn.yiidii.pigeon.cmdb.controller;

import cn.yiidii.pigeon.base.vo.Result;
import cn.yiidii.pigeon.base.vo.ResultCodeEnum;
import cn.yiidii.pigeon.cmdb.controller.form.ResInsertForm;
import cn.yiidii.pigeon.cmdb.entity.Res;
import cn.yiidii.pigeon.cmdb.service.impl.CMDBService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("res")
@Slf4j
@Api(tags = "资源")
@Validated
public class ResController {

    @Autowired
    private CMDBService cmdbService;

    @GetMapping("/all")
    @ApiOperation(value = "获取所有资源")
    public Object getAllRes() {
        return cmdbService.getAllRes();
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加资源")
    public Object addRes(@RequestBody @Valid ResInsertForm resInsertForm) {
        Res res = new Res();
        Map<String, String> paramsMap = resInsertForm.getParams();
        BeanUtils.copyProperties(resInsertForm, res);
        Integer row = cmdbService.addRes(res, paramsMap);
        if (row == 1) {
            return Result.success(ResultCodeEnum.SUCCESS);
        }
        return Result.error(ResultCodeEnum.OPT_FAIL);
    }

}
