package cn.yiidii.pigeon.collection.collector.database.mysql.connection;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Date;
import java.util.Objects;

/**
 * Mysql连接工厂
 */
@Slf4j
public class MysqlConnectionFactory implements KeyedPooledObjectFactory<MysqlConfig, Connection> {

    @Override
    public PooledObject<Connection> makeObject(MysqlConfig mysqlConfig) throws Exception {
        System.out.println("makeObject: " + mysqlConfig.getHost());
        Connection connection = null;
        try {
            String driver = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://" + mysqlConfig.getHost() + ":" + mysqlConfig.getPort() + "/"
                    + mysqlConfig.getDatabase() + "?characterEncoding=utf8&useSSL=true";
            Class.forName(driver);
            connection = DriverManager.getConnection(url,
                    mysqlConfig.getUsername(),
                    mysqlConfig.getPassword());
        } catch (Exception e) {
            log.error("MysqlConnectionFactory makeObject exception: {}.", e.toString());
        }
        if (!Objects.isNull(connection)) {
            return new DefaultPooledObject<Connection>(connection);
        }
        return null;
    }

    @Override
    public void destroyObject(MysqlConfig mysqlConfig, PooledObject<Connection> pooledObject) throws Exception {
        System.out.println("destroyObject: " + mysqlConfig.getHost());
        Connection conn = pooledObject.getObject();
        if (Objects.isNull(conn)) return;
        if (!conn.isClosed()) {
            conn.close();
        }
    }

    @Override
    public boolean validateObject(MysqlConfig mysqlConfig, PooledObject<Connection> pooledObject) {
        Connection conn = pooledObject.getObject();
        boolean isColsed = false;
        try {
            isColsed = conn.isClosed();
        } catch (SQLException e) {
            isColsed = false;
        }
        boolean isInvalid = false;
        if (!isColsed) {
            Date startTime = mysqlConfig.getStartTime();
            if (System.currentTimeMillis() - startTime.getTime() > 10 * 1000) {
                isInvalid = true;
            }
        }
        System.out.println("validateObject: " + mysqlConfig.getHost() + ",isclosed: " + isColsed + ", isInvalid: " + isInvalid + " [" + (System.currentTimeMillis() - mysqlConfig.getStartTime().getTime()) + "]");
        if (!isColsed || isInvalid) {
            try {
                destroyObject(mysqlConfig, pooledObject);
            } catch (Exception e) {

            }
        }
        return !isColsed || isInvalid;
    }

    @Override
    public void activateObject(MysqlConfig mysqlConfig, PooledObject<Connection> pooledObject) throws Exception {
        System.out.println("activateObject: " + mysqlConfig.getHost());
//        Connection conn = pooledObject.getObject();
//        try {
//            String driver = "com.mysql.jdbc.Driver";
//            String url = "jdbc:mysql://" + mysqlConfig.getHost() + ":" + mysqlConfig.getPort() + "/"
//                    + mysqlConfig.getDatabase() + "?characterEncoding=utf8&useSSL=true";
//            Class.forName(driver);
//            conn = DriverManager.getConnection(url,
//                    mysqlConfig.getUsername(),
//                    mysqlConfig.getPassword());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
    }

    @Override
    public void passivateObject(MysqlConfig mysqlConfig, PooledObject<Connection> pooledObject) throws Exception {
        //no method body
    }
}
