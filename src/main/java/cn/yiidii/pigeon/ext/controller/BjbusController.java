package cn.yiidii.pigeon.ext.controller;

import cn.yiidii.pigeon.annotation.OptLogAnnotation;
import cn.yiidii.pigeon.ext.mgr.BJBusUtil;
import cn.yiidii.pigeon.ext.controller.form.BjBusForm;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/third/bjBus")
@Api(tags = "北京公交")
public class BjbusController {

    @GetMapping(value = "realTime")
    @OptLogAnnotation(desc = "查询北京实时公交")
    @ApiOperation(value = "查询北京实时公交")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "bid", value = "公交线路ID", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "rid", value = "公交方向ID", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "sid", value = "公交站点ID", required = true)})
    public Object getRealTimeBus(@Validated BjBusForm bjBusForm)
            throws Exception {
        return BJBusUtil.getRealTimeBus(bjBusForm.getBid(), bjBusForm.getRid(), bjBusForm.getSid());
    }

    @GetMapping(value = "bus")
    @ApiOperation(value = "北京公交线路列表")
    public Object getBuses() throws Exception {
        return BJBusUtil.getBuses();
    }

    @GetMapping(value = "row")
    @ApiOperation(value = "北京公交方向列表", notes = "需要指定公交线路")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "bid", value = "公交线路ID", required = true),
    })
    public Object getRows(@RequestParam(name = "bid", required = true) String bid) throws Exception {
        return BJBusUtil.getRows(bid);
    }

    @GetMapping(value = "station")
    @ApiOperation(value = "北京公交站点列表", notes = "需要指定公交线路和方向")
    @ApiImplicitParams({@ApiImplicitParam(paramType = "query", dataType = "String", name = "bid", value = "公交线路ID", required = true),
            @ApiImplicitParam(paramType = "query", dataType = "String", name = "sid", value = "公交站点ID", required = true)})
    public Object getStations(@RequestParam(name = "bid", required = true) String bid,
                              @RequestParam(name = "rid", required = true) String rid) throws Exception {
        return BJBusUtil.getStations(bid, rid);
    }

}
