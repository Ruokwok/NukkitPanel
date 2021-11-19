package cc.ruok.nukkitpanel.api;

import cc.ruok.bpm.BPM;
import cc.ruok.bpm.DownloadException;
import cc.ruok.nukkitpanel.Config;
import cc.ruok.nukkitpanel.Log;
import cc.ruok.nukkitpanel.Main;
import cc.ruok.nukkitpanel.R;
import cc.ruok.nukkitpanel.api.json.*;
import cc.ruok.nukkitpanel.file.FileIO;
import cc.ruok.nukkitpanel.servers.Auth;
import cc.ruok.nukkitpanel.servers.websocket.WebSocketServer;
import cc.ruok.nukkitpanel.task.Task;
import cc.ruok.nukkitpanel.task.TaskManager;
import cc.ruok.nukkitpanel.utils.Common;
import cc.ruok.nukkitpanel.utils.Format;
import cc.ruok.nukkitpanel.utils.Monitor;
import cc.ruok.nukkitpanel.utils.Net;
import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.level.Level;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.scheduler.NukkitRunnable;
import cn.nukkit.utils.Logger;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

public class API {

    private static API handler = new API();

    private Server server = Server.getInstance();
    private PluginManager manager = Server.getInstance().getPluginManager();
    private Gson gson = new Gson();

    private Auth auth = Auth.getInstance();

    private Map<String, Handler> apis = new HashMap<>();

    private Logger logger;

    private API() {
        registerHandlers();
        logger = Main.getInstance().getLogger();
    }

    public static API getInstance() {
        return  handler;
    }

    public void registerHandlers() {
        apis.put("LOGIN", login());
        apis.put("GUIDE", guide());
        apis.put("AUTH", auth());
        apis.put("GET_MAIN", main());
        apis.put("GET_LOG", log());
        apis.put("COMMAND", cmd());
        apis.put("GET_PLUGINS", getPlugins());
        apis.put("SWITCH_PLUGIN", switchPlugin());
        apis.put("GET_PLAYERS", getPlayers());
        apis.put("PLAYER_DETAILS", playerDetails());
        apis.put("PLAYER_OPER", playerOper());
        apis.put("GET_FILES", fileList());
        apis.put("TO_PATH", toPath());
        apis.put("DEL_FILE", fileDelete());
        apis.put("RENAME_FILE", fileRename());
        apis.put("MOVE_FILE", fileMove());
        apis.put("COPY_FILE", fileCopy());
        apis.put("SHUTDOWN", shutdown());
        apis.put("READ_FILE", fileRead());
        apis.put("SAVE_FILE", fileSave());
        apis.put("CREATE_FILE", fileCreate());
        apis.put("FILE_UPLOAD", fileUpload());
        apis.put("GET_TASKS", taskList());
        apis.put("CREATE_TASK", createTask());
        apis.put("DELETE_TASK", deleteTask());
        apis.put("PAUSE_TASK", pauseTask());
        apis.put("BPM_SEARCH", bpmSearch());
        apis.put("BPM_INFO", bpmInfo());
        apis.put("BPM_OPERATION", bpmOperation());
        apis.put("BPM_CHECK", bpmCheck());
    }

    public void registerHandler(String key, Handler handler) {
        apis.put(key, handler);
    }

    public Handler get(String type) {
        return apis.get(type);
    }

    public boolean state(Json json) {
        if (json.token == null || auth.check(json.token) < 1) {
            return false;
        } else {
            return true;
        }
    }

    private Handler login() {
        return (p,s) -> {
            LoginJson json = gson.fromJson(p, LoginJson.class);
            AuthJson aj = new AuthJson();
            if (Common.sha256(auth.getSalt() + "#" + Config.getUsername() + "@" + Config.getPassword() + "MineBBS-YYDS!").equals(json.key)) {
                aj.auth = true;
                try {
                    aj.token = auth.getAuth();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                aj.message = "welcome";
                logger.info(s.getRemoteSocketAddress() + R.get("login-success"));
            } else {
                aj.auth = false;
                aj.message = "password error";
                logger.info(s.getRemoteSocketAddress() + R.get("login-fail"));
            }
            try {
                s.send(aj.toString());
                return aj.toString();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        };
    }

    private Handler guide() {
        return (p, s) -> {
            GuideJson json = gson.fromJson(p, GuideJson.class);;
            File file = new File(Main.getInstance().getDataFolder().getPath() + "/panel.properties");
            if (file.exists()) {
                s.send(new TipsJson(R.get("guide-fail")).toString());
                return null;
            }
            Properties properties = new Properties();
            String username = json.username.trim();
            String password = json.password.trim();
            String language = json.lang.trim();
            String port = (json.port >= 0)? String.valueOf(json.port) : "auto";
            String entry = json.entry.trim();
            String ftp = (json.ftp)? "on" : "off";
            String log = (json.log)? "on" : "off";
            int ftpPort = (json.ftpPort >= 0)? json.ftpPort : Net.getPortOfTCP();
            String website = (json.website)? "on" : "off";
            properties.setProperty("username", username);
            properties.setProperty("password", password);
            properties.setProperty("language", language);
            properties.setProperty("websocket-port", port);
            properties.setProperty("panel-entry", entry);
            properties.setProperty("panel-logs", log);
            properties.setProperty("ftp-server", ftp);
            properties.setProperty("ftp-port", String.valueOf(ftpPort));
            properties.setProperty("website", website);
            properties.setProperty("keep-connected", "120");
            properties.setProperty("local-mdui", "off");
            properties.setProperty("charset", "auto");
            try {
                file.createNewFile();
                properties.store(new FileOutputStream(file), "Panel config file.");
                Config.load();
                R.load();
                s.send(new TipsJson(R.get("guide-complete")).toString());
            } catch (Exception e) {
                e.printStackTrace();
                s.send(new TipsJson(R.get("guide-exception")).toString());
            }
            return null;
        };
    }

    private Handler auth() {
        return (p,s) -> {
            AuthJson json = gson.fromJson(p, AuthJson.class);;
            if (auth.check(json.token) > 0) {
                GotoJson j = new GotoJson();
                j.page = "main";
                s.send(j.toString());
                return j.toString();
            }
            return null;
        };
    }

    private Handler main() {
        return (p,s) -> {
            //Log.info(R.get("access-main"));
            new Thread(()->{
                Runtime runtime = Runtime.getRuntime();
                Server server = Server.getInstance();
                SystemJson sj = new SystemJson();
                sj.cpuname = Monitor.getCPUName();
                sj.jre = Monitor.getJre();
                sj.path = Monitor.getRunningPath();
                sj.system = Monitor.getSystem();
                sj.admin = Monitor.getUser();
                s.send(sj.toString());
                while (true) {
                    try {
                        float load = server.getTickUsage();
                        double usedMB = NukkitMath.round((double)(runtime.totalMemory() - runtime.freeMemory()) / 1024.0D / 1024.0D, 2);
                        double maxMB = NukkitMath.round((double)runtime.maxMemory() / 1024.0D / 1024.0D, 2);
                        double usage = usedMB / maxMB * 100.0D;
                        double jvmr = NukkitMath.round(usage, 2);
                        double osmr = Monitor.getOSMenRound();
                        GetMainJson j = new GetMainJson();
                        double plr = (server.getOnlinePlayers().size() / server.getMaxPlayers()) * 10;
                        double up = NukkitMath.round(server.getNetwork().getUpload() / 1024.0D * 1000.0D, 2);
                        double down = NukkitMath.round(server.getNetwork().getDownload() / 1024.0D * 1000.0D, 2);
                        j.load = load;
                        j.jvmr = jvmr;
                        j.osmr = osmr;
                        j.plr = (plr == 0)? 1: plr;
                        j.pmax = server.getMaxPlayers();
                        j.pol = server.getOnlinePlayers().size();
                        j.up = up;
                        j.down = down;
                        j.cpu = Monitor.getCPULoad();
                        j.osmemory = Monitor.getOSMen();
                        j.vmmemory = usedMB + "MB / " + maxMB + "MB";
                        j.thread = Thread.getAllStackTraces().size();
                        j.ver = server.getVersion();
                        j.protocol = ProtocolInfo.CURRENT_PROTOCOL;
                        j.tps = server.getTicksPerSecond();
                        j.motd = server.getMotd().replaceAll("§.", "");
                        String nk = (server.getCodename().equals(""))? "NukkitX": server.getCodename();
                        j.nukkit = nk.contains("Nukkit")? nk: server.getName();
                        long time = System.currentTimeMillis() - Nukkit.START_TIME;
                        j.runtime = Format.formatUptime(time);
                        Map<Integer, Level> levels = server.getLevels();
                        for (Map.Entry<Integer, Level> entry : levels.entrySet()) {
                            GetMainJson.Level level = new GetMainJson.Level();
                            level.name = entry.getValue().getFolderName();
                            level.chunks = entry.getValue().getChunks().size();
                            level.entity = entry.getValue().getEntities().length;
                            level.player = entry.getValue().getPlayers().size();
                            j.level.add(level);
                        }
                        s.send(j.toString());
                        Thread.sleep(5000);
                    } catch (Exception e) {
                        break;
                    }
                }
            }).start();
            return null;
        };
    }

    private Handler log() {
        return (p,s) -> {
            new Thread(() -> {
                Log log = new Log();
                LogJson j = new LogJson();
                j.logs = log.getLogToList();
                s.send(j.toString());
                while (true) {
                    try {
                        ArrayList<String> map = log.getLogNewLineToList();
                        if (map.size() > 0) {
                            LogJson lj = new LogJson();
                            lj.logs = map;
                            s.send(lj.toString());
                        }
                        Thread.sleep(1000);
                    } catch (Exception e) {
                        break;
                    }
                }
            }).start();
            return null;
        };
    }

    private Handler cmd() {
        return  (p,s) -> {
            CommandJson json = gson.fromJson(p, CommandJson.class);
            new NukkitRunnable() {
                @Override
                public void run() {
                    Main.getInstance().getLogger().info("Input: " + json.cmd);
                    Server.getInstance().dispatchCommand(new ConsoleCommandSender(), json.cmd);
                }
            }.runTask(Main.getInstance());
            return null;
        };
    }

    private Handler getPlugins() {
        return (p,s) -> {
            Map<String, Plugin> plugins = server.getPluginManager().getPlugins();
            ArrayList<PluginJson> list = new ArrayList<>();
            for (Map.Entry<String, Plugin> entry : plugins.entrySet()) {
                PluginJson pj = new PluginJson();
                pj.name = entry.getKey();
                pj.ver = entry.getValue().getDescription().getVersion();
                pj.main = entry.getValue().getDescription().getMain();
                List<String> authors = entry.getValue().getDescription().getAuthors();
                pj.author = (authors.size() > 0)? authors.get(0): "";
                pj.des = entry.getValue().getDescription().getDescription();
                pj.on = entry.getValue().isEnabled();
                list.add(pj);
            }
            s.send(new Gson().toJson(list));
            Log.info(R.get("get-plugins"));
            return null;
        };
    }

    private Handler switchPlugin() {
        return (p,s) -> {
            SetPluginJson json = gson.fromJson(p, SetPluginJson.class);
            Plugin plugin = manager.getPlugin(json.name);
            if (json.on == 1) {
                Log.info(R.get("enable-plugin") + ": " + json.name);
                server.getPluginManager().enablePlugin(plugin);
            } else if (json.on == 0){
                Log.info(R.get("disable-plugin") + ": " + json.name);
                server.getPluginManager().disablePlugin(plugin);
            } else if (json.on == 2) {
                Log.info(R.get("reload-plugin") + " :" + json.name);
                server.getPluginManager().disablePlugin(plugin);
                server.getPluginManager().enablePlugin(plugin);
            } else if (json.on == 3) {
                try {
                    new BPM().delete(plugin, server);
                    Log.info(R.get("delete-plugin") + ": " + json.name);
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                    s.send(new TipsJson(R.get("bpm-del-exception") + e.getMessage()).toString());
                    return null;
                }
            }
            new Thread(() -> {
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                GotoJson r = new GotoJson();
                r.page = "plugins";
                s.send(r.toString());
            }).start();
            return null;
        };
    }

    private Handler getPlayers() {
        return (p,s) -> {
            Log.info(R.get("get-players"));
            new Thread(() -> {
                while (true) {
                    try {
                        Map<UUID, Player> map = Server.getInstance().getOnlinePlayers();
                        if (map.size() == 0) {
                            Thread.sleep(3000);
                            continue;
                        }
                        ArrayList<PlayerInfoJson> list = new ArrayList<>();
                        for (Map.Entry<UUID, Player> entry: map.entrySet()) {
                            PlayerInfoJson j = new PlayerInfoJson();
                            Player player = entry.getValue();
                            j.name = player.getName();
                            j.mode = player.getGamemode();
                            j.level = player.getLevel().getName();
                            j.isXbox = player.getLoginChainData().isXboxAuthed();
                            j.position = "x" + ((int)player.getX())+";y"+((int)player.getY())+";z"+((int)player.getZ());
                            list.add(j);
                        }
                        s.send(new Gson().toJson(list));
                        Thread.sleep(3000);
                    } catch (Exception e) {
                        break;
                    }
                }
            }).start();
            return null;
        };
    }

    private Handler playerDetails() {
        return (p,s) -> {
            GetPlayerDetailsJson json = gson.fromJson(p, GetPlayerDetailsJson.class);
            Player player = Server.getInstance().getPlayer(json.player);
            PlayerInfoJson j = new PlayerInfoJson();
            j.position = "x" + ((int)player.getX())+";y"+((int)player.getY())+";z"+((int)player.getZ());
            j.isXbox = player.getLoginChainData().isXboxAuthed();
            j.level = player.getLevel().getName();
            j.name = player.getName();
            j.xuid = player.getLoginChainData().getXUID();
            j.uuid = player.getUniqueId().toString();
            j.hp = player.getHealth();
            j.lv = player.getExperienceLevel();
            j.mode = player.getGamemode();
            j.op = player.isOp();
            j.device = player.getLoginChainData().getDeviceModel();
            j.deviceOS = player.getLoginChainData().getDeviceOS();
            j.deviceID = player.getLoginChainData().getDeviceId();
            j.address = player.getAddress();
            s.send(j.toString());
            Log.info(R.get("get-player-info") + ": " + json.player);
            return null;
        };
    }

    private Handler playerOper() {
        return (p,s) -> {
            PlayerOperJson json = gson.fromJson(p, PlayerOperJson.class);
            Player player = Server.getInstance().getPlayer(json.player);
            switch (json.oper) {
                case "MESSAGE":
                    player.sendMessage(json.msg);
                    Log.info(R.get("send-message-2-player").replaceFirst("%P%", json.player) + ": " + json.msg);
                    break;
                case "TITLE":
                    player.sendTitle(json.msg);
                    Log.info(R.get("send-title-2-player").replaceFirst("%P%", json.player) + ": " + json.msg);
                    break;
                case "SUBTITLE":
                    player.sendTitle("", json.msg);
                    Log.info(R.get("send-subtitle-2-player").replaceFirst("%P%", json.player) + ": " + json.msg);
                    break;
                case "TIPS":
                    player.sendTip(json.msg);
                    Log.info(R.get("send-tips-2-player").replaceFirst("%P%", json.player) + ": " + json.msg);
                    break;
                case "OP":
                    player.setOp(json.bool);
                    Log.info(R.get("set-player-op") + "[" + json.player + "] " + json.bool);
                    break;
                case "KICK":
                    player.kick();
                    Log.info(R.get("kick-player-1") + ": " + json.player);
                    break;
                case "MODE":
                    player.setGamemode(json.i);
                    Log.info(R.get("set-player-gm") + "[" + json.player + "] " + json.i);
            }
            return null;
        };
    }

    private Handler fileList() {
        return (p,s) -> {
            GetFilesJson json = gson.fromJson(p, GetFilesJson.class);
            ArrayList<FileInfo> list = FileIO.getFileList(json.path);
            FileListJson j = new FileListJson();
            j.position = "";
            j.list = list;
            s.send(new Gson().toJson(j));
            Log.info(R.get("get-files") + ": /");
            return null;
        };
    }

    private Handler toPath() {
        return  (p,s) -> {
            FileCdPath json = gson.fromJson(p, FileCdPath.class);
            ArrayList<FileInfo> list = FileIO.getFileList(json.path);
            FileListJson j = new FileListJson();
            j.position = json.path;
            j.list = list;
            s.send(new Gson().toJson(j));
            Log.info(R.get("get-files") + ": " + j.position);
            return null;
        };
    }

    private Handler fileDelete() {
        return (p,s) -> {
            FileDeleteJson json = gson.fromJson(p, FileDeleteJson.class);
            for (int i = 0; i < json.files.length; i++) {
                try {
                    String file = ((json.path.equals("")) ? "" : json.path + "/") + json.files[i];
                    FileUtils.forceDelete(new File(file));
                    Log.info(R.get("delete-files") + ": " + file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ArrayList<FileInfo> list = FileIO.getFileList((json.path.equals(""))?"./":json.path);
            FileListJson j = new FileListJson();
            j.position = json.path;
            j.list = list;
            s.send(new Gson().toJson(j));
            return null;
        };
    }
    
    private Handler fileRename() {
        return (p,s) -> {
            FileRenameJson json = gson.fromJson(p, FileRenameJson.class);
            String path = (json.path.equals(""))?"":json.path+"/";
            FileIO.rename(path + json.file, path + json.newname);
            Log.info(R.get("rename-file") + ": " + path + json.file + "->" + path + json.newname);
            ArrayList<FileInfo> list = FileIO.getFileList((json.path.equals(""))?"./":json.path);
            FileListJson j = new FileListJson();
            j.position = json.path;
            j.list = list;
            s.send(new Gson().toJson(j));
            return null;
        };
    }

    private Handler fileMove() {
        return (p,s) -> {
            FileMoveJson json = gson.fromJson(p, FileMoveJson.class);
            String path = (json.path.equals(""))?"./":json.path+"/";
            String target = (json.target.equals(""))?"./":json.target+"/";
            for (int i = 0; i < json.files.length; i++) {
                try {
                    File file = new File(path + json.files[i]);
                    if (file.isDirectory()) {
                        FileUtils.moveDirectory(file, new File(target + json.files[i]));
                    } else {
                        FileUtils.moveFile(file, new File(target + json.files[i]));
                    }
                    Log.info(R.get("move-file") + ": " + file + "->" + target + json.files[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ArrayList<FileInfo> list = FileIO.getFileList((json.path.equals(""))?"./":json.path);
            FileListJson j = new FileListJson();
            j.position = json.path;
            j.list = list;
            s.send(new Gson().toJson(j));
            return null;
        };
    }

    private Handler fileCopy() {
        return (p,s) -> {
            FileCopyJson json = gson.fromJson(p, FileCopyJson.class);
            String path = (json.path.equals(""))?"./":json.path+"/";
            String target = (json.target.equals(""))?"./":json.target+"/";
            for (int i = 0; i < json.files.length; i++) {
                try {
                    File file = new File(path + json.files[i]);
                    if (file.isDirectory()) {
                        FileUtils.copyDirectory(file, new File(target + json.files[i]));
                    } else {
                        FileUtils.copyFile(file, new File(target + json.files[i]));
                    }
                    Log.info(R.get("copy-file") + ": " + file + "->" + target + json.files[i]);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            ArrayList<FileInfo> list = FileIO.getFileList((json.path.equals(""))?"./":json.path);
            FileListJson j = new FileListJson();
            j.position = json.path;
            j.list = list;
            s.send(new Gson().toJson(j));
            return null;
        };
    }

    private Handler shutdown() {
        return (p,s) -> {
            ShutdownJson json = gson.fromJson(p, ShutdownJson.class);
            if (json.stop) {
                Server.getInstance().shutdown();
                Log.info(R.get("shutdown-server"));
            } else {
                Server.getInstance().reload();
                Log.info(R.get("reload-server"));
            }
            return null;
        };
    }

    private Handler fileRead() {
        return (p,s) -> {
            FileReadJson json = gson.fromJson(p, FileReadJson.class);
            FileReadJson j = new FileReadJson();
            j.file = json.file;
            File file = new File(json.file);
            if (file.length() < (1024*1024)) {
                try {
                    String content = Common.ch2Unicode(FileUtils.readFileToString(file, "UTF-8"));
                    j.content = Base64.getEncoder().encodeToString(content.getBytes("UTF-8"));
                    j.code = 200;
                    Log.info(R.get("read-file") + " :" + json.file);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                j.code = 501;
            }
            s.send(j.toString());
            return null;
        };
    }

    private Handler fileSave() {
        return (p,s) -> {
            FileSaveJson json = gson.fromJson(p, FileSaveJson.class);
            try {
                FileUtils.writeStringToFile(new File(json.file), Common.decodeUnicode(new String(Base64.getDecoder().decode(json.content))), "UTF-8");
                s.send(new TipsJson(R.get("file-save-success")).toString());
                Log.info(R.get("edit-file") + ": " + json.file);
            } catch (IOException e) {
                s.send(new TipsJson(R.get("file-save-fail") + e.getMessage()).toString());
                e.printStackTrace();
            }
            return null;
        };
    }

    private Handler fileCreate() {
        return (p,s) -> {
            FileCreateJson json = gson.fromJson(p, FileCreateJson.class);
            if (json.dir) {
                new File(json.name).mkdir();
                Log.info(R.get("make-dir") + ": " + json.name);
                ArrayList<FileInfo> list = FileIO.getFileList((json.path.equals(""))?"./":json.path);
                FileListJson j = new FileListJson();
                j.position = json.path;
                j.list = list;
                s.send(new Gson().toJson(j));
            } else {
                try {
                    new File(json.name).createNewFile();
                    Log.info(R.get("make-file") + ": " + json.name);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                FileReadJson j = new FileReadJson();
                j.file = json.name;
                j.content = "";
                j.code = 200;
                s.send(j.toString());
            }
            return null;
        };
    }
    
    private Handler fileUpload() {
        return (p,s) -> {
            FileUploadJson json = gson.fromJson(p, FileUploadJson.class);
            Log.info(R.get("upload-file") + ": " + json.path + "/" + json.file);
            WebSocketServer.upload_md5 = json.md5;
            WebSocketServer.upload_name = json.file;
            WebSocketServer.upload_path = json.path;
            return null;
        };
    }

    private Handler taskList() {
        return (p,s) -> {
            GetTasksJson json = gson.fromJson(p, GetTasksJson.class);
            json.tasks = TaskManager.getList();
            Log.info(R.get("get-tasks"));
            s.send(json.toString());
            return null;
        };
    }

    private Handler createTask() {
        return (p,s) -> {
            TaskCreateJson json = gson.fromJson(p, TaskCreateJson.class);
            Task task = new Task();
            task.title = json.title;
            task.expression = json.cron;
            task.command = json.command.split("\n");
            task.status = true;
            boolean add = TaskManager.add(task);
            Log.info(R.get("make-task") + ": " + json.title);
            GetTasksJson j = gson.fromJson(p, GetTasksJson.class);
            j.tasks = TaskManager.getList();
            s.send(j.toString());
            if (!add) {
                s.send(new DialogJson("Cron " + R.get("invalid-expression") + "!").toString());
            }
            return null;
        };
    }

    private Handler deleteTask() {
        return (p,s) -> {
            TaskDeleteJson json = gson.fromJson(p, TaskDeleteJson.class);
            TaskManager.getList().list.remove(json.key);
            TaskManager.getList().save();
            TaskManager.removeJob(String.valueOf(json.key));
            GetTasksJson j = gson.fromJson(p, GetTasksJson.class);
            j.tasks = TaskManager.getList();
            s.send(j.toString());
            return null;
        };
    }

    private Handler pauseTask() {
        return (p,s) -> {
            TaskPauseJson json = gson.fromJson(p, TaskPauseJson.class);
            TaskManager.getList().pause(json.key);
            TaskManager.getList().save();
            TaskManager.update();
            GetTasksJson j = gson.fromJson(p, GetTasksJson.class);
            j.tasks = TaskManager.getList();
            s.send(j.toString());
            return null;
        };
    }

    private Handler bpmSearch() {
        return (p,s) -> {
            BpmSearchJson json = gson.fromJson(p, BpmSearchJson.class);
            new Thread(() -> {
                BPM bpm = new BPM();
                try {
                    HashMap<String, String> search = bpm.search(json.word);
                    Log.info(R.get("bpm-search") + " :" + json.word);
                    json.list = search;
                    s.send(json.toString());
                } catch (Exception e) {
                    s.send(new TipsJson(e.getMessage()).toString());
                }
            }).start();
            return null;
        };
    }

    private Handler bpmInfo() {
        return (p,s) -> {
            BpmInfoJson json = gson.fromJson(p, BpmInfoJson.class);
            new Thread(() -> {
                BPM bpm = new BPM();
                BpmInfoJson info = new BpmInfoJson(bpm.info(json.name));
                s.send(info.toString());
            }).start();
            return null;
        };
    }

    private Handler bpmOperation() {
        return (p,s) -> {
            BpmOperationJson json = gson.fromJson(p, BpmOperationJson.class);
            new Thread(() -> {
                try {
                    File file = new BPM().install(json.plugin, json.version);
                    Log.info(R.get("bpm-install") + ": " + json.plugin + " v" + json.version);
                    if (file.exists()) {
                        s.send(new TipsJson(json.plugin + " v" + json.version + " " + R.get("bpm-installed")).toString());
                        Plugin plugin = server.getPluginManager().loadPlugin(file);
                        Server.getInstance().getPluginManager().enablePlugin(plugin);
                    } else {
                        s.send(new TipsJson(R.get("bpm-install-fail")).toString());
                    }
                } catch (DownloadException e) {
                    e.printStackTrace();
                    s.send(new TipsJson(R.get("bpm-exception") + e.getMessage()).toString());
                }
            }).start();
            return null;
        };
    }

    private Handler bpmCheck() {
        return (p, s) -> {
            BpmCheckJson json = gson.fromJson(p, BpmCheckJson.class);
            Plugin plugin = server.getPluginManager().getPlugin(json.plugin);
            if (plugin == null) return null;
            new Thread(() -> {
                BPM bpm = new BPM();
                String version = plugin.getDescription().getVersion();
                String check = bpm.check(json.plugin);
                if (check == null) {
                    s.send(new TipsJson(R.get("bpm-not-found")).toString());
                } else if (version.equals(check)) {
                    s.send(new TipsJson(R.get("bpm-newest")).toString());
                } else {
                    json.version = version;
                    json.newest = check;
                    s.send(json.toString());
                }
            }).start();
            return null;
        };
    }

}
