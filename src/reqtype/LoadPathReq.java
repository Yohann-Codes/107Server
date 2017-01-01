package reqtype;

import bean.PathBean;
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
 * Created by Yohann on 2016/8/22.
 */
public class LoadPathReq {
    private Socket socket;
    private InputStream in;
    private Gson gson;

    public LoadPathReq(Socket socket, InputStream in) {
        this.socket = socket;
        this.in = in;
        this.gson = new Gson();
    }

    /**
     * 发送地址
     *
     * @param data
     */
    public void sendPath(String data) throws SQLException, ClassNotFoundException, IOException {
        PathBean pathBean = gson.fromJson(data, PathBean.class);
        Integer id = pathBean.getId();
        String path = new EventDao().queryLoadPath(id);
        StringUtils.print(path);

        OutputStream out = socket.getOutputStream();
        StreamUtils.writeString(out, path);
        socket.shutdownOutput();

        out.close();
        in.close();
        socket.close();
    }
}
