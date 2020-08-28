package cn.yiidii.pigeon.collection.perf.influx;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.influxdb.InfluxDB;
import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.InfluxDBFactory;
import org.influxdb.dto.*;
import org.influxdb.dto.Point.Builder;
import org.influxdb.dto.QueryResult.Result;
import org.influxdb.dto.QueryResult.Series;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Data
@NoArgsConstructor
@Component
public class InfluxDBConnection {
    private static final Object OBJECT = new Object();
    // 用户名
    @Value("${influx.username}")
    private String username;
    // 密码
    @Value("${influx.password}")
    private String password;
    // 连接地址
    @Value("${influx.openurl}")
    private String openurl;
    // 数据库
    @Value("${influx.database}")
    private String database;
    // 保留策略
    @Value("${influx.retentionPolicy}")
    private String retentionPolicy;

    private InfluxDB influxdb;

    public InfluxDBConnection(String username, String password, String openurl,
                              String database, String retentionPolicy) {
        this.username = username;
        this.password = password;
        this.openurl = openurl;
        this.database = database;
        this.retentionPolicy = retentionPolicy;
//        influxDbBuild();
    }

    /**
     * 连接时序数据库 ，若不存在则创建
     *
     * @return
     */
    public InfluxDB influxDbBuild() {
        if (influxdb == null) {
            synchronized (OBJECT) {
                influxdb = InfluxDBFactory.connect(openurl, username, password);
            }
        } else if (!ping()) {
            synchronized (OBJECT) {
                influxdb = InfluxDBFactory.connect(openurl, username, password);
            }
        }
//        if (!isDBExist(database)) {
//            createDB(database);
//            System.out.println("influxDB [" + database
//                    + "]is not exist and creating...");
//        }
        influxdb.setLogLevel(InfluxDB.LogLevel.NONE);
        return influxdb;
    }

    /**
     * 创建自定义保留策略
     *
     * @param policyName  策略名
     * @param duration    保存天数
     * @param replication 保存副本数量
     * @param isDefault   是否设为默认保留策略
     */
    public void createRetentionPolicy(String policyName, String duration,
                                      int replication, Boolean isDefault) {
        String sql = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s ",
                policyName,
                database,
                duration,
                replication);
        if (isDefault) {
            sql = sql + " DEFAULT";
        }
        this.query(sql);
    }

    /**
     * 修改自定义保留策略
     *
     * @param policyName  策略名
     * @param duration    保存天数
     * @param replication 保存副本数量
     * @param isDefault   是否设为默认保留策略
     */
    public void updateRetentionPolicy(String policyName, String duration,
                                      int replication, Boolean isDefault) {
        String sql = String.format("ALTER RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s ",
                policyName,
                database,
                duration,
                replication);
        if (isDefault) {
            sql = sql + " DEFAULT";
        }
        this.query(sql);
    }

    /**
     * 创建连续性策略
     *
     * @param indicatorName
     */
    public void createContinuousQueryPolicy(String indicatorName) {

    }

    /**
     * 插入
     *
     * @param measurement 表
     * @param tags        标签
     * @param fields      字段
     */
    public void insert(String measurement, Map<String, String> tags,
                       Map<String, Object> fields, long time, TimeUnit timeUnit) {
        Builder builder = Point.measurement(measurement);
        // builder.tag(tags);
        builder.fields(fields);
        if (0 != time) {
            builder.time(time, timeUnit);
        }
        influxdb.write(database, retentionPolicy, builder.build());
    }

    /**
     * 批量写入测点
     *
     * @param batchPoints
     */
    public void batchInsert(BatchPoints batchPoints) {
        influxdb.write(batchPoints);
        // influxDB.enableGzip();
        // influxDB.enableBatch(2000,100,TimeUnit.MILLISECONDS);
        // influxDB.disableGzip();
        // influxDB.disableBatch();
    }

    /**
     * 批量写入数据
     *
     * @param database        数据库
     * @param retentionPolicy 保存策略
     * @param consistency     一致性
     * @param records         要保存的数据（调用BatchPoints.lineProtocol()可得到一条record）
     */
    public void batchInsert(final String database,
                            final String retentionPolicy, final ConsistencyLevel consistency,
                            final List<String> records) {
        influxdb.write(database, retentionPolicy, consistency, records);
    }

    /**
     * 查询
     *
     * @param command 查询语句
     * @return
     */
    public QueryResult query(String command) {
        return influxdb.query(new Query(command, database));
    }

    /**
     * 删除
     *
     * @param command 删除语句
     * @return 返回错误信息
     */
    public String deleteMeasurementData(String command) {
        QueryResult result = influxdb.query(new Query(command, database));
        return result.getError();
    }

    /**
     * 创建默认的保留策略
     */
    public void createDefaultRetentionPolicy() {
        String command = String.format("CREATE RETENTION POLICY \"%s\" ON \"%s\" DURATION %s REPLICATION %s DEFAULT",
                "default",
                database,
                "30d",
                1);
        this.query(command);
    }

    /**
     * 构建Point
     *
     * @param measurement
     * @param time
     * @param fields
     * @return
     */
    public Point pointBuilder(String measurement, long time,
                              Map<String, String> tags, Map<String, Object> fields) {
        Point point = Point.measurement(measurement)
                .time(time, TimeUnit.MILLISECONDS)
                .tag(tags)
                .fields(fields)
                .build();
        return point;
    }

    public String getVersion() {
        return influxdb.version();
    }

    /**
     * 获取所有measurement（表，对应某一个指标）
     *
     * @return
     */
    public List<String> getMeasurements() {
        List<String> measurements = new ArrayList<String>();
        QueryResult qr = query("show measurements");
        List<Result> results = qr.getResults();
        List<Series> series = results.get(0).getSeries();
        if (series == null) {
            return measurements;
        }
        for (Series serie : series) {
            List<String> columns = serie.getColumns();
            List<List<Object>> values = serie.getValues();
            for (int i = 0; i < values.size(); i++) {
                for (int j = 0; j < columns.size(); j++) {
                    measurements.add((String) values.get(i).get(j));
                }
            }
        }
        return measurements;
    }

    public List<String> getColumnNames(String measurement) {
        List<String> columnNames = new ArrayList<String>();
        String sql = "SELECT * FROM \"" + measurement + "\"";
        QueryResult qr = query(sql);
        List<Result> results = qr.getResults();
        if (results == null) {
            return columnNames;
        }
        List<Series> series = results.get(0).getSeries();
        if (null == series) {
            return columnNames;
        }
        for (Series serie : series) {
            columnNames = serie.getColumns();
        }
        return columnNames;
    }

    /**
     * 创建数据库
     *
     * @param dbName
     */
    public void createDb(String dbName) {
        influxdb.createDatabase(dbName);
    }

    /**
     * 删除数据库
     *
     * @param dbName
     */
    public void deleteDb(String dbName) {
        influxdb.deleteDatabase(dbName);
    }

    /**
     * 测试连接是否正常
     *
     * @return true 正常
     */
    public boolean ping() {
        boolean isConnected = false;
        Pong pong;
        try {
            pong = influxdb.ping();
            if (pong != null) {
                isConnected = true;
            }
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return isConnected;
    }

    /**
     * 数据库是否存在
     */
    public boolean isDbExist(String db) {
        List<List<Object>> dbs = this.query("show databases")
                .getResults()
                .get(0)
                .getSeries()
                .get(0)
                .getValues();
        boolean isDbExist = false;
        for (List<Object> list : dbs) {
            for (Object object : list) {
                if (StringUtils.equals(String.valueOf(object), db)) {
                    isDbExist = true;
                    break;
                }
            }
        }
        return isDbExist;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        influxdb.close();
    }

}
