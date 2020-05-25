package cn.yiidii.pigeon.collection.collector.database.mysql.connection;

import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Test {

    public static void main(String[] args) throws Exception {
        simulate();
        printPoolInfo();
    }

    private static void simulate() throws Exception {
        Thread t = new Thread() {
            public void run() {
                GenericKeyedObjectPool<MysqlConfig, Connection> pool = MysqlConnectionPool.getInstance();
                MysqlConfig[] mysqlConfigArr = {new MysqlConfig("192.168.2.37", 3306, "root", "pigoss", "pigoss"),
                        new MysqlConfig("192.168.2.219", 3306, "root", "pigoss", "pigoss")};
                int i = 0;
                while (true) {
                    MysqlConfig mysqlConfig = mysqlConfigArr[i % 2];
                    Connection connection = null;
                    try {
                        connection = pool.borrowObject(mysqlConfig);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    ResultSet result = null;
                    query(connection, "select count(*) from tuser;");
                    pool.returnObject(mysqlConfig, connection);
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                    }
                    i++;
                }
            }
        };
        t.start();
    }

    private static void printPoolInfo() {
        Thread t = new Thread() {
            public void run() {
                while (true) {
                    GenericKeyedObjectPool<MysqlConfig, Connection> pool = MysqlConnectionPool.getInstance();
                    System.out.println("============");
                    System.out.println("CreatedCount: " + pool.getCreatedCount());
                    System.out.println("BorrowedCount: " + pool.getBorrowedCount());
                    System.out.println("ReturnedCount: " + pool.getReturnedCount());
                    System.out.println("DestroyedCount: " + pool.getDestroyedCount());
                    System.out.println("NumIdle: " + pool.getNumIdle());
                    System.out.println();
                    try {
                        Thread.sleep(6000);
                    } catch (InterruptedException e) {
                    }
                }
            }
        };
        t.start();
    }

    private static void query(Connection connection, String sql) {
        ResultSet result = null;
        try {
            PreparedStatement ps = connection.prepareStatement(sql);
            result = ps.executeQuery();
            while (result.next()) {
                System.out.println("user count: " + result.getString(1));
            }
        } catch (Exception e) {
        }
    }
}
