package cc.ruok.nukkitpanel;

import cc.ruok.nukkitpanel.file.FileIO;
import cc.ruok.nukkitpanel.utils.Common;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Log {

    private int line;
    private int j = 1;
    private File file = new File("logs/server.log");

    public String getLogs() {
        try {
            return IOUtils.toString(new FileInputStream(file), Config.getCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public LinkedHashMap<String, String> getLogToMap() {
        LinkedHashMap<String, String> map = stringToMap(Objects.requireNonNull(getLogs()).split("\n"));
        line = getLogLine();
        return map;
    }

    public LinkedHashMap<String, String> getLogNewLineToMap() {
        int newLine = getLogLine();
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        if (newLine > line) {
            try {
                map = stringToMap(FileIO.getToEnd(line + 1, file).split("\n"));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        line = newLine;
        return map;
    }

    public int getLogLine() {
        try {
            return FileIO.getTotalLines(file);
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
    }


    public LinkedHashMap<String, String> stringToMap(String[] logs) {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();

        for (int i = 0; i < logs.length; i++) {
            if (logs[i].indexOf('-') == 4 && logs[i].indexOf(':') == 13 && logs[i].indexOf('.') == 19) {
                String time = j + "@" +logs[i].split(" ")[1].split("\\.")[0];
                String type = Common.subString(logs[i], "] ", " - ").trim();
                String log = logs[i].split(" - ")[1];
                try {
                    map.put(time + "[" + type + "]", log);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            } else {
                String time = j + "@";
                String log = logs[i];
                map.put(time, log);
            }
            j++;
        }

        return map;
    }

}
