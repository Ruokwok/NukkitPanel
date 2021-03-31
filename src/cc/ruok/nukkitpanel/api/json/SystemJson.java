package cc.ruok.nukkitpanel.api.json;

public class SystemJson extends Json {

    public SystemJson() {
        type = "SYSTEM";
    }

    public String cpuname;  //cpu型号

    public String system;   //操作系统

    public String jre;      //运行环境

    public String path;     //运行目录

    public String admin;    //用户名

}
