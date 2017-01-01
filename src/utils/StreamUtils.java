package utils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * 流解析工具
 * <p>
 * Created by Yohann on 2016/8/11.
 */
public class StreamUtils {

    private static InputStreamReader inReader;

    //字符缓冲区大小
    private static final int BUFFER_CHAR = 100;
    //字节缓冲区大小
    private static final int BUFFER_BYTE = 1024 * 500;

    /**
     * 从流中获取字符串
     *
     * @param in InputSream
     * @return String
     */
    public static String readString(InputStream in) throws UnsupportedEncodingException {
        inReader = new InputStreamReader(in, "UTF-8");
        StringBuilder strBuilder = new StringBuilder();
        char[] buffer = new char[BUFFER_CHAR];
        int len = 0;
        try {
            while ((len = inReader.read(buffer)) != -1) {
                strBuilder.append(buffer, 0, len);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return strBuilder.toString();
    }

    /**
     * 将字符数据写入流中
     *
     * @param out
     * @param data
     * @throws IOException
     */
    public static void writeString(OutputStream out, String data) throws IOException {
        out.write(data.getBytes("UTF-8"));
        out.flush();
    }

    public static void close() throws IOException {
        inReader.close();
    }

    /**
     * 将文件写入磁盘
     *
     * @param path
     * @param in
     */
    public static void writeFileToDisk(String path, InputStream in) {
        FileOutputStream fileOut = null;
        try {
            fileOut = new FileOutputStream(path);
            int len = 0;
            byte[] buf = new byte[BUFFER_BYTE];
            while ((len = in.read(buf)) != -1) {
                fileOut.write(buf, 0, len);
                fileOut.flush();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                fileOut.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 解压缩目录
     *
     * @param desDir
     * @param fis
     * @throws IOException
     */
    public static void decompress(File desDir, FileInputStream fis) throws IOException {
        if (!desDir.exists()) {
            desDir.mkdirs();
        }

        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        ZipInputStream zis = new ZipInputStream(fis);
        ZipEntry entry = null;

        try {
            while ((entry = zis.getNextEntry()) != null) {
                String path = desDir + File.separator + entry.getName();
                File file = new File(path);
                fos = new FileOutputStream(file);
                bos = new BufferedOutputStream(fos);
                int len = 0;
                byte[] buf = new byte[BUFFER_BYTE];
                while ((len = zis.read(buf)) != -1) {
                    bos.write(buf, 0, len);
                    bos.flush();
                }
                bos.close();
                fos.close();
            }
        } catch (IOException e) {
        } finally {
            zis.close();
            fis.close();
        }
    }
}
