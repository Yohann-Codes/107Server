package reqtype;

import bean.EventBean;
import com.google.gson.Gson;
import dao.EventDao;
import utils.StreamUtils;
import utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * 客户端请求事件文本信息
 * Created by Yohann on 2016/8/19.
 */
public class EventReqText {
    private Socket socket;
    private EventBean eventBean;
    private Gson gson;
    private InputStream in;

    public EventReqText(Socket socket, InputStream in) {
        this.socket = socket;
        gson = new Gson();
        this.in = in;
    }

    public void HandleReq() throws SQLException, ClassNotFoundException, IOException {
        //调用DAO，获取数据库中所有的事件基本信息
        ArrayList<String> eventList = new EventDao().queryAll();
        String data = gson.toJson(eventList, ArrayList.class);
        //将数据发送到客户端
        OutputStream out = socket.getOutputStream();
        StreamUtils.writeString(out, data);
        socket.shutdownOutput();
        StringUtils.print("基本数据请求成功");

        out.close();
        in.close();
        socket.close();
    }
}
