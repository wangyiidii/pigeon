package cn.yiidii.pigeon.optlog.controller;

import cn.yiidii.pigeon.common.util.page.PageUtil;
import cn.yiidii.pigeon.common.util.page.dto.PageResult;
import cn.yiidii.pigeon.optlog.entity.OptLog;
import cn.yiidii.pigeon.optlog.service.impl.OptLogService;
import cn.yiidii.pigeon.shiro.entity.User;
import cn.yiidii.pigeon.shiro.util.SecurityUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@RestController
@Api(tags = "操作日志")
public class OptLogController {

    @Autowired
    public OptLogService optLogService;
    @Autowired
    private SecurityUtil securityUtil;

    @GetMapping("/optlog")
    @ResponseBody
    @ApiOperation(value = "查询操作日志")
    public Object getOptLog(Integer page, Integer pageSize) {
        User user = securityUtil.getCurrUser();
        PageResult<OptLog> optLogs = null;
        if (Objects.isNull(user)) {
            optLogs = (PageResult<OptLog>) optLogService.queryLogWithoutUid(page, pageSize);
        } else {
            optLogs = (PageResult<OptLog>) optLogService.queryLogByUid(user.getId(), page, pageSize);
        }
        return optLogs;
    }

}
