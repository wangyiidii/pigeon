Pigeon
----------

pigeon, 记录了自己写的小项目，以女票的名纸命名~

# 已完成的功能
基于 `springboot` + `maven` ，提供接口，前后端分离

安全相关：
- [x] `shiro` 权限管理（shiro魔改前后端分离）
- [x] `JWT` 无状态身份认证

CMDB模块（JUC）
- [x] CMDB模块（XML建模，拓展指标需定义资源指标模型并实现采集器接口）
- [x] 支持的采集方式：`SNMP`、`SSH`、`HTTP`、`JDBC`、`JAVA`
- [x] 支持的设备：主机设备（Linux，Windows），中间件（Tomcat），增加ing...
- [x] 探针监控设备基本性能（Netty4）。[Agent4Pigeon](https://github.com/wangyiidii/Agent4Pigeon)

功能相关
- [x] `Quartz` 任务调度（已实现北京联通双签和全民K歌鲜花签到）
- [x] 北京公交查询
- [x] `Freemarker` 实现邮件模板的静态化
- [x] `Logback` 记录系统运行日志
- [x] `AOP` 方式记录操作日志和分页查询
- [x] `swagger` 接口文档
- [x] `Hibernate validator` 参数校验
- [x] `kinfe4j` 接口文档
- [x] `RabbitMQ` 邮件发送 

未实现的
- [ ] 限制接口的调用频率

待优化的
- [ ] 采集线程池的优化
- [ ] Security模块和CMDB模块密码加密存储
- [ ] CMDB模块查询和Perf数据存储时间性能优化（Redis 或 缓存）
- [ ] 完善采集方式：`JMX`
- [ ] 采集增加重试次数


# 部分截图

**北京公交**

![北京公交](https://blog-bucket.yiidii.cn/blog/bjbus.png)

**邮件模板**

![邮件模板](https://blog-bucket.yiidii.cn/blog/serverHealthEmail.jpg)

**操作日志**

![操作日志](https://blog-bucket.yiidii.cn/blog/optlog.png)

**接口文档**

![接口文档](https://blog-bucket.yiidii.cn/blog/kinfe4j.png)
