package reqtype;

import bean.EventBean;
import com.google.gson.Gson;
import constants.Variable;
import dao.EventDao;
import push.PushSocket;
import utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.sql.SQLException;

/**
 * 添加107端上传的事件
 * <p>
 * Created by Yohann on 2016/8/13.
 */
public class RootEventAddText {
    private Socket socket;
    private EventBean eventBean;
    private Gson gson;
    private InputStream in;

    public RootEventAddText(Socket socket, InputStream in) {
        this.socket = socket;
        gson = new Gson();
        this.in = in;
    }

    /**
     * 添加事件
     *
     * @param body
     */
    public void addEvent(String body) throws SQLException, ClassNotFoundException, IOException {

        //添加结果，"1"代表成功，"0"代表失败
        String result = null;
        //向客户端返回的数据
        String dataReturn = null;

        //获取事件的各个信息
        eventBean = gson.fromJson(body, EventBean.class);
        String startLocation = eventBean.getStartLocation();
        String endLocation = eventBean.getEndLocation();
        Double startLongitude = eventBean.getStartLongitude();
        Double endLongitude = eventBean.getEndLongitude();
        Double startLatitude = eventBean.getStartLatitude();
        Double endLatitude = eventBean.getEndLatitude();
        String eventLabels = eventBean.getEventLabels();
        String eventTitle = eventBean.getEventTitle();
        String eventDesc = eventBean.getEventDesc();
        Long startTime = eventBean.getStartTime();

        //调用推送方法
        PushSocket.push(body);

        //添加到数据库
        int id = new EventDao().insertEvent(
                startLocation,
                endLocation,
                startLongitude,
                endLongitude,
                startLatitude,
                endLatitude,
                eventLabels,
                eventTitle,
                eventDesc,
                startTime);

        if (id != 0) {
            //将id和保存到EventId
            Variable.eventId = id;
            result = "1";
        } else {
            //添加失败
            result = "0";
        }

        if ("1".equals(result)) {
            //执行成功，向客户端返回Json数据

            EventBean eventBean = new EventBean();
            eventBean.setId(id);

            //生成Json串
            dataReturn = gson.toJson(eventBean);

        } else {
            //执行失败，返回"error"
            dataReturn = "error";
        }

        //向客户端返回执行结果
        OutputStream out = socket.getOutputStream();
        StreamUtils.writeString(out, dataReturn);
        socket.shutdownOutput();

        System.out.println("dataReturn = " + dataReturn);
        //关闭流和socket
        out.close();
        in.close();
        StreamUtils.close();
        socket.close();
    }
}
