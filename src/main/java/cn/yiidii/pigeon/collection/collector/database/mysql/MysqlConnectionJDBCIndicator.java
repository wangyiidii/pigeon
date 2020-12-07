package cn.yiidii.pigeon.collection.collector.database.mysql;

import cn.yiidii.pigeon.cmdb.dto.IndicatorValue;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.cmdb.entity.Res;
import cn.yiidii.pigeon.cmdb.service.impl.CMDBService;
import cn.yiidii.pigeon.collection.collector.ICollector;
import cn.yiidii.pigeon.collection.collector.database.mysql.connection.MysqlConfig;
import cn.yiidii.pigeon.collection.collector.database.mysql.connection.MysqlConnectionPool;
import cn.yiidii.pigeon.common.util.server.SpringContextUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Map;
import java.util.Objects;

@Slf4j
public class MysqlConnectionJDBCIndicator implements ICollector {

    private static final String JDBC_PORT = "param.port";
    private static final String JDBC_USERNAME = "param.username";
    private static final String JDBC_PASSWORD = "param.password";
    private static final String METRIC_CONNECTIONNUM = "ConnectionNum";
    private static final String METRIC_ABORTEDCONECTIONNUM = "AbortedConnectionNum";

    private static final String DATABASE = "mysql";


    @Override
    public IndicatorValue collect(Indicator indicator) {
        IndicatorValue iv = new IndicatorValue();
        CMDBService cmdbService = SpringContextUtil.getBean(CMDBService.class);
        Res res = cmdbService.getResByIndicator(indicator.getName());
        String host = res.getHost();
        Map<String, String> params = cmdbService.getResParamsByName(res.getName());
        if (!checkJDBCParam(params)) {
            iv.setFailureResult("JDBC参数不正确");
            return iv;
        }
        String username = params.get(JDBC_USERNAME);
        String password = params.get(JDBC_PASSWORD);
        Integer port = Integer.parseInt(params.get(JDBC_PORT));
        MysqlConfig config = new MysqlConfig(host, port, username, password, DATABASE);
        GenericKeyedObjectPool<MysqlConfig, Connection> pool = MysqlConnectionPool.getInstance();
        Connection connection = null;
        try {
            connection = pool.borrowObject(config);
            Statement ps = connection.createStatement();
            ResultSet rs = ps.executeQuery("show status");
            while (rs.next()) {
                String column = rs.getString(1);
                if ("Connections".equals(column)) {
                    iv.addValue(METRIC_CONNECTIONNUM, rs.getInt(2));
                    continue;
                }
                if ("Aborted_connects".equals(column)) {
                    iv.addValue(METRIC_ABORTEDCONECTIONNUM, rs.getInt(2));
                    continue;
                }
            }
        } catch (Exception e) {
            pool.returnObject(config, connection);
            log.error("collector ind {} of {} failed。e:{}", indicator.getName(), res.getHost(), e.toString());
            log.error("{}", e);
            iv.setFailureResult("采集失败");
            return iv;
        } finally {
            if (!Objects.isNull(connection)) {
                pool.returnObject(config, connection);
            }
        }
        String connNum = iv.getValue(METRIC_CONNECTIONNUM);
        iv.addValue("statusDesc", "当前连接数: " + connNum);
        return iv;
    }

    private boolean checkJDBCParam(Map<String, String> params) {
        if (params.containsKey(JDBC_PORT) && params.containsKey(JDBC_USERNAME) && params.containsKey(JDBC_PASSWORD)) {
            return true;
        }
        return false;
    }

}
