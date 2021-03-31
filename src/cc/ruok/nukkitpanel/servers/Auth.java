package cc.ruok.nukkitpanel.servers;

import cc.ruok.nukkitpanel.utils.Common;
import cc.ruok.nukkitpanel.Config;

import java.util.HashMap;
import java.util.Map;

public class Auth {

    private static Auth auth = new Auth();

    private Map<String, Long> map = new HashMap<>();

    private String salt;

    private long saltEffectiveTime;

    private Auth() {}

    public static Auth getInstance() {
        return auth;
    }

    public String getAuth() {
        String token = Common.getRandomString(16);
        map.put(token, System.currentTimeMillis() + (Config.getKeepConnected() * 60000));
        return token;
    }

    /**
     * 更新令牌的过期时间
     */
    public void update(String token) {
        map.put(token, System.currentTimeMillis() + Config.getKeepConnected());
    }


    /**
     * 检查此令牌是否有权限
     * @param token
     * @return 1正常 0不存在 -1已过期
     */
    public int check(String token) {
        Long l = map.get(token);
        if (l == null) {
            return 0;
        } else if (l < System.currentTimeMillis()) {
            return -1;
        } else {
            return 1;
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

}
