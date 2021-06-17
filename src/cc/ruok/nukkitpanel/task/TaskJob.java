package cc.ruok.nukkitpanel.task;

import cc.ruok.nukkitpanel.Main;
import cc.ruok.nukkitpanel.R;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.scheduler.NukkitRunnable;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.ArrayList;

public class TaskJob implements Job {

    @Override
    public void execute(JobExecutionContext jobExecutionContext) throws JobExecutionException {
        int i = Integer.parseInt(jobExecutionContext.getTrigger().getJobKey().getName());
        ArrayList<Task> list = TaskManager.getList().list;
        String[] command = list.get(i).command;
        Main.getInstance().getLogger().info(R.get("execute-task") + ": " + list.get(i).title);
        for (int j = 0; j < command.length; j++) {
            int o = j;
            new NukkitRunnable() {
                @Override
                public void run() {
                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), command[o] );
                }
            }.runTask(Main.getInstance());
        }
    }

}
