package cc.ruok.nukkitpanel.modules;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.plugin.PluginDisableEvent;

import java.util.List;

public class ModuleListener implements Listener {

    @EventHandler
    public void pluginDisable(PluginDisableEvent event) {
        List<Module> modules = ModuleManager.getInstance().getModules();
        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).getPlugin() == event.getPlugin()) {
                modules.get(i).remove();
            }
        }
    }

}
