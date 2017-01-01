package loginreg;

import bean.CloseConnBean;
import bean.UserBean;
import com.google.gson.Gson;
import constants.Constants;
import dao.UserDao;
import dao.UserDaoTemp;

import java.io.File;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用户注册
 * <p>
 * Created by Yohann on 2016/8/11.
 */
public class UserReg {

    private Gson gson;

    public UserReg() {
        gson = new Gson();
    }

    /**
     * 注册
     *
     * @param username
     * @param password
     * @return
     */
    public boolean register(String username, String password) throws SQLException, ClassNotFoundException {

        //用户名冲突检测
        boolean b = new UserDao().queryReg(username);

        if (!b) {
            //向数据库中添加用户信息
            int rows = new UserDao().insert(username, password);

            //创建用户个人表
            new UserDaoTemp().createUserTables(username);

            if (rows != 0) {

                //创建用户的个人目录
                File file = new File(Constants.USERS_PATH + username);
                if (!file.exists()) {
                    file.mkdirs();
                }
                return true;

            } else {
                return false;
            }
        }

        return false;
    }

    /**
     * 注册成功后调用，获取用户基本表信息
     *
     * @param username
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public String getMsg(String username) throws SQLException, ClassNotFoundException {
        //Json格式的用户信息
        String msg = null;

        //查询
        CloseConnBean closeConnBean = new UserDao().queryUserMsg(username);
        ResultSet resultSet = closeConnBean.getResultSet();
        PreparedStatement pstmt = closeConnBean.getPstmt();
        Connection conn = closeConnBean.getConn();
        try {
            if (resultSet.next()) {
                //提取各个字段数据
                int id = resultSet.getInt(1);
                String password = resultSet.getString(3);
                int star = resultSet.getInt(4);

                //将数据建立UserBean模型
                UserBean userBean = new UserBean();
                userBean.setId(id);
                userBean.setUsername(username);
                userBean.setPassword(password);
                userBean.setStar(star);

                //将bean转换为Json字符串
                msg = gson.toJson(userBean);
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

        //返回信息
        return msg;
    }
}
