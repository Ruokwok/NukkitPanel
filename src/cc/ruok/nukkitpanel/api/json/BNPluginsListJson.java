package cc.ruok.nukkitpanel.api.json;

import java.util.Map;

public class BNPluginsListJson extends Json {

    public BNPluginsListJson() {
        type = "MD_BN_LIST";
    }

    public Map<String, BNP> list;

    public static class BNP {

        public boolean stats;

        public boolean bnpm;

    }

}
