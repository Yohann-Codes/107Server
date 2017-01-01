package loginreg;

import bean.CloseConnBean;
import bean.UserBean;
import com.google.gson.Gson;
import dao.UserDao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * 用户注册
 * <p>
 * Created by Yohann on 2016/8/11.
 */
public class UserLogin {

    private UserDao userDao;
    private Gson gson;

    public UserLogin() throws SQLException, ClassNotFoundException {
        userDao = new UserDao();
        gson = new Gson();
    }

    /**
     * 登录
     *
     * @param username
     * @return
     */
    public boolean login(String username, String password) {

        String result = userDao.queryPassword(username);

        if (result != null) {
            if (result.equals(password)) {
               return true;
            } else {
                return false;
            }
        } else {
            return false;
        }
    }


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
