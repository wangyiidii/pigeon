package cn.yiidii.pigeon.lovebook.controller;

import cn.yiidii.pigeon.annotation.OptLogAnnotation;
import cn.yiidii.pigeon.common.util.server.IPUtil;
import cn.yiidii.pigeon.lovebook.entity.LoveBook;
import cn.yiidii.pigeon.lovebook.service.impl.LoveBookService;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/loveBook")
@Slf4j
//@Api(tags = "小本本")
public class LoveBookController {

    @Autowired
    private LoveBookService loveBookService;

    @GetMapping("/all")
    @ApiOperation(value = "获取所有的小本本条目")
    @OptLogAnnotation
    public Object getAllLoveBook() {
        List<LoveBook> loveBooks = loveBookService.queryAllLoveBook();
        JSONArray ja = (JSONArray) JSONObject.toJSON(loveBooks);
        return ja;
    }

    @PostMapping("/")
    @ApiOperation(value = "画个圈圈", notes = "")
    @OptLogAnnotation
    public String insert(@ApiParam("内容") @Validated LoveBook loveBook, HttpServletRequest request) {
        log.info("loveBook: " + loveBook);
        loveBook.setCreateTime(new Date());
        loveBook.setHost(IPUtil.getIpAddr(request));
        loveBook.setUa(IPUtil.getUa(request));
        Integer row = loveBookService.insert(loveBook);
        if (row == 1) {
            return "爱的魔力画圈圈~~";
        }
        return "画圈圈失败了";
    }
}
