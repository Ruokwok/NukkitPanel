package cc.ruok.nukkitpanel.utils;

import cc.ruok.nukkitpanel.Config;
import cc.ruok.nukkitpanel.Main;
import cc.ruok.nukkitpanel.modules.ModuleManager;
import cc.ruok.nukkitpanel.servers.websocket.WebSocketServer;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;

public class Format {

    private static final String UPTIME_FORMAT;

    public static String formatHTML(String html) {
        try {
            String drawer = IOUtils.toString(Format.class.getResource("/resources/admin/include/drawer.html"), "utf-8")
                    .replace("{{modules.extension}}", ModuleManager.getInstance().getDrawerHtml());
            String header = IOUtils.toString(Format.class.getResource("/resources/admin/include/header.html"), "utf-8");
            String src = IOUtils.toString(Format.class.getResource("/resources/admin/include/src.html"), "utf-8");
            String ws = IOUtils.toString(Format.class.getResource("/resources/admin/include/websocket.js"), "utf-8");

            return (html.replaceAll("\\{\\{web.drawer}}", drawer)
                    .replaceAll("\\{\\{web.header}}", header)
                    .replaceAll("\\{\\{web.src}}", src)
                    .replaceAll("\\{\\{web.mdui.css}}", Config.getMduiCss())
                    .replaceAll("\\{\\{web.mdui.js}}", Config.getMduiJs())
                    .replaceAll("\\{\\{js.websocket}}", Matcher.quoteReplacement(ws)))
                    .replaceAll("\\{\\{ws.port}}", String.valueOf(WebSocketServer.port()))
                    .replaceAll("\\{\\{web.version}}", Main.getInstance().getDescription().getVersion());
        } catch (IOException e) {
            e.printStackTrace();
            return html;
        }
    }

    public static String formatAddress(String html, InetSocketAddress address) {
        String ip = String.valueOf(address.getHostString());
        String port = String.valueOf(address.getPort());
        ip = ip.contains("0:0:0")? "127.0.0.1": ip;
        return html.replaceAll("\\{\\{e.address}}", ip + ":" + port);
    }


    public static String formatUptime(long uptime) {
        long days = TimeUnit.MILLISECONDS.toDays(uptime);
        uptime -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(uptime);
        uptime -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(uptime);
        uptime -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(uptime);
        return String.format(UPTIME_FORMAT, days, hours, minutes, seconds);
    }

    static {
        UPTIME_FORMAT = "%ddays %dh %dmin %ds";
    }
}
