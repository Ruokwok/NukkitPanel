package cc.ruok.nukkitpanel.api;

import cc.ruok.nukkitpanel.api.json.Json;
import org.java_websocket.WebSocket;

public interface Handler {

    String run(String json, WebSocket socket);

}
