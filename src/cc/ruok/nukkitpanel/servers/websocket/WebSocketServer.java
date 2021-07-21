package cc.ruok.nukkitpanel.servers.websocket;

import cc.ruok.nukkitpanel.Main;
import cc.ruok.nukkitpanel.R;
import cc.ruok.nukkitpanel.api.API;
import cc.ruok.nukkitpanel.api.json.*;
import cc.ruok.nukkitpanel.utils.Common;
import com.google.gson.Gson;
import org.apache.commons.io.FileUtils;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.WebSocket;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;

public class WebSocketServer extends org.java_websocket.server.WebSocketServer {

    private static int port;

    public static String upload_md5;
    public static String upload_name;
    public static String upload_path;

    public WebSocketServer(int port) {
        super(new InetSocketAddress(port));
        WebSocketServer.port = port;
        try {
            R.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(WebSocket webSocket, ClientHandshake clientHandshake) {

    }

    @Override
    public void onClose(WebSocket webSocket, int i, String s, boolean b) {

    }

    @Override
    public void onMessage(WebSocket webSocket, String s) {
        Json json = new Gson().fromJson(s, Json.class);
        String type = json.getType();
        API api = API.getInstance();
        if (!type.equals("LOGIN")) {
            if (api.state(json)) {
                api.get(type).run(s, webSocket);
            } else {
                webSocket.send(new NoPermissionJson().toString());
            }
        } else {
            api.get(type).run(s, webSocket);
        }
    }

    @Override
    public void onMessage(WebSocket webSocket, ByteBuffer buffer) {
        System.out.println(1);
        if (upload_name != null) {
            try {
                File file = new File(upload_path + "/" + upload_name);
                File temp = new File(Main.getInstance().getDataFolder() + "/temp/upload.bin");
                temp.createNewFile();
                FileChannel fc = new FileOutputStream(temp).getChannel();
                fc.write(buffer);
                String s = Common.md5(temp);
                if (s.equals(upload_md5)) {
                    if (file.exists()) {
                        FileUtils.forceDelete(file);
                    }
                    FileUtils.copyFile(temp, file);
//                    FileUtils.forceDelete(temp);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            upload_name = null;
        }
    }

    @Override
    public void onError(WebSocket webSocket, Exception e) {
        e.printStackTrace();
    }

    @Override
    public void onStart() {

    }

    public static int port() {
        return port;
    }
}
