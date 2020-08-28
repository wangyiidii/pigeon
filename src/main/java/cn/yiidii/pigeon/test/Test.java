package cn.yiidii.pigeon.test;

import java.sql.*;

/**
 * @author yiidii Wang
 * @desc This class is used to ...
 */
public class Test {
    public static void main(String[] args) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            String driverClass = "com.mysql.jdbc.Driver";
            String url = "jdbc:mysql://127.0.0.1:3306/pigoss_ck";
            String user = "root";
            String pass = "pigoss";

            Class.forName(driverClass);
            connection = DriverManager.getConnection(url, user, pass);

            String sql = "select id,event_desc,event_detail,threshold_desc, facility ,severity ,event_type ,times, start_time,update_time,ack_state,ack_user,ack_time,clear_state,clear_user,clear_time , event_source indicator_path,   co_type,ip,metric_name,metric_alias, remark , history_time,filter_history, alreadyalert from alarm where clear_state = 1;";
            preparedStatement = connection.prepareStatement(sql);
            Long start = System.currentTimeMillis();
            resultSet = preparedStatement.executeQuery();
            System.out.println(String.format("time taked: %dms", (System.currentTimeMillis() - start)));
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) {
                    resultSet.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (preparedStatement != null) {
                    preparedStatement.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }

            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
