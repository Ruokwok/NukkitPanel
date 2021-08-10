package cc.ruok.nukkitpanel.api.json;

import java.util.Map;

public class BpmSearchJson extends Json {

    public BpmSearchJson() {
        type = "BPM_SEARCH";
    }

    public String word;

    public Map<String, String> list;

}
