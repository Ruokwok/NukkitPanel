package cc.ruok.nukkitpanel.task;

import cc.ruok.nukkitpanel.Config;
import cc.ruok.nukkitpanel.Main;
import com.google.gson.GsonBuilder;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class TaskList {

    public ArrayList<Task> list;

    public void save() {
        File t = new File(Main.getInstance().getDataFolder().getPath() + "/tasks.json");
        String s = new GsonBuilder().setPrettyPrinting().create().toJson(this);
        try {
            IOUtils.write(s, new FileOutputStream(t), Config.getCharset());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void add(Task task) {
        list.add(task);
    }

    public void pause(int i) {
        list.get(i).status = !list.get(i).status;
    }

}
