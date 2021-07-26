package cc.ruok.nukkitpanel.modules;

import cc.ruok.nukkitpanel.api.json.TipsJson;
import cn.nukkit.command.ConsoleCommandSender;
import org.java_websocket.WebSocket;

public class SimulationSender extends ConsoleCommandSender {

    private WebSocket socket;

    public SimulationSender(WebSocket socket) {
        this.socket = socket;
    }

    @Override
    public void sendMessage(String message) {
        try {
            socket.send(new TipsJson(message.replaceAll("§.", "")).toString());
        } catch (Exception e) {}
    }
}
