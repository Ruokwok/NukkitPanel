package cc.ruok.nukkitpanel.api;

import cc.ruok.nukkitpanel.Config;
import cc.ruok.nukkitpanel.Log;
import cc.ruok.nukkitpanel.Main;
import cc.ruok.nukkitpanel.api.json.*;
import cc.ruok.nukkitpanel.file.FileIO;
import cc.ruok.nukkitpanel.servers.Auth;
import cc.ruok.nukkitpanel.servers.websocket.WebSocketServer;
import cc.ruok.nukkitpanel.utils.Common;
import cc.ruok.nukkitpanel.utils.Format;
import cc.ruok.nukkitpanel.utils.Monitor;
import cn.nukkit.Nukkit;
import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.ConsoleCommandSender;
import cn.nukkit.math.NukkitMath;
import cn.nukkit.network.protocol.ProtocolInfo;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginManager;
import cn.nukkit.scheduler.NukkitRunnable;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class API {

    private static API handler = new API();

    private Server server = Server.getInstance();
    private PluginManager manager = Server.getInstance().getPluginManager();
    private Gson gson = new Gson();

    private Auth auth = Auth.getInstance();

    private Map<String, Handler> apis = new HashMap<>();

    private API() {
        registerapis();
    }

    public static API getInstance() {
        return  handler;
    }

    public void registerapis() {
        apis.put("LOGIN", login());
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
            } else {
                aj.auth = false;
                aj.message = "password error";
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
                        j.motd = server.getMotd();
                        String nk = (server.getCodename().equals(""))? "NukkitX": server.getCodename();
                        j.nukkit = nk.contains("Nukkit")? nk: server.getName();
                        long time = System.currentTimeMillis() - Nukkit.START_TIME;
                        j.runtime = Format.formatUptime(time);
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
                j.logs = log.getLogToMap();
                s.send(j.toString());
                while (true) {
                    try {
                        LinkedHashMap<String, String> map = log.getLogNewLineToMap();
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
            return null;
        };
    }

    private Handler switchPlugin() {
        return (p,s) -> {
            SetPluginJson json = gson.fromJson(p, SetPluginJson.class);
            Plugin plugin = manager.getPlugin(json.name);
            if (json.on == 1) {
                Server.getInstance().getPluginManager().enablePlugin(plugin);
            } else if (json.on == 0){
                Server.getInstance().getPluginManager().disablePlugin(plugin);
            } else if (json.on == 2) {
                Server.getInstance().getPluginManager().disablePlugin(plugin);
                Server.getInstance().getPluginManager().enablePlugin(plugin);
            }
            GotoJson r = new GotoJson();
            r.type = "GOTO";
            r.page = "plugins";
            s.send(r.toString());
            return null;
        };
    }

    private Handler getPlayers() {
        return (p,s) -> {
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
                    break;
                case "TITLE":
                    player.sendTitle(json.msg);
                    break;
                case "SUBTITLE":
                    player.sendTitle("", json.msg);
                    break;
                case "TIPS":
                    player.sendTip(json.msg);
                    break;
                case "OP":
                    player.setOp(json.bool);
                    break;
                case "KICK":
                    player.kick();
                    break;
                case "MODE":
                    player.setGamemode(json.i);
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
            return null;
        };
    }

    private Handler fileDelete() {
        return (p,s) -> {
            FileDeleteJson json = gson.fromJson(p, FileDeleteJson.class);
            for (int i = 0; i < json.files.length; i++) {
                try {
                    FileUtils.forceDelete(new File(((json.path.equals(""))?"":json.path+"/")+json.files[i]));
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
            } else {
                Server.getInstance().reload();
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
            } catch (IOException e) {
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
                ArrayList<FileInfo> list = FileIO.getFileList((json.path.equals(""))?"./":json.path);
                FileListJson j = new FileListJson();
                j.position = json.path;
                j.list = list;
                s.send(new Gson().toJson(j));
            } else {
                try {
                    new File(json.name).createNewFile();
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
            WebSocketServer.upload_md5 = json.md5;
            WebSocketServer.upload_name = json.file;
            WebSocketServer.upload_path = json.path;
            return null;
        };
    }

}
