package cc.ruok.nukkitpanel.modules;

import cc.ruok.nukkitpanel.Main;
import cc.ruok.nukkitpanel.api.json.BNCommandJson;
import cc.ruok.nukkitpanel.api.json.BNPluginsListJson;
import cn.nukkit.Server;
import cn.nukkit.scheduler.NukkitRunnable;
import com.blocklynukkit.loader.Loader;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class BlocklyNukkit extends Module {

    public BlocklyNukkit() {
        super("blockly-nukkit", "BlocklyNukkit");
        try {
            setHTMLofResource("blockly-nukkit.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        setTitle("BlocklyNukkit");
        getAPI().registerHandler("MD_BN_LIST",this::list);
        getAPI().registerHandler("MD_BN_CMD",this::cmd);
    }

    public String list(String s, WebSocket socket) {
        String[] files = new File("plugins/BlocklyNukkit").list();
        String[] bnpms = new File("plugins/BlocklyNukkit/bnpm").list();
        BNPluginsListJson json = new BNPluginsListJson();
        json.list = new HashMap<>();
        Set<String> set = Loader.bnpluginset;
        for (int i = 0; i < files.length; i++) {
            if (files[i].endsWith(".js") ||
                    files[i].endsWith(".php") ||
                    files[i].endsWith(".lua") ||
                    files[i].endsWith(".bnpx") ||
                    files[i].endsWith(".py")) {
                BNPluginsListJson.BNP bnp = new BNPluginsListJson.BNP();
                bnp.stats = set.contains(files[i]);
                bnp.bnpm = bnpms != null && Arrays.asList(bnpms).contains(files[i].replaceAll("\\.(php|js|lua|py|bnpx)", ".yml"));
                json.list.put(files[i], bnp);
            }
        }
        socket.send(json.toString());
        return null;
    }

    public String cmd(String s, WebSocket socket) {
        BNCommandJson j = new Gson().fromJson(s, BNCommandJson.class);
        String cmd = null;
        if (j.param.equals("load")) {
            cmd = "bnreload " + j.plugin;
        } else if (j.param.equals("update")) {
            cmd = "bnpm update " + j.plugin;
        } else if (j.param.equals("delete")) {
            cmd = "bnpm delete " + j.plugin.replaceAll("\\.(php|js|lua|py|bnpx)", "");
        } else if (j.param.equals("install")) {
            cmd = "bnpm install " + j.plugin;
        }
        String fc = cmd;
        new NukkitRunnable() {
            @Override
            public void run() {
                Server.getInstance().dispatchCommand(new SimulationSender(socket), fc);
            }
        }.runTask(Main.getInstance());
        return null;
    }

}
