package cc.ruok.nukkitpanel.modules;

import cc.ruok.nukkitpanel.Main;
import cc.ruok.nukkitpanel.R;
import cc.ruok.nukkitpanel.modules.exception.ModuleExecption;
import cc.ruok.nukkitpanel.modules.exception.ModuleIsExistedExecption;
import cc.ruok.nukkitpanel.servers.http.PanelHttpServer;
import cn.nukkit.Server;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginManager;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ModuleManager {

    public static HashMap<String, Class> supportedList = new HashMap<>();

    static {
        supportedList.put("BlocklyNukkit", BlocklyNukkit.class);
        supportedList.put("EconomyAPI", BlocklyNukkit.class);
    }

    private static ModuleManager manager = new ModuleManager();

    private List<Module> modules = new ArrayList<>();

    private ModuleManager() {
        PluginManager pluginManager = Server.getInstance().getPluginManager();
        pluginManager.registerEvents(new ModuleListener(), Main.getInstance());
    }

    public static ModuleManager getInstance() {
        return manager;
    }

    public void load() {
        PluginManager pluginManager = Server.getInstance().getPluginManager();
        Map<String, Plugin> plugins = pluginManager.getPlugins();
        for (Map.Entry<String, Plugin> entry : plugins.entrySet()) {
            if (isSupported(entry.getKey())) {
                Class aClass = supportedList.get(entry.getKey());
                try {
                    Module module = (Module) aClass.newInstance();
                    module.register();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (ModuleExecption moduleExecption) {
                    moduleExecption.printStackTrace();
                }
            }
        }
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

    public boolean isSupported(String plugin) {
        return supportedList.containsKey(plugin);
    }

}
