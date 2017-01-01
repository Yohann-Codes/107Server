package launcher;

import com.google.gson.Gson;
import constants.Constants;
import reqtype.RootEventAddBin;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.sql.SQLException;

/**
 * 处理字节数据
 * <p>
 * Created by Yohann on 2016/8/16.
 */
public class ServerThreadBytes implements Runnable {
    private Socket socket;
    private Gson gson;
    private String task;
    private InputStream in;

    public ServerThreadBytes(Socket socket, InputStream in, String task) {
        this.socket = socket;
        this.in = in;
        this.task = task;
        gson = new Gson();
    }

    @Override
    public void run() {
        try {
            //判断任务类型
            switch (task) {
                //注册
                case Constants.ADD_EVENT_BIN:
                    System.out.println("上传事件的媒体信息");
                    new RootEventAddBin(socket, in).addBin();
                    break;
            }
        } //注意：这里不能立刻关闭流资源，因为直接用传统方式关闭流后socket也会被关闭
        catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
