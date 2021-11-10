package cc.ruok.nukkitpanel.servers.http;

import cc.ruok.nukkitpanel.Config;
import cc.ruok.nukkitpanel.R;
import cc.ruok.nukkitpanel.modules.ModuleManager;
import cc.ruok.nukkitpanel.utils.Format;
import cn.nukkit.Server;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class PanelHttpServer {

    private HttpServer httpServer;

    private static PanelHttpServer panelHttpServer = new PanelHttpServer();

    private PanelHttpServer() {
        try {
            httpServer = HttpServer.create(new InetSocketAddress(Server.getInstance().getPort()), 0);
            httpServer.createContext(Config.getEntry(), PanelHandler.adminIndex());
            httpServer.createContext(Config.getEntry() + "/guide", PanelHandler.html("admin/guide.html"));
            httpServer.createContext(Config.getEntry() + "/res", PanelHandler.file());
            httpServer.createContext(Config.getEntry() + "/image", PanelHandler.image());
            httpServer.createContext(Config.getEntry() + "/main", PanelHandler.adminMain());
            httpServer.createContext(Config.getEntry() + "/login", PanelHandler.adminLogin());
            httpServer.createContext(Config.getEntry() + "/console", PanelHandler.adminConsole());
            httpServer.createContext(Config.getEntry() + "/log", PanelHandler.adminLog());
            httpServer.createContext(Config.getEntry() + "/plugins", PanelHandler.adminPlugins());
            httpServer.createContext(Config.getEntry() + "/players", PanelHandler.adminPlayers());
            httpServer.createContext(Config.getEntry() + "/files", PanelHandler.adminFiles());
            httpServer.createContext(Config.getEntry() + "/download", PanelHandler.download());
            httpServer.createContext(Config.getEntry() + "/task", PanelHandler.adminTask());
            if (!Config.isOk()) {
                httpServer.createContext("/", PanelHandler.html("admin/guide.html"));
            } else if (Config.onSite()) {
                httpServer.createContext("/", PanelHandler.website());
            }
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void updateModulePage(String mid) {
//        ModuleManager manager = ModuleManager.getInstance();
//        String temp = null;
//        try {
//            temp = IOUtils.toString(PanelHttpServer.class.getResource("/resources/admin/module.html"));
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        String s = Format.formatHTML(temp.replaceAll("\\{\\{module.html}}", manager.getModules(mid).getHTML()));
//        for (Map.Entry<String, String> entry : R.lang.entrySet()) {
//            s = s.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
//        }
        ModuleManager manager = ModuleManager.getInstance();
        int code = 200;
        getInstance().httpServer.createContext("/admin/" + mid, (http) -> {
            if (ModuleManager.getInstance().exists(mid)) {
                PanelHandler.format(http);
                http.getResponseHeaders().add("Content-Type", "text/html; charset=" + Config.getCharset());
                String html = R.format(Format.formatHTML(manager.getModules(mid).getHTML()));
                byte[] bytes = html.getBytes();
                http.sendResponseHeaders(code, bytes.length);
                http.getResponseBody().write(bytes);
                http.close();
            } else {
                byte[] bytes = ("<head> <meta http-equiv=\"refresh\" content=\"0;url=/admin/main/\"></head>").getBytes();
                http.sendResponseHeaders(200, bytes.length);
                http.getResponseBody().write(bytes);
                http.close();
            }
        });
    }

    public static PanelHttpServer getInstance() {
        return panelHttpServer;
    }

    public void close() {
        httpServer.stop(1);
    }

}
