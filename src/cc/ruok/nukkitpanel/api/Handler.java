package cc.ruok.nukkitpanel.api;

import org.java_websocket.WebSocket;

public interface Handler {

    String run(String json, WebSocket socket);

}
