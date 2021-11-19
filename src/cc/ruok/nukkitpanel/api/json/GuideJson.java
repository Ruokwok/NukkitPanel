package cc.ruok.nukkitpanel.api.json;

public class GuideJson extends Json {

    public String username;

    public String password;

    public int port;

    public String entry;

    public boolean ftp;

    public int ftpPort;

    public boolean website;

    public String lang;

    public boolean log;

    public GuideJson() {
        type = "GUIDE";
    }

}
