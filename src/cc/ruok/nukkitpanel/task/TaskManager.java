package cc.ruok.nukkitpanel.task;

import cc.ruok.nukkitpanel.Config;
import cc.ruok.nukkitpanel.Main;
import cn.nukkit.utils.Logger;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.quartz.*;
import org.quartz.impl.StdSchedulerFactory;

import java.io.File;
import java.io.IOException;

public class TaskManager {

    private static TaskList list;
    private static Logger logger = Main.getInstance().getLogger();
    private static Scheduler scheduler;

    public static void load() {
        try {
            String json = FileUtils.readFileToString(new File(Main.getInstance().getDataFolder().getPath() + "/tasks.json"), Config.getCharset());
            list = new Gson().fromJson(json, TaskList.class);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean add(Task task) {
        try {
            list.add(task);
            list.save();
            quartz(list.list.size() - 1);
        } catch (Exception e) {
            e.printStackTrace();
            list.list.remove(list.list.size() - 1);
            list.save();
            return false;
        }
        return true;
    }

    public static void update() {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            sched.pauseAll();
            sched.shutdown();
            scheduler.shutdown();
        } catch (NullPointerException e) {
//            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < list.list.size(); i++) {
            try {
                if (list.list.get(i).status) {
                    quartz(i);
                }
            } catch (Exception e) {
                e.printStackTrace();
                logger.warning("Cron表达式无效: " + list.list.get(i).expression + " /" + list.list.get(i).title);
            }
        }
    }

    public static void quartz(int i) throws SchedulerException {
        scheduler = StdSchedulerFactory.getDefaultScheduler();
        JobDetail job = JobBuilder.newJob(TaskJob.class).withIdentity(String.valueOf(i), "base").build();
        CronTrigger trigger = TriggerBuilder.newTrigger().withIdentity(String.valueOf(i),
                "base").withSchedule(CronScheduleBuilder.cronSchedule(list.list.get(i).expression)).build();
        scheduler.scheduleJob(job,trigger);
        scheduler.start();
    }

    public static TaskList getList() {
        return list;
    }

    public static void removeJob(String name) {
        SchedulerFactory schedulerFactory = new StdSchedulerFactory();
        try {
            Scheduler sched = schedulerFactory.getScheduler();
            TriggerKey triggerKey = TriggerKey.triggerKey(name, "base");
            sched.pauseTrigger(triggerKey);// 停止触发器
            sched.unscheduleJob(triggerKey);// 移除触发器
            sched.deleteJob(JobKey.jobKey(name, "base"));// 删除任务
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
