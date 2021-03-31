package cc.ruok.nukkitpanel.api.json;

public class GetMainJson extends GetJson {

    public GetMainJson() {
        type = "GET_MAIN";
    }

    public float load;      //负载

    public double jvmr;     //JVM内存

    public double osmr;     //系统内存

    public double plr;      //在线人数百分比

    public int pmax;        //最大人数

    public int pol;         //在线人数

    public double up;       //上行网速

    public double down;     //下行网速

    public String runtime;  //运行时间

    public int thread;      //线程数量

    public String osmemory; //系统内存详情

    public String vmmemory; //虚拟机内存详情

    public double cpu;      //处理器利用率

    public String kernel;   //内核

    public double tps;      //tps

    public String ver;  //版本

    public int protocol;    //协议

    public String motd;     //MOTD;

    public String nukkit;   //内核名称

}
