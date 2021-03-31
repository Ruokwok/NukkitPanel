package cc.ruok.nukkitpanel.api.json;

public class PlayerInfoJson extends Json {

    public PlayerInfoJson() {
        type = "PLAYER_INFO";
    }

    public String name;     //昵称

    public String level;    //所在世界

    public String position; //坐标

    public boolean isXbox;  //Xbox账号登录

    public String xuid;     //Xbox账号

    public String uuid;

    public float hp;        //生命值

    public int lv;

    public int mode;

    public boolean op;

    public String device;

    public int deviceOS;

    public String deviceID;

    public String address;

}
