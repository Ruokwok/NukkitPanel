package cc.ruok.nukkitpanel.modules;

import cn.nukkit.event.EventHandler;
import cn.nukkit.event.Listener;
import cn.nukkit.event.plugin.PluginDisableEvent;
import cn.nukkit.event.plugin.PluginEnableEvent;
import org.luaj.vm2.ast.Str;

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

    @EventHandler
    public void pluginEnable(PluginEnableEvent event) {
        List<Module> modules = ModuleManager.getInstance().getModules();
        String plugin = event.getPlugin().getName();
        for (int i = 0; i < modules.size(); i++) {
            if (modules.get(i).getPlugin() == event.getPlugin()) {
                return;
            }
        }
        ModuleManager.getInstance().load(event.getPlugin().getName());
    }

}
