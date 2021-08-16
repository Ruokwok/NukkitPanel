package cc.ruok.nukkitpanel.file;

import cc.ruok.nukkitpanel.api.json.FileInfo;
import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.ArrayList;

public class FileIO {

    /**
     * 获取文件总行数
     * @param file 文件
     * @return
     * @throws IOException
     */
    public static int getTotalLines(File file) throws IOException {
        FileReader in = new FileReader(file);
        LineNumberReader reader = new LineNumberReader(in);
        reader.skip(Long.MAX_VALUE);
        int lines = reader.getLineNumber();
        reader.close();
        return lines;
    }


    /**
     * 获取文件从指定行到末尾
     * 参考自 https://blog.csdn.net/qq_18671415/article/details/112611901
     * @param line 行
     * @param file 文件
     * @return
     * @throws Exception
     */
    public static String getToEnd(int line, File file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuffer sb = new StringBuffer();
        String temp = null;
        int count = 0;
        while ((temp = br.readLine()) != null) {
            count++;
            if (count >= line) {
                sb.append(temp + "\n");
            }
        }
        return sb.toString();
    }


    public static ArrayList<FileInfo> getFileList(String path) {
        File root = new File(path);
        File[] files = root.listFiles();
        ArrayList<FileInfo> list = new ArrayList<>();
        if (files == null) return list;
        for (File file : files) {
            FileInfo fi = new FileInfo();
            fi.name = file.getName();
            fi.dir = file.isDirectory();
            fi.size = file.length();
            list.add(fi);
        }
        return list;
    }

    public static void rename(String oldFile, String newFile) {
        try {
            FileUtils.moveFile(new File(oldFile), new File(newFile));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
