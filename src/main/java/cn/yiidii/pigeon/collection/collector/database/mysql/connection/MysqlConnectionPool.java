package cn.yiidii.pigeon.collection.collector.database.mysql.connection;

import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

import java.sql.Connection;
import java.util.Objects;

/**
 * Mysql连接池
 */
public class MysqlConnectionPool extends GenericKeyedObjectPool<MysqlConfig, Connection> {

    private static GenericKeyedObjectPool<MysqlConfig, Connection> pool = null;

    public MysqlConnectionPool(KeyedPooledObjectFactory<MysqlConfig, Connection> factory, GenericKeyedObjectPoolConfig<Connection> config) {
        super(factory, config);
    }

    public synchronized static GenericKeyedObjectPool<MysqlConfig, Connection> getInstance() {
        if (Objects.isNull(pool)) {
            initPool();
        }
        return pool;
    }

    public static void initPool() {
        MysqlConnectionFactory factory = new MysqlConnectionFactory();
        GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
        config.setMaxIdlePerKey(10);
        config.setMaxTotalPerKey(100);
        config.setMaxTotal(500);
        config.setMinIdlePerKey(10);
        config.setTestOnBorrow(true);
        config.setTestOnReturn(true);

        pool = new MysqlConnectionPool(factory, config);
    }
}
