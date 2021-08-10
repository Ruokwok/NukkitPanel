package cc.ruok.nukkitpanel.api.json;

import cc.ruok.bpm.json.Artifacts;

public class BpmInfoJson extends Json {

    public String name;

    public Artifacts info;

    public BpmInfoJson() {
    }

    public BpmInfoJson(Artifacts artifacts) {
        type = "BPM_INFO";
        info = artifacts;
    }

}
