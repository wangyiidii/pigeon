package cn.yiidii.pigeon.collection.collector.database.mysql.connection;

import lombok.Data;

import java.util.Date;

/**
 * Mysql连接配置
 */
@Data
public class MysqlConfig {
    private String host;
    private Integer port = 3306;
    private String username;
    private String password;
    private String database;
    private Date startTime;

    public MysqlConfig(String host, Integer port, String username, String password, String database) {
        this.host = host;
        this.port = port;
        this.username = username;
        this.password = password;
        this.database = database;
        this.startTime = new Date();
    }
}