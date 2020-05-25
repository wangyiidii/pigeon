package cn.yiidii.pigeon.collection.perf;

import cn.yiidii.pigeon.cmdb.dto.IndicatorValue;
import cn.yiidii.pigeon.cmdb.entity.Indicator;
import cn.yiidii.pigeon.collection.perf.influx.InfluxDBConnection;
import cn.yiidii.pigeon.collection.perf.influx.InfluxDBPool;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
public class PerfHelper {

    public static void savePerfValues(Indicator indicator, IndicatorValue indicatorValue) {
        InfluxDBPool pool = InfluxDBPool.getInstance();
        InfluxDBConnection conn = null;
        try {
            conn = pool.borrowObject();
            conn.insert(indicator.getName(), null, indicatorValue.getValues(), System.currentTimeMillis(), TimeUnit.MILLISECONDS);
        } catch (Exception e) {
            //log.error("exception occured when save perf of {}: {}", indicator.getName(), e.toString());
        } finally {
            if (!Objects.isNull(conn)) {
                pool.returnObject(conn);
            }
        }
    }
}
