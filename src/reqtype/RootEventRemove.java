package reqtype;

import bean.EventBean;
import com.google.gson.Gson;
import dao.EventDao;
import utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;

/**
 * Created by Yohann on 2016/8/21.
 */
public class RootEventRemove {
    private Socket socket;
    private InputStream in;
    private Gson gson;

    public RootEventRemove(Socket socket, InputStream in) {
        this.socket = socket;
        this.in = in;
        gson = new Gson();
    }

    /**
     * 删除事件操作
     *
     * @param data
     */
    public void remove(String data) throws SQLException, ClassNotFoundException, IOException {
        EventBean eventBean = gson.fromJson(data, EventBean.class);
        int id = eventBean.getId();

        String result = null;

        //删除数据库events表对应的内容, 更新events_records的end_time
        int rows = new EventDao().delete(id);
        System.out.println("rows = " + rows);

        if (rows == 2) {
            result = "1";
        } else {
            result = "0";
        }

        OutputStream out = socket.getOutputStream();
        StreamUtils.writeString(out, result);
        socket.shutdownOutput();

        in.close();
        out.close();
        socket.close();
    }
}
