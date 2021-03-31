package cc.ruok.nukkitpanel.utils;

import java.net.ServerSocket;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Net {

    public static boolean isPublicIp(String ip) {
        Pattern reg = Pattern.compile("^(127\\.0\\.0\\.1)|(localhost)|(10\\.\\d{1,3}\\.\\d{1,3}\\.\\d{1,3})|(172\\.((1[6-9])|(2\\d)|(3[01]))\\.\\d{1,3}\\.\\d{1,3})|(192\\.168\\.\\d{1,3}\\.\\d{1,3})$");
        Matcher match = reg.matcher(ip);
        return !match.find();
    }


    public static int getPortOfTCP() {
        try {
            int port = random(10000, 20000);
            ServerSocket socket = new ServerSocket(port);
            socket.close();
            return port;
        } catch (Exception e) {
            return getPortOfTCP();
        }
    }


    public static int random(int a, int b) {
        double random = java.lang.Math.random();
        return a+(int)(random*(b-a+1));
    }

}
