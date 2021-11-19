package cc.ruok.nukkitpanel;

import cc.ruok.nukkitpanel.file.FileIO;
import cc.ruok.nukkitpanel.utils.Common;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Objects;

public class Log {

    private int line;
    private int j = 1;
    private final File file = new File("logs/server.log");

    public String getLogs() {
        try {
            return IOUtils.toString(new FileInputStream(file), Config.getCharset());
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public ArrayList<String> getLogToList() {
        ArrayList<String> map = stringToList(Objects.requireNonNull(getLogs()).split("\n"));
        line = getLogLine();
        return map;
    }

    public ArrayList<String> getLogNewLineToList() {
        int newLine = getLogLine();
        ArrayList<String> map = new ArrayList<String>();
        if (newLine > line) {
            try {
                map = stringToList(FileIO.getToEnd(line + 1, file).split("\n"));
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


    public ArrayList<String> stringToList(String[] logs) {
        ArrayList<String> map = new ArrayList<>();
        for (String log : logs) {
            String l = log.replace("\r", "")
                        .replaceFirst("\\d{4}-\\d{2}-\\d{2}", "§3")
                        .replaceFirst("\\.\\d{3}", "§f")
                        .replaceFirst("\\[(.*?)]", "")
                        .replaceFirst("  ", "[")
                        .replaceFirst(" -", "]")
                        .replaceFirst("Caused by:", "§4Caused by:")
                        .replaceFirst("java.", "§4java.")
                        .replaceFirst("INFO", "§1INFO§f")
                        .replaceFirst("WARN", "§4WARN§f")
                        .replaceFirst("ERROR", "§4ERROR§f")
                        .replaceFirst("FATAL", "§4FATAL§f").trim()
                        .replaceFirst("at ", "    §4at ")
                        .replaceFirst("\\.\\.\\. ", "    §4... ");
            map.add(l);
            j++;
        }
        return map;
    }

    public static void info(String str) {
        if (Config.onLogs()) {
            Main.getInstance().getLogger().info(str);
        }
    }

}
