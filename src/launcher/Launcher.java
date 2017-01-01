package launcher;

import constants.Constants;
import push.PushSocket;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 启动类
 * <p>
 * 每当有客户端连接时，都会创建一个线程为其单独服务
 * <p>
 * Created by Yohann on 2016/8/11.
 */
public class Launcher {
    /**
     * 程序执行入口
     */
    public static void main(String[] args) {

        //负责接收请求推送socket的线程
        new Thread() {
            @Override
            public void run() {
                try {
                    ServerSocket serverSocketPush = new ServerSocket(Constants.PORT_PUSH);
                    System.out.println("---------------- 端口PORT_PUSH(20001)启动监听 (推送消息)---------------");
                    while (true) {
                        //等待客户端连接
                        Socket socket = serverSocketPush.accept();
                        //每当连接一个处理推送任务的socket时，就存放在map集合中管理 (key=value: ip=socket)
                        PushSocket.addSocket(socket);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        /**
         * 自定义通讯协议
         * 客户端发来的数据内容之前都会有7个字节头信息
         * 格式为：数据格式+请求任务标记 (例如：json001 和 byte001)
         */
        ServerSocket serverSocketPortJson = null;
        try {
            serverSocketPortJson = new ServerSocket(Constants.PORT_BASIC);
            System.out.println("---------------- 端口PORT_JSON(20000)启动监听 (TCP)---------------");
            while (true) {
                //等待客户端连接
                Socket socket = serverSocketPortJson.accept();
                System.out.println("########### 连接成功 ###########");

                //获取输入流
                InputStream in = socket.getInputStream();
                parseData(socket, in);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 解析header信息
     *
     * @param in
     */
    public static void parseData(Socket socket, InputStream in) throws IOException {

        byte[] headBytes = new byte[7];
        in.read(headBytes, 0, 7);
        String header = new String(headBytes);

        String type = header.substring(0, 4);
        String task = header.substring(4);

        System.out.println("type=" + type);
        System.out.println("task=" + task);

        switch (type) {
            case Constants.TYPE_JSON:

                //处理Json数据的任务线程
                new Thread(new ServerThreadJson(socket, in, task)).start();
                break;

            case Constants.TYPE_BYTE:

                //处理bytes数据的任务线程
                new Thread(new ServerThreadBytes(socket, in, task)).start();
                break;
        }
    }
}
