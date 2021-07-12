package cc.ruok.nukkitpanel;

import cc.ruok.nukkitpanel.modules.ModuleManager;
import cc.ruok.nukkitpanel.servers.ftp.FTPServer;
import cc.ruok.nukkitpanel.servers.http.PanelHttpServer;
import cc.ruok.nukkitpanel.servers.websocket.WebSocketServer;
import cc.ruok.nukkitpanel.task.TaskList;
import cc.ruok.nukkitpanel.task.TaskManager;
import cn.nukkit.plugin.PluginBase;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;

public class Main extends PluginBase {

    private PanelHttpServer panelHttpServer;
    private WebSocketServer webSocket;
    private FTPServer ftpServer;
    private static Main main;

    @Override
    public void onEnable() {
        main = this;
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
        TaskManager.load();
        TaskManager.update();
        try {
            panelHttpServer = PanelHttpServer.getInstance();
            webSocket = new WebSocketServer(Config.getWsPort());
            webSocket.start();
            if (Config.onFtp()) {
                ftpServer = new FTPServer();
                ftpServer.start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        ModuleManager.getInstance().loadAll();
    }


    @Override
    public void onDisable() {
        panelHttpServer.close();
        if (ftpServer != null) {
            ftpServer.close();
        }
        try {
            webSocket.stop();
        } catch (NullPointerException e) {
            // not print
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
             e.printStackTrace();
        }
    }


    private void init() throws IOException {
        File dir = getDataFolder();
        if (!dir.exists()) {
            dir.mkdir();
        }
        File temp = new File(getDataFolder() + "/temp");
        if (!temp.exists()) {
            temp.mkdir();
        }
        File www = new File(getDataFolder() + "/wwwroot");
        if (!www.exists()) {
            www.mkdir();
        }
        File c = new File(getDataFolder().getPath() + "/panel.properties");
        if (!c.exists()) {
            Files.copy(Main.class.getResourceAsStream("/resources/config/panel.properties"), c.toPath());
        }
        File t = new File(getDataFolder().getPath() + "/tasks.json");
        if (!t.exists()) {
            TaskList taskList = new TaskList();
            taskList.list = new ArrayList<>();
            taskList.save();
        }
        Config.load();
    }

    public static Main getInstance() {
        return main;
    }
}