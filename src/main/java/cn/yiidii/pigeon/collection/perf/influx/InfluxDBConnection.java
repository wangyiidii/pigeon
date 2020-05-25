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
    private static final Object obj = new Object();
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

    private InfluxDB influxDB;

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
        if (influxDB == null) {
            synchronized (obj) {
                influxDB = InfluxDBFactory.connect(openurl, username, password);
            }
        } else if (!ping()) {
            synchronized (obj) {
                influxDB = InfluxDBFactory.connect(openurl, username, password);
            }
        }
//        if (!isDBExist(database)) {
//            createDB(database);
//            System.out.println("influxDB [" + database
//                    + "]is not exist and creating...");
//        }
        influxDB.setLogLevel(InfluxDB.LogLevel.NONE);
        return influxDB;
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
        influxDB.write(database, retentionPolicy, builder.build());
    }

    /**
     * 批量写入测点
     *
     * @param batchPoints
     */
    public void batchInsert(BatchPoints batchPoints) {
        influxDB.write(batchPoints);
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
        influxDB.write(database, retentionPolicy, consistency, records);
    }

    /**
     * 查询
     *
     * @param command 查询语句
     * @return
     */
    public QueryResult query(String command) {
        return influxDB.query(new Query(command, database));
    }

    /**
     * 删除
     *
     * @param command 删除语句
     * @return 返回错误信息
     */
    public String deleteMeasurementData(String command) {
        QueryResult result = influxDB.query(new Query(command, database));
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
        return influxDB.version();
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
    public void createDB(String dbName) {
        influxDB.createDatabase(dbName);
    }

    /**
     * 删除数据库
     *
     * @param dbName
     */
    public void deleteDB(String dbName) {
        influxDB.deleteDatabase(dbName);
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
            pong = influxDB.ping();
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
    public boolean isDBExist(String db) {
        List<List<Object>> dbs = this.query("show databases")
                .getResults()
                .get(0)
                .getSeries()
                .get(0)
                .getValues();
        boolean isDBExist = false;
        for (List<Object> list : dbs) {
            for (Object object : list) {
                if (StringUtils.equals(String.valueOf(object), db)) {
                    isDBExist = true;
                    break;
                }
            }
        }
        return isDBExist;
    }

    /**
     * 关闭数据库
     */
    public void close() {
        influxDB.close();
    }

    public static void main(String[] args) {

        InfluxDBConnection influxDBConnection = new InfluxDBConnection("admin",
                "admin",
                "http://127.0.0.1:8086",
                "dbtest",
                null);
        // for (int i = 0; i < 1000; i++) {
        // influxDBConnection.deleteMeasurementData("drop measurement indicator"
        // + i);
        // }
        // BatchPoints batch = BatchPoints.database("dbtest")
        // .retentionPolicy("autogen")
        // .build();
        // for (int i = 0; i < 10; i++) {
        // Point point = Point.measurement("DiskSnmpIndicator61746")
        // .time(System.currentTimeMillis(), TimeUnit.MILLISECONDS)
        // .addField("num", i)
        // .tag("pkg", "pkg_" + i)
        // .tag("statusCode", "statusCode_" + i)
        // .build();
        // batch.point(point);
        // }
        //
        // influxDBConnection.batchInsert(batch);
        // influxDBConnection.insert("DiskSnmpIndicator61746",
        // null,
        // map,
        // new Date().getTime(),
        // TimeUnit.MILLISECONDS);
        QueryResult results = influxDBConnection.query("SELECT * FROM DiskSnmpIndicator61746 order by time desc limit 1000;SELECT max(value) FROM DiskSnmpIndicator61746;SELECT min(value) FROM DiskSnmpIndicator61746;");
        // results.getResults()是同时查询多条SQL语句的返回值。
        for (int i = 0; i < results.getResults().size(); i++) {
            Result oneResult = results.getResults().get(i);
            for (Series s : oneResult.getSeries()) {
                List<String> colunms = s.getColumns();
                for (int j = 0; j < colunms.size(); j++) {
                    for (List<Object> list : s.getValues()) {
                        int k = 0;
                        for (Object object : list) {
                            System.out.println(colunms.get(k) + "\t\t"
                                    + object.toString());
                            k++;
                        }
                    }
                }
            }
        }
    }
}
