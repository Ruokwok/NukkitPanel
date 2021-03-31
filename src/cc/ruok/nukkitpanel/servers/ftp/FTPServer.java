package cc.ruok.nukkitpanel.servers.ftp;

import cc.ruok.nukkitpanel.Config;
import org.apache.ftpserver.FtpServer;
import org.apache.ftpserver.FtpServerFactory;
import org.apache.ftpserver.ftplet.Authority;
import org.apache.ftpserver.ftplet.FtpException;
import org.apache.ftpserver.listener.ListenerFactory;
import org.apache.ftpserver.usermanager.impl.BaseUser;
import org.apache.ftpserver.usermanager.impl.WritePermission;

import java.util.ArrayList;
import java.util.List;

public class FTPServer {

    private FtpServerFactory serverFactory = new FtpServerFactory();
    private FtpServer server;
    
    public FTPServer() {
        ListenerFactory factory = new ListenerFactory();
        factory.setPort(Config.getFtpPort());
        serverFactory.addListener("default", factory.createListener());
        BaseUser user = new BaseUser();
        user.setName(Config.getUsername());
        user.setPassword(Config.getPassword());
        user.setHomeDirectory("./");
        List<Authority> authorities = new ArrayList<Authority>();
        //增加写权限
        authorities.add(new WritePermission());
        user.setAuthorities(authorities);
        try {
            serverFactory.getUserManager().save(user);
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public void start() {
        server = serverFactory.createServer();
        try {
            server.start();
        } catch (FtpException e) {
            e.printStackTrace();
        }
    }

    public void close() {
        server.stop();
    }

}
