package utils;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * 检测工具
 * <p>
 * Created by Yohann on 2016/8/19.
 */
public class DetectionUtils {
    private static Map<String, String> fomatMap = new HashMap<>();

    /**
     * 检测音频，视频，图片
     *
     * @param file
     * @return
     */
    public static String detectFileFormat(File file) {
        //添加格式
        fomatMap.put(".mp3", "voice");
        fomatMap.put(".wav", "voice");
        fomatMap.put(".amr", "voice");
        fomatMap.put(".jpg", "picture");
        fomatMap.put(".png", "picture");
        fomatMap.put(".mp4", "video");
        fomatMap.put(".avi", "video");
        fomatMap.put(".3gp", "video");
        fomatMap.put(".wmv", "video");

        String filepath = file.getAbsolutePath();
        int index = filepath.lastIndexOf(".");
        String postfix = filepath.substring(index);
        String type = fomatMap.get(postfix);

        if (type == null) {
            type = "unknown";
        }

        return type;
    }
}
