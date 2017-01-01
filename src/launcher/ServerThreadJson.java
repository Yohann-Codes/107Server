package launcher;

import com.google.gson.Gson;
import constants.Constants;
import reqtype.*;
import utils.StreamUtils;
import utils.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.sql.SQLException;

/**
 * 处理Json数据
 * <p>
 * Created by Yohann on 2016/8/11.
 */
public class ServerThreadJson implements Runnable {
    private Socket socket;
    private Gson gson;
    private String task;
    private InputStream in;

    public ServerThreadJson(Socket socket, InputStream in, String task) {
        this.socket = socket;
        this.in = in;
        this.task = task;
        gson = new Gson();
    }

    @Override
    public void run() {
        try {
            //读取Json格式的字符串
            String data = StreamUtils.readString(in);
            System.out.println("data=" + data);

            //判断任务类型
            switch (task) {
                //注册
                case Constants.REGISTER:
                    System.out.println("执行注册任务");
                    new Register(socket, in).register(data);
                    break;

                //登录
                case Constants.LOGIN:
                    System.out.println("执行登录任务");
                    new Login(socket, in).login(data);
                    break;

                //添加107端上传的事件的文本信息
                case Constants.ADD_EVENT_TEXT:
                    System.out.println("添加107端上传的事件的文本信息");
                    new RootEventAddText(socket, in).addEvent(data);
                    break;

                //客户端请求事件的基本信息
                case Constants.GET_EVENT_TEXT:
                    StringUtils.print("客户端请求事件的基本信息");
                    new EventReqText(socket, in).HandleReq();
                    break;

                case Constants.REMOVE_EVENT:
                    StringUtils.print("删除事件");
                    new RootEventRemove(socket, in).remove(data);
                    break;

                //获取媒体文件的在线加载地址
                case Constants.GET_LOAD_PATH:
                    StringUtils.print("获取媒体文件的在线加载地址");
                    new LoadPathReq(socket, in).sendPath(data);
                    break;
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            e.printStackTrace();
        } //注意：这里不能立刻关闭流资源，因为直接用传统方式关闭流后socket也会被关闭
    }
}
