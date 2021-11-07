package cc.ruok.nukkitpanel;

import cc.ruok.nukkitpanel.utils.Net;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.Properties;

public class Config {

    private static String charset;
    private static String username;
    private static String password;
    private static long keep;
    private static boolean mdui;
    private static int wsPort;
    private static String lang;
    private static int ftpPort;
    private static boolean ftp;
    private static boolean site;
    private static boolean status = false;

    public static void load() {
        try {
            Properties properties = new Properties();
            properties.load(new FileInputStream(new File(Main.getInstance().getDataFolder().getPath() + "/panel.properties")));
            charset = setCharset(properties.getProperty("charset"));
            username = properties.getProperty("username");
            password = properties.getProperty("password");
            lang = properties.getProperty("language");
            keep = Long.parseLong(properties.getProperty("keep-connected"));
            mdui = properties.getProperty("local-mdui").equals("on");
            ftp = properties.getProperty("ftp-server").equals("on");
            site = properties.getProperty("website").equals("on");
            ftpPort = Integer.parseInt(properties.getProperty("ftp-port"));
            int wp = 0;
            try {
                wp = Integer.parseInt(properties.getProperty("websocket-port"));
                if (wp > 0 && wp < 65536) wsPort = wp;
                else wsPort = Net.getPortOfTCP();
            } catch (Exception e) {
                wsPort = Net.getPortOfTCP();
            }
            status = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static String setCharset(String string) {
        String s = string.toLowerCase();
        if (s.contains("auto")) {
            String os = System.getProperty("os.name").toLowerCase();
            String s1 = (os.contains("windows"))? "gbk": "utf8";
            List<String> bean = ManagementFactory.getRuntimeMXBean().getInputArguments();
            for (int i = 0; i < bean.size(); i++) {
                if (bean.get(i).toLowerCase().contains("-dfile.encoding=")) {
                    s1 = bean.get(i).split("=")[1];
                    break;
                }
            }
            return s1;
        } else {
            return s;
        }
    }

    public static String getCharset() {
        return charset;
    }

    public static String getUsername() {
        return username;
    }

    public static String getPassword() {
        return password;
    }

    public static long getKeepConnected() {
        return keep;
    }

    public static boolean localMdui() {
        return mdui;
    }

    public static String getMduiCss() {
        if (mdui) {
            return "../res/css/mdui.min.css";
        } else {
            return "https://cdn.jsdelivr.net/npm/mdui@1.0.1/dist/css/mdui.min.css";
        }
    }

    public static String getMduiJs() {
        if (mdui) {
            return "../res/js/mdui.min.js";
        } else {
            return "https://cdn.jsdelivr.net/npm/mdui@1.0.1/dist/js/mdui.min.js";
        }
    }

    public static int getWsPort() {
        return wsPort;
    }

    public static String getLang() {
        return lang;
    }

    public static int getFtpPort() {
        return ftpPort;
    }

    public static boolean onFtp() {
        return ftp;
    }

    public static boolean onSite() {
        return site;
    }

    public static boolean isOk() {
        return status;
    }
}
