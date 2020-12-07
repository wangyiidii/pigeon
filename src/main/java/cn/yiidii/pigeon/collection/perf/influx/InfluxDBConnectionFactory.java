package cn.yiidii.pigeon.collection.perf.influx;

import cn.yiidii.pigeon.common.util.server.SpringContextUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.PooledObjectFactory;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import java.util.Objects;

@Slf4j
@Data
public class InfluxDBConnectionFactory implements PooledObjectFactory<InfluxDBConnection> {


    @Override
    public PooledObject<InfluxDBConnection> makeObject() throws Exception {
        InfluxDBConnection conn = SpringContextUtil.getBean(InfluxDBConnection.class);
        conn.influxDbBuild();
        return new DefaultPooledObject<>(conn);
    }

    @Override
    public void activateObject(PooledObject<InfluxDBConnection> pooledObject) throws Exception {
        InfluxDBConnection conn = pooledObject.getObject();
        conn.influxDbBuild();
    }

    @Override
    public boolean validateObject(PooledObject<InfluxDBConnection> pooledObject) {
        InfluxDBConnection conn = pooledObject.getObject();
        if (!conn.ping()) {
            try {
                destroyObject(pooledObject);
            } catch (Exception e) {
            }
        }
        return conn.ping();
    }

    @Override
    public void destroyObject(PooledObject<InfluxDBConnection> pooledObject) throws Exception {
        InfluxDBConnection conn = pooledObject.getObject();
        if (!Objects.isNull(conn)) {
            conn.close();
        }
    }

    @Override
    public void passivateObject(PooledObject<InfluxDBConnection> pooledObject) throws Exception {
    }
}
