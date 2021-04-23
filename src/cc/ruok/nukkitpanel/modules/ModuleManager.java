package cc.ruok.nukkitpanel.modules;

import cc.ruok.nukkitpanel.Main;
import cc.ruok.nukkitpanel.R;
import cc.ruok.nukkitpanel.modules.exception.ModuleExecption;
import cc.ruok.nukkitpanel.modules.exception.ModuleIsExistedExecption;
import cc.ruok.nukkitpanel.servers.http.PanelHttpServer;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private static ModuleManager manager = new ModuleManager();

    private List<Module> modules = new ArrayList<>();

    private ModuleManager() {
        Server.getInstance().getPluginManager().registerEvents(new ModuleListener(), Main.getInstance());
    }

    public static ModuleManager getInstance() {
        return manager;
    }

    public boolean exists(String id) {
        boolean e = false;
        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).getId().equals(id)) {
                e = true;
            }
            break;
        }
        return e;
    }

    public void register(Module module) throws ModuleExecption {
        if (exists(module.getId())) {
            throw new ModuleIsExistedExecption(module.getId());
        }
        modules.add(module);
        try {
            R.load();
            PanelHttpServer.updateModulePage(module.getId());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Module getModules(String id) {
        Module module = null;
        for (int i = 0; i < modules.size(); i++) {
            Module select = modules.get(i);
            if (select.getId().equals(id)) {
                module = select;
                break;
            }
        }
        return module;
    }

    public String getDrawerHtml() {
        StringBuilder d = new StringBuilder();
        for (int i = 0; i < modules.size(); i++) {
            d.append(modules.get(i).getDrawerHtml());
        }
        return d.toString();
    }

    public String[] getModuleList() {
        String[] arr = new String[modules.size()];
        for (int i = 0; i < modules.size(); i++) {
            arr[i] = modules.get(i).getId();
        }
        return arr;
    }

    protected List<Module> getModules() {
        return modules;
    }

    protected void remove(Module module) {
        modules.remove(module);
        try {
            R.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
