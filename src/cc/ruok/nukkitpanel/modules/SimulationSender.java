package cc.ruok.nukkitpanel.modules;

import cn.nukkit.command.ConsoleCommandSender;

public class SimulationSender extends ConsoleCommandSender {

    @Override
    public void sendMessage(String message) {
        System.out.println(message);
    }
}
