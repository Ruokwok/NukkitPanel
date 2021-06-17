package cc.ruok.nukkitpanel.api.json;

public class DialogJson extends Json {

    public String message;

    public DialogJson(String message) {
        this.message = message;
        this.type = "DIALOG";
    }

}
