package cn.yiidii.pigeon.test;

import cn.yiidii.pigeon.collection.collector.database.mysql.connection.MysqlConfig;
import cn.yiidii.pigeon.collection.collector.database.mysql.connection.MysqlConnectionPool;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.util.CollectionUtils;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * <p>
 *
 * </p>
 *
 * @author: YiiDii Wang
 * @create: 2020-11-28 11:19
 */
@Slf4j
public class TestTencent {

    private static String SQL_INSERT_QQ = "INSERT INTO qq01 (`qq`, `phone`) VALUES (?, ?)";

    private static final Integer EACH_HANDLE_SIZE = 10 * 10000;
    private static final Integer EACH_PERSIST_SIZE = 1 * 10000;

    public static void main(String[] args) throws Exception {
        asyncPersist();
    }

    private static void asyncPersist() throws Exception {
        int cpuCount = Runtime.getRuntime().availableProcessors();
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(cpuCount + 1);
        executor.setMaxPoolSize(cpuCount * 2 + 1);
        executor.setQueueCapacity(100);
        executor.setKeepAliveSeconds(10);
        executor.setThreadNamePrefix("tencentPool-%s");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());// 由调用线程（提交任务的线程）处理该任务
        executor.initialize();
        log.info("tencentPool init... : {}", JSONObject.toJSONString(executor));

        String fileName = "D:\\download\\QBang_F_8e_1105\\QBang_F_8e\\总库0.txt";
        BufferedReader br = new BufferedReader(new FileReader(fileName));
        String line = null;
        List<String> lines = new ArrayList<>();
        int index = 1;
        while ((line = br.readLine()) != null) {
            if (index % EACH_HANDLE_SIZE == 0) {
                lines.add(line);
                List<String> newLines = new ArrayList<>(lines);
                lines.clear();
                executor.submit(() -> {
                    try {
                        long start = System.currentTimeMillis();
                        write2Db(newLines);
                        long taked = System.currentTimeMillis() - start;
                        log.info("persist {} time taked {}ms", newLines.size(), taked);
                    } catch (Exception e) {
                        log.error("写入异常: {}", e.getMessage());
                    }
                });
            } else {
                lines.add(line);
            }
            index++;
        }
        executor.shutdown();
    }

    private static void write2Db(List<String> lines) throws Exception {
        if (CollectionUtils.isEmpty(lines)) {
            return;
        }
        String user = "root";
        String password = "114130";
        MysqlConfig config = new MysqlConfig("127.0.0.1", 3306, user, password, "tencent_info");
        GenericKeyedObjectPool<MysqlConfig, Connection> pool = MysqlConnectionPool.getInstance();
        Connection connection = null;
        try {
            connection = pool.borrowObject(config);
            connection.setAutoCommit(false);
            PreparedStatement ps = connection.prepareStatement(SQL_INSERT_QQ);
            for (int i = 0; i < lines.size(); i++) {
                String line = lines.get(i);
                if (StringUtils.isBlank(line)) continue;
                String[] qqAndPhone = line.split("----");
                if (ArrayUtils.isEmpty(qqAndPhone) || qqAndPhone.length < 2) continue;
                ps.setString(1, qqAndPhone[0]);
                ps.setString(2, qqAndPhone[1]);
                ps.addBatch();
                if (i % EACH_PERSIST_SIZE == 0) {
                    ps.executeBatch();
                    connection.commit();
                }
            }
            ps.executeBatch();
            connection.commit();
        } catch (Exception e) {
            pool.returnObject(config, connection);
        } finally {
            if (!Objects.isNull(connection)) {
                pool.returnObject(config, connection);
            }
        }
    }
}
