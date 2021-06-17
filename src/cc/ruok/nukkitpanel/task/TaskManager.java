package cc.ruok.nukkitpanel.task;

import cc.ruok.nukkitpanel.Config;
import cc.ruok.nukkitpanel.Main;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TaskManager {

    private static TaskList list;

    public static void load() {
        try {
            String json = FileUtils.readFileToString(new File(Main.getInstance().getDataFolder().getPath() + "/tasks.json"), Config.getCharset());
            list = new Gson().fromJson(json, TaskList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
