package cc.ruok.nukkitpanel.api.json;

public class TipsJson extends Json {

    public TipsJson(String msg) {
        type = "TIPS";
        message = msg;
    }

    public String message;

}
