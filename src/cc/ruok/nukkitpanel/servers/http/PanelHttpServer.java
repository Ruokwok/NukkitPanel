package cc.ruok.nukkitpanel.servers.http;

import cc.ruok.nukkitpanel.Config;
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
            httpServer.createContext("/admin", PanelHandler.adminIndex());
            httpServer.createContext("/admin/res", PanelHandler.file());
            httpServer.createContext("/admin/image", PanelHandler.image());
            httpServer.createContext("/admin/main", PanelHandler.adminMain());
            httpServer.createContext("/admin/login", PanelHandler.adminLogin());
            httpServer.createContext("/admin/console", PanelHandler.adminConsole());
            httpServer.createContext("/admin/log", PanelHandler.adminLog());
            httpServer.createContext("/admin/plugins", PanelHandler.adminPlugins());
            httpServer.createContext("/admin/players", PanelHandler.adminPlayers());
            httpServer.createContext("/admin/files", PanelHandler.adminFiles());
            httpServer.createContext("/admin/download", PanelHandler.download());
            if (Config.onSite()) {
                httpServer.createContext("/", PanelHandler.website());
            }
            httpServer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static PanelHttpServer getInstance() {
        return panelHttpServer;
    }

    public void close() {
        httpServer.stop(1);
    }

}
