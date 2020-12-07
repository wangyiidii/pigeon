package cn.yiidii.pigeon.cmdb.controller;

import cn.yiidii.pigeon.collection.collection.CollectionExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("collection")
@Slf4j
public class CollectionController {
    @GetMapping("/collStatus")
    public Object collStatus() {
        return "CollectionExecutor.getTimerCollectExecutorStatus()";
    }
}
