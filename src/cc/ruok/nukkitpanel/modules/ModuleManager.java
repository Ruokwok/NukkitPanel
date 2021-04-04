package cc.ruok.nukkitpanel.modules;

import java.util.HashMap;
import java.util.Map;

public class ModuleManager {

    private static ModuleManager manager = new ModuleManager();

    private Map<String, Module> modules = new HashMap<>();

    private ModuleManager() {}

    public static ModuleManager getInstance() {
        return manager;
    }

    public void register(String id, Module module) {
        modules.put(id, module);
    }

    public Module getModules(String id) {
        return modules.get(id);
    }

}
