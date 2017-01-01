package dao;

import bean.EventBean;
import bean.PathBean;
import com.google.gson.Gson;

import java.sql.*;
import java.util.ArrayList;
import java.util.Properties;

/**
 * 访问交通事件信息的数据接口
 * <p>
 * Created by Yohann on 2016/8/13.
 */
public class EventDao {

    //数据库连接对象
    private Connection conn;
    private Gson gson;

    /**
     * 构造方法, 连接MySQL数据库
     *
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public EventDao() throws ClassNotFoundException, SQLException {
        //加载JDBC驱动
        Class.forName("com.mysql.jdbc.Driver");

        //准备数据库连接数据
        Properties info = new Properties();
        String url = "jdbc:mysql://localhost:3306/107db";
        info.put("user", "root");
        info.put("password", "1044510273yh");

        //获取连接对象
        conn = DriverManager.getConnection(url, info);

        gson = new Gson();
    }

    /**
     * 插入一行路况 (events)
     *
     * @param startLocation
     * @param endLocation
     * @param startLongitude
     * @param endLongitude
     * @param startLatitude
     * @param endLatitude
     * @param eventLabels
     * @param eventTitle
     * @param eventDesc
     * @param startTime
     * @return 插入的最后一条记录的id
     */
    public int insertEvent(String startLocation,
                           String endLocation,
                           Double startLongitude,
                           Double endLongitude,
                           Double startLatitude,
                           Double endLatitude,
                           String eventLabels,
                           String eventTitle,
                           String eventDesc,
                           long startTime) {

        String sql = "INSERT INTO events (" +
                "start_location," +
                "end_location," +
                "start_longitude," +
                "end_longitude," +
                "start_latitude," +
                "end_latitude," +
                "event_labels," +
                "event_title," +
                "event_desc," +
                "start_time) " +
                "VALUES (?,?,?,?,?,?,?,?,?,?)";

        int id = 0;
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;

        try {
            pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);

            pstmt.setString(1, startLocation);
            pstmt.setString(2, endLocation);
            pstmt.setDouble(3, startLongitude);
            pstmt.setDouble(4, endLongitude);
            pstmt.setDouble(5, startLatitude);
            pstmt.setDouble(6, endLatitude);
            pstmt.setString(7, eventLabels);
            pstmt.setString(8, eventTitle);
            pstmt.setString(9, eventDesc);
            pstmt.setTimestamp(10, new Timestamp(startTime));

            pstmt.executeUpdate();
            resultSet = pstmt.getGeneratedKeys();
            if (resultSet.next()) {
                id = resultSet.getInt(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                pstmt.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        //插入记录表中
        int rows = insertRecords(
                id,
                startLocation,
                endLocation,
                startLongitude,
                endLongitude,
                startLatitude,
                endLatitude,
                eventLabels,
                startTime);

        if (rows == 0) {
            id = 0;
        }
        return id;
    }

    /**
     * 插入记录表events_records (用于统计数据)
     *
     * @param startLocation
     * @param endLocation
     * @param startLongitude
     * @param endLongitude
     * @param startLatitude
     * @param endLatitude
     * @param eventLabels
     * @param startTime
     * @return
     */
    public int insertRecords(int id,
                             String startLocation,
                             String endLocation,
                             Double startLongitude,
                             Double endLongitude,
                             Double startLatitude,
                             Double endLatitude,
                             String eventLabels,
                             long startTime) {
        String sql = "INSERT INTO events_records (" +
                "id," +
                "start_location," +
                "end_location," +
                "start_longitude," +
                "end_longitude," +
                "start_latitude," +
                "end_latitude," +
                "event_labels," +
                "start_time) " +
                "VALUES (?,?,?,?,?,?,?,?,?)";

        int rows = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql);

            pstmt.setInt(1, id);
            pstmt.setString(2, startLocation);
            pstmt.setString(3, endLocation);
            pstmt.setDouble(4, startLongitude);
            pstmt.setDouble(5, endLongitude);
            pstmt.setDouble(6, startLatitude);
            pstmt.setDouble(7, endLatitude);
            pstmt.setString(8, eventLabels);
            pstmt.setTimestamp(9, new Timestamp(startTime));

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
     * 添加二进制文件路径 (events)
     *
     * @param voicePath
     * @param picPath
     * @param videoPath
     * @return
     */
    public int addBinaryPath(String voicePath, String picPath, String videoPath, String zipPath, int id) {

        String sql = "UPDATE events SET voice_path = ?, picture_path = ?, video_path = ?, zip_path = ? WHERE id = ?";
        String sqlFinish = "UPDATE events_records SET voice_path = ?, picture_path = ?, video_path = ?, zip_path = ? WHERE id = ?";
        int rows = 0;
        int rowsFin = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setString(1, voicePath);
            pstmt.setString(2, picPath);
            pstmt.setString(3, videoPath);
            pstmt.setString(4, zipPath);
            pstmt.setInt(5, id);
            rows = pstmt.executeUpdate();

            pstmt = conn.prepareStatement(sqlFinish);
            pstmt.setString(1, voicePath);
            pstmt.setString(2, picPath);
            pstmt.setString(3, videoPath);
            pstmt.setString(4, zipPath);
            pstmt.setInt(5, id);
            rowsFin = pstmt.executeUpdate();
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

        return rows + rowsFin;
    }

    /**
     * 将Web资源添加到表resource_path中
     *
     * @param voicePathR
     * @param picPathR1
     * @param picPathR2
     * @param picPathR3
     * @param videoPathR
     * @return
     */
    public int addWebResourcePath(int id,
                                  String voicePathR, String picPathR1, String picPathR2, String picPathR3, String videoPathR,
                                  String voiceUrl, String picUrl1, String picUrl2, String picUrl3, String videoUrl) {
        String sql = "INSERT INTO resource_path VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
        int rows = 0;
        PreparedStatement pstmt = null;

        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            pstmt.setString(2, voicePathR);
            pstmt.setString(3, picPathR1);
            pstmt.setString(4, picPathR2);
            pstmt.setString(5, picPathR3);
            pstmt.setString(6, videoPathR);
            pstmt.setString(7, voiceUrl);
            pstmt.setString(8, picUrl1);
            pstmt.setString(9, picUrl2);
            pstmt.setString(10, picUrl3);
            pstmt.setString(11, videoUrl);
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
     * 查询所有事件的基本信息
     *
     * @return
     */
    public ArrayList<String> queryAll() {
        String sql = "SELECT * FROM events";
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        ArrayList<String> list = new ArrayList<>();
        try {
            pstmt = conn.prepareStatement(sql);
            resultSet = pstmt.executeQuery();
            while (resultSet.next()) {
                EventBean eventBean = new EventBean();
                eventBean.setId(resultSet.getInt(1));
                eventBean.setStartLocation(resultSet.getString(2));
                eventBean.setEndLocation(resultSet.getString(3));
                eventBean.setStartLongitude(resultSet.getDouble(4));
                eventBean.setEndLongitude(resultSet.getDouble(5));
                eventBean.setStartLatitude(resultSet.getDouble(6));
                eventBean.setEndLatitude(resultSet.getDouble(7));
                eventBean.setEventLabels(resultSet.getString(8));
                eventBean.setEventTitle(resultSet.getString(9));
                eventBean.setEventDesc(resultSet.getString(10));
                eventBean.setStartTime(resultSet.getLong(15));

                //对象 --> Json
                String eventJson = gson.toJson(eventBean);

                //添加到集合
                list.add(eventJson);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    /**
     * 查询媒体文件的加载路径 (仅一条)
     *
     * @return 所有文件路径
     */
    public String queryLoadPath(int id) {
        String sql = "SELECT * FROM resource_path WHERE id = ?";
        PreparedStatement pstmt = null;
        ResultSet resultSet = null;
        String path = null;
        try {
            pstmt = conn.prepareStatement(sql);
            pstmt.setInt(1, id);
            resultSet = pstmt.executeQuery();
            if (resultSet.next()) {
                PathBean pathBean = new PathBean();
                pathBean.setId(id);
                pathBean.setVoicePath(resultSet.getString("voice_url"));
                pathBean.setPicPath1(resultSet.getString("pic_url_1"));
                pathBean.setPicPath2(resultSet.getString("pic_url_2"));
                pathBean.setPicPath3(resultSet.getString("pic_url_3"));
                pathBean.setVideoPath(resultSet.getString("video_url"));
                path = gson.toJson(pathBean);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                resultSet.close();
                pstmt.close();
                conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return path;
    }

    /**
     * 删除一条事件记录, 添加删除事件到记录表中
     *
     * @param id
     * @return
     */
    public int delete(int id) {
        String sqlDelete = "DELETE FROM events WHERE id = ?";
        String sqlUpdata = "UPDATE events_records SET end_time = ? WHERE id = ?";
        PreparedStatement pstmt = null;
        int rowsD = 0;
        int rowsU = 0;
        try {
            //删除
            pstmt = conn.prepareStatement(sqlDelete);
            pstmt.setInt(1, id);
            rowsD = pstmt.executeUpdate();

            //更新
            pstmt = conn.prepareStatement(sqlUpdata);
            pstmt.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
            pstmt.setInt(2, id);
            rowsU = pstmt.executeUpdate();
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

        return rowsD + rowsU;
    }
}
