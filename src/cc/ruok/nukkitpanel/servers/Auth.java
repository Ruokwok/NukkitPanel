package cc.ruok.nukkitpanel.servers;

import cc.ruok.nukkitpanel.Main;
import cc.ruok.nukkitpanel.utils.Common;
import cc.ruok.nukkitpanel.Config;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Auth {

    private static Auth auth = new Auth();

    private ConcurrentHashMap<String, String> map;

    private String salt;

    private long saltEffectiveTime;

    private File file = new File(Main.getInstance().getDataFolder() + "/temp/auth.json");

    private Auth() {
        try {
            String json = FileUtils.readFileToString(file, "utf8");
            map = new Gson().fromJson(json, ConcurrentHashMap.class);
            clear();
        } catch (IOException e) {
            map = new ConcurrentHashMap<>();
        }
    }

    public static Auth getInstance() {
        return auth;
    }

    public String getAuth() {
        String token = Common.getRandomString(16);
        map.put(token, String.valueOf(System.currentTimeMillis() + (Config.getKeepConnected()) * 60000));
        save();
        return token;
    }

    /**
     * 更新令牌的过期时间
     */
    public void update(String token) {
        map.put(token, String.valueOf(System.currentTimeMillis() + Config.getKeepConnected()));
        save();
    }


    /**
     * 检查此令牌是否有权限
     * @param token
     * @return 1正常 0不存在 -1已过期
     */
    public int check(String token) {
        try {
            Long l = Long.parseLong(map.get(token));
            if (l == null) {
                return 0;
            } else if (l < System.currentTimeMillis()) {
                map.remove(token);
                return -1;
            } else {
                return 1;
            }
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    public String getSalt() {
        if (salt == null || saltEffectiveTime < System.currentTimeMillis()) {
            salt = Common.getRandomString(8);
            saltEffectiveTime = System.currentTimeMillis() + 600000;
            return salt;
        } else {
            return salt;
        }
    }

    public void save() {
        clear();
        String json = new Gson().toJson(map, ConcurrentHashMap.class);
        try {
            IOUtils.write(json, new FileOutputStream(file), "utf8");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void clear() {
        for (Map.Entry<String, String> entry : map.entrySet()) {
            check(entry.getKey());
        }
    }

}
