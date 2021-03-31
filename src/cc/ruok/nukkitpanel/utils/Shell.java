package cc.ruok.nukkitpanel.utils;

import org.apache.commons.exec.CommandLine;
import org.apache.commons.exec.DefaultExecutor;
import org.apache.commons.exec.ExecuteWatchdog;
import org.apache.commons.exec.PumpStreamHandler;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.InputStreamReader;

public class Shell {

    public static String exec(String command,String charset) {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ByteArrayOutputStream errorStream = new ByteArrayOutputStream();
            CommandLine commandline = CommandLine.parse(command);
            DefaultExecutor exec = new DefaultExecutor();
            exec.setExitValues((int[])null);
            ExecuteWatchdog watchdog = new ExecuteWatchdog(30000L);
            exec.setWatchdog(watchdog);
            PumpStreamHandler streamHandler = new PumpStreamHandler(outputStream, errorStream);
            exec.setStreamHandler(streamHandler);
            exec.execute(commandline);
            String out;
            String error;
            out = outputStream.toString(charset);
            error = errorStream.toString(charset);

            return out + error;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String execLinux(String commandStr) {
        String result = null;

        try {
            String[] cmd = new String[]{"/bin/sh", "-c", commandStr};
            Process ps = Runtime.getRuntime().exec(cmd);
            BufferedReader br = new BufferedReader(new InputStreamReader(ps.getInputStream()));
            StringBuffer sb = new StringBuffer();

            String line;
            while((line = br.readLine()) != null) {
                sb.append(line).append("\n");
            }

            result = sb.toString();
        } catch (Exception var7) {
            var7.printStackTrace();
        }

        return result;
    }

}
