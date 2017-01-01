package dao;

import bean.CloseConnBean;

import java.sql.*;
import java.util.Properties;

/**
 * 数据访问接口
 * <p>
 * 提供一系列访问数据库的方法
 * <p>
 * Created by Yohann on 2016/8/11.
 */
public class UserDao {

    //数据库连接对象
    private final Connection conn;

    /**
     * 构造方法, 连接MySQL数据库
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public UserDao() throws ClassNotFoundException, SQLException {
        //加载JDBC驱动
        Class.forName("com.mysql.jdbc.Driver");

        //准备数据库连接数据
        Properties info = new Properties();
        String url = "jdbc:mysql://localhost:3306/107db";
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
                "event_type VARCHAR(100) NOT NULL," +
                "event_title VARCHAR(300) NOT NULL," +
                "event_desc VARCHAR(1000) NOT NULL," +
                "voice VARCHAR(500)," +
                "picture VARCHAR(500)," +
                "video VARCHAR(500)," +
                "start_time DATETIME NOT NULL," +
                "end_time DATETIME NOT NULL" +
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

    /**
     * 添加用户
     *
     * @param username
     * @param password
     * @return
     */
    public int insert(String username, String password) {

        String sql = "INSERT INTO user (username, password, star) VALUES (?, ?, ?)";
        PreparedStatement pstmt = null;
        int rows = 0;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.setInt(3, 0);
            rows = pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return rows;
    }

    /**
     * 注册查询
     *
     * @param username
     * @return 如果存在此用户名返回true，否则返回false
     */
    public boolean queryReg(String username) {
        String sql = "SELECT * FROM USER WHERE username = ?";
        PreparedStatement pstmt = null;
        boolean b = false;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                b = true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return b;
    }

    /**
     * 根据用户名查询密码
     *
     * @param username
     * @return
     */
    public String queryPassword(String username) {
        String sql = "SELECT * FROM USER WHERE username = ?";
        PreparedStatement pstmt = null;
        String password = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            ResultSet resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                password = resultSet.getString("password");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        return password;
    }

    /**
     * 根据用户名查询用户所有的信息
     *
     * @param username
     * @return
     */
    public CloseConnBean queryUserMsg(String username) {
        String sql = "SELECT * FROM USER WHERE username = ?";
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, username);
            resultSet = pstmt.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //创建返回数据模型
        CloseConnBean closeConnBean = new CloseConnBean();
        if (conn != null) {
            closeConnBean.setConn(conn);
        }
        if (pstmt != null) {
            closeConnBean.setPstmt(pstmt);
        }
        closeConnBean.setResultSet(resultSet);

        return closeConnBean;
    }
}
