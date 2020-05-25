package cn.yiidii.pigeon.collection.perf.influx;

import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import java.util.Objects;

public class InfluxDBPool extends GenericObjectPool<InfluxDBConnection> {

    private static InfluxDBPool instance = null;

    public static synchronized InfluxDBPool getInstance() {
        if (Objects.isNull(instance)) {
            initPool();
        }
        return instance;
    }

    private InfluxDBPool(PooledObjectFactory<InfluxDBConnection> factory, GenericObjectPoolConfig<InfluxDBConnection> config) {
        super(factory, config);
    }

    public static void initPool() {
        InfluxDBConnectionFactory factory = new InfluxDBConnectionFactory();
        //设置对象池的相关参数
        GenericObjectPoolConfig poolConfig = new GenericObjectPoolConfig();
        poolConfig.setMaxIdle(5);
        poolConfig.setMaxTotal(10);
        poolConfig.setMinIdle(2);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);

        //新建一个对象池,传入对象工厂和配置
        instance = new InfluxDBPool(factory, poolConfig);
    }
}
