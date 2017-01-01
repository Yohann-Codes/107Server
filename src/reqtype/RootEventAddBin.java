package reqtype;

import constants.Constants;
import constants.Variable;
import dao.EventDao;
import utils.DetectionUtils;
import utils.StreamUtils;
import utils.StringUtils;

import java.io.*;
import java.net.Socket;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Yohann on 2016/8/16.
 */
public class RootEventAddBin {
    private Socket socket;
    private InputStream in;

    public RootEventAddBin(Socket socket, InputStream in) {
        this.socket = socket;
        this.in = in;
    }

    /**
     * 将客户端上传的二进制文件存放在本地
     */
    public void addBin() throws IOException, SQLException, ClassNotFoundException {

        //以返回的id在本地创建目录 (该目录中存放该事件的媒体文件)
        File file = new File(Constants.EVENTS_PATH + Variable.eventId);
        if (!file.exists()) {
            file.mkdirs();
        }

        //将zip文件写入磁盘
        StreamUtils.writeFileToDisk(Constants.EVENTS_PATH + Variable.eventId + ".zip", in);

        //向客户端返回执行结果
        OutputStream out = socket.getOutputStream();
        StreamUtils.writeString(out, "1");
        socket.shutdownOutput();
        System.out.println("zip文件上传完成");

        in.close();
        out.close();
        socket.close();

        //进行后续操作
        after();
    }

    /**
     * 服务器后续操作：
     * 1.将压缩文件复制到web服务器程序的目录下
     * 2.解压
     * 3.数据库添加路径 (events, resource_path)
     */
    public void after() throws IOException, SQLException, ClassNotFoundException {

        //复制zip文件
        FileInputStream fisR = new FileInputStream(Constants.EVENTS_PATH + Variable.eventId + ".zip");
        StreamUtils.writeFileToDisk(Constants.RESOURCE_PATH + Variable.eventId + ".zip", fisR);

        //解压
        FileInputStream fis = new FileInputStream(Constants.EVENTS_PATH + Variable.eventId + ".zip");
        StreamUtils.decompress(new File(Constants.EVENTS_PATH + Variable.eventId), fis);
        fisR = new FileInputStream(Constants.EVENTS_PATH + Variable.eventId + ".zip");
        StreamUtils.decompress(new File(Constants.RESOURCE_PATH + Variable.eventId), fisR);
        System.out.println("解压完成");

        //指定文件路径
        //events表中的路径
        String voicePath = null;
        String picPath = null;
        String videoPath = null;
        String zipPath = Constants.EVENTS_PATH + Variable.eventId + ".zip";

        //resource_path表中的路径
        String voicePathR = null;
        String picPathR1 = null;
        String picPathR2 = null;
        String picPathR3 = null;
        String videoPathR = null;

        String voiceUrl = null;
        String picUrl1 = null;
        String picUrl2 = null;
        String picUrl3 = null;
        String videoUrl = null;

        ArrayList<String> picPathList = new ArrayList<>();

        File dir = new File(Constants.EVENTS_PATH + Variable.eventId);
        File[] files = dir.listFiles();

        for (int i = 0, j = 1; i < files.length; i++) {
            File file = files[i];
            String type = DetectionUtils.detectFileFormat(file);
            switch (type) {
                case "voice":
                    voicePath = file.getCanonicalPath();
                    voicePathR = Constants.RESOURCE_PATH + Variable.eventId + File.separator + file.getName();
                    voiceUrl = Constants.LOAD_PATH + Variable.eventId + File.separator + file.getName();
                    break;
                case "picture":
                    picPathList.add(file.getCanonicalPath());
                    switch (j) {
                        case 1:
                            picPathR1 = Constants.RESOURCE_PATH + Variable.eventId + File.separator + file.getName();
                            picUrl1 = Constants.LOAD_PATH + Variable.eventId + File.separator + file.getName();
                            break;
                        case 2:
                            picPathR2 = Constants.RESOURCE_PATH + Variable.eventId + File.separator + file.getName();
                            picUrl2 = Constants.LOAD_PATH + Variable.eventId + File.separator + file.getName();
                            break;
                        case 3:
                            picPathR3 = Constants.RESOURCE_PATH + Variable.eventId + File.separator + file.getName();
                            picUrl3 = Constants.LOAD_PATH + Variable.eventId + File.separator + file.getName();
                            break;
                    }
                    j++;
                    break;
                case "video":
                    videoPath = file.getCanonicalPath();
                    videoPathR = Constants.RESOURCE_PATH + Variable.eventId + File.separator + file.getName();
                    videoUrl = Constants.LOAD_PATH + Variable.eventId + File.separator + file.getName();
                    break;
                case "unknown":
                    StringUtils.print("不支持该格式的文件：" + file.getName());
                    break;
            }
        }
        picPath = StringUtils.getStringFromArrayList(picPathList);

        //将路径添加到数据库中
        new EventDao().addBinaryPath(voicePath, picPath, videoPath, zipPath, Variable.eventId);
        new EventDao().addWebResourcePath(Variable.eventId,
                voicePathR, picPathR1, picPathR2, picPathR3, videoPathR,
                voiceUrl, picUrl1, picUrl2, picUrl3, videoUrl);
    }
}
