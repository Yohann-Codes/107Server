package push;

import utils.StreamUtils;
import utils.StringUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by Yohann on 2016/8/19.
 */
public class PushSocket {
    public static Map<String, Socket> socketMap = new HashMap<>();

    /**
     * 将每一个socket连接都添加到socketMap中
     *
     * @param socket
     */
    public static void addSocket(Socket socket) {
        String ip = socket.getInetAddress().getHostAddress();
        socketMap.put(ip, socket);
        StringUtils.print(ip + " ---> 已连接推送");
    }

    /**
     * 推送
     */
    public static void push(String data) {
        Socket socket = null;
        //遍历socketMap，一个一个推送
        Iterator<Map.Entry<String, Socket>> iterator = socketMap.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Socket> entry = iterator.next();
            socket = entry.getValue();
            //执行推送
            pushData(socket, data);
        }
    }

    private static void pushData(Socket socket, String data) {
        OutputStream out = null;
        try {
            out = socket.getOutputStream();
            StreamUtils.writeString(out, data);
            socket.shutdownOutput();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                out.close();
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
