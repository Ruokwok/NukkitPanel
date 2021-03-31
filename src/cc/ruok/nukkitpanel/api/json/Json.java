package cc.ruok.nukkitpanel.api.json;

import com.google.gson.Gson;

public class Json {

    public String type;

    public String token;

    public String getType() {
        return type;
    }

    @Override
    public String toString() {
        return new Gson().toJson(this);
    }

}
