package cc.ruok.nukkitpanel.modules;

import cc.ruok.nukkitpanel.R;
import cc.ruok.nukkitpanel.modules.exception.ModuleExecption;
import cc.ruok.nukkitpanel.modules.exception.ModuleIsExistedExecption;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuleManager {

    private static ModuleManager manager = new ModuleManager();

    private List<Module> modules = new ArrayList<>();

    private ModuleManager() {}

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

}
