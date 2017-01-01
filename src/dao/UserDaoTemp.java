package dao;

import java.sql.*;
import java.util.Properties;

/**
 * 数据访问接口
 * <p>
 * 用于管理存放用户临时创建的表的数据库
 * <p>
 * Created by Yohann on 2016/8/11.
 */
public class UserDaoTemp {

    //数据库连接对象
    private final Connection conn;

    /**
     * 构造方法, 连接MySQL数据库
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public UserDaoTemp() throws ClassNotFoundException, SQLException {
        //加载JDBC驱动
        Class.forName("com.mysql.jdbc.Driver");

        //准备数据库连接数据
        Properties info = new Properties();
        String url = "jdbc:mysql://localhost:3306/107db_temp";
        info.put("user", "root");
        info.put("password", "1044510273yh");

        //获取连接对象
        conn = DriverManager.getConnection(url, info);
    }

    /**
     * 在注册后，创建用户参与表和用户足迹表
     *
     * @param username
     */
    public void createUserTables(String username) {
        String sql1 = "CREATE TABLE " + username + "_events(" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "start_location VARCHAR(100) NOT NULL," +
                "end_location VARCHAR(100) NOT NULL," +
                "start_longitude DOUBLE NOT NULL," +
                "end_longitude DOUBLE NOT NULL," +
                "start_latitude DOUBLE NOT NULL," +
                "end_latitude DOUBLE NOT NULL," +
                "event_labels VARCHAR(100) NOT NULL," +
                "event_title VARCHAR(300) NOT NULL," +
                "event_desc VARCHAR(1000) NOT NULL," +
                "voice_path VARCHAR(500)," +
                "picture_path VARCHAR(500)," +
                "video_path VARCHAR(500)," +
                "zip_path VARCHAR(500)," +
                "start_time DATETIME NOT NULL" +
                ")";

        String sql2 = "CREATE TABLE " + username + "_track(" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "location VARCHAR(100) NOT NULL," +
                "longitude DOUBLE NOT NULL," +
                "latitude DOUBLE NOT NULL," +
                "time DATETIME NOT NULL" +
                ");";

        Statement stmt = null;

        try {
            //批处理
            stmt = conn.createStatement();
            stmt.addBatch(sql1);
            stmt.addBatch(sql2);
            stmt.executeBatch();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                stmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
