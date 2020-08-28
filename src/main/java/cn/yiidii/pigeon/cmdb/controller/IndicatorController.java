package cn.yiidii.pigeon.cmdb.controller;

import cn.yiidii.pigeon.base.vo.Result;
import cn.yiidii.pigeon.base.vo.ResultCodeEnum;
import cn.yiidii.pigeon.cmdb.controller.form.IndicatorInsertForm;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.service.impl.CMDBService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("indicator")
@Slf4j
@Api(tags = "指标")
@Validated
public class IndicatorController {

    @Autowired
    private CMDBService cmdbService;

    @GetMapping("/all/{name}")
    @ApiOperation(value = "获取资源的所有指标")
    public Object getAllIndicatorOfRes(@Validated @NotNull(message = "资源名不能为空") @PathVariable("name") String name) {
        log.info("name:{}", name);
        List<Indicator> inds = cmdbService.getAllIndicatorsOfRes(name);
        JSONArray ja = new JSONArray();
        inds.forEach(ind -> {
            JSONObject jo = (JSONObject) JSON.toJSON(ind);
            ja.add(jo);
        });
        return inds;
    }

    @PostMapping("/add")
    @ApiOperation(value = "添加指标")
    public Object addIndicator(IndicatorInsertForm indicatorInsertForm) {
        Indicator indicator = new Indicator();
        BeanUtils.copyProperties(indicatorInsertForm, indicator);
        Integer rows = cmdbService.addIndicator(indicator);
        if (rows == 1) {
            return Result.success(ResultCodeEnum.SUCCESS);
        }
        return Result.error();
    }

    @PutMapping("/enable")
    @ApiOperation(value = "修改指标状态")
    public Object enableCollectStatus(@Validated @NotNull(message = "name不能为空") String name, @Validated @NotNull(message = "collStatus不能为空") Integer collStatus) {
        Integer rows = cmdbService.updateCollectStatus(name, collStatus);
        if (rows == 1) {
            return Result.success(ResultCodeEnum.SUCCESS);
        }
        return Result.error();
    }

}
