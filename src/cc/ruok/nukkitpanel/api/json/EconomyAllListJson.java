package cc.ruok.nukkitpanel.api.json;

import java.util.LinkedHashMap;

public class EconomyAllListJson extends Json {

    public EconomyAllListJson() {
        type = "MD_ECONOMY_LIST";
    }

    public String coin;

    public LinkedHashMap<String, Double> list;

}
