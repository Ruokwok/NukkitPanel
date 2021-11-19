package cc.ruok.nukkitpanel.modules;

import cc.ruok.nukkitpanel.Log;
import cc.ruok.nukkitpanel.R;
import cc.ruok.nukkitpanel.api.API;
import cc.ruok.nukkitpanel.api.json.EconomyAllListJson;
import cc.ruok.nukkitpanel.api.json.EconomyEditJson;
import cc.ruok.nukkitpanel.api.json.TipsJson;
import cn.nukkit.Player;
import cn.nukkit.Server;
import com.google.gson.Gson;
import org.java_websocket.WebSocket;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

public class EconomyAPI extends Module {

    private me.onebone.economyapi.EconomyAPI economyAPI = me.onebone.economyapi.EconomyAPI.getInstance();

    private String coin = economyAPI.getMonetaryUnit();

    public EconomyAPI() {
        super("economy-api", "EconomyAPI");
        try {
            setHTMLofResource("economy-api.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        setTitle("Economy {{economics}}");
        setIcon("attach_money");
        API.getInstance().registerHandler("MD_ECONOMY_LIST", this::listApi);
        API.getInstance().registerHandler("MD_ECONOMY_EDIT", this::edit);
        API.getInstance().registerHandler("MD_ECONOMY_ADD", this::add);
        API.getInstance().registerHandler("MD_ECONOMY_REDUCE", this::reduce);
    }

    private String listApi(String json, WebSocket socket) {
        LinkedHashMap<String, Double> allMoney = economyAPI.getAllMoney();
        EconomyAllListJson j = new EconomyAllListJson();
        LinkedHashMap<String, Double> list = new LinkedHashMap<>();
        for (Map.Entry<String, Double> entry : allMoney.entrySet()) {
            String name = Server.getInstance().getOfflinePlayer(UUID.fromString(entry.getKey())).getName();
            list.put(name, entry.getValue());
        }
        j.list = list;
        j.coin = coin;
        socket.send(j.toString());
        return null;
    }

    private String edit(String json, WebSocket socket) {
        EconomyEditJson j = new Gson().fromJson(json, EconomyEditJson.class);
        economyAPI.setMoney(j.player, j.value);
        economyAPI.saveAll();
        Log.info(R.get("economy-edit").replaceFirst("%P%", j.player) + ": " + j.value);
        socket.send(new TipsJson(R.get("success")).toString());
        return null;
    }

    private String add(String json, WebSocket socket) {
        EconomyEditJson j = new Gson().fromJson(json, EconomyEditJson.class);
        economyAPI.addMoney(j.player, j.value);
        Log.info(R.get("economy-add").replaceFirst("%P%", j.player) + ": " + j.value);
        economyAPI.saveAll();
        socket.send(new TipsJson(R.get("success")).toString());
        return null;
    }

    private String reduce(String json, WebSocket socket) {
        EconomyEditJson j = new Gson().fromJson(json, EconomyEditJson.class);
        economyAPI.reduceMoney(j.player, j.value);
        Log.info(R.get("economy-reduce").replaceFirst("%P%", j.player) + ": " + j.value);
        economyAPI.saveAll();
        socket.send(new TipsJson(R.get("success")).toString());
        return null;
    }
}
