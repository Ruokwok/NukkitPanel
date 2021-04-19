package cc.ruok.nukkitpanel.modules;

import cc.ruok.nukkitpanel.api.API;
import cc.ruok.nukkitpanel.api.Handler;
import cc.ruok.nukkitpanel.modules.exception.ModuleExecption;
import cc.ruok.nukkitpanel.modules.exception.ModuleIsExistedExecption;
import cn.nukkit.plugin.Plugin;
import cn.nukkit.plugin.PluginBase;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Module {

    private String id;

    private String html = "<h1>This is a module page.</h1>";

    private String icon = "extension";

    private String title = "Module";

    private Plugin plugin;

    public Module(String id, PluginBase plugin) {
        this.id = id;
        this.plugin = plugin;
    }

    public String getId() {
        return id;
    }

    public API getAPI() {
        return API.getInstance();
    }

    public void registerHandler(String key, Handler handler) {
        API.getInstance().registerHandler(key, handler);
    }

    public void setHTML(String html) {
        this.html = html;
    }

    public void setHTML(InputStream inputStream) throws IOException {
        setHTML(inputStream, "utf8");
    }

    public void setHTML(InputStream inputStream, String charset) throws IOException {
        setHTML(IOUtils.toString(inputStream, charset));
    }

    public void setHTML(File htmlFile) throws IOException {
        setHTML(new FileInputStream(htmlFile));
    }

    public String getHTML() {
        return html;
    }


    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getDrawerHtml() {
        return "<a href=\"../" + id + "\"><li class=\"mdui-list-item mdui-ripple\">\n" +
                "<i class=\"mdui-list-item-icon mdui-icon material-icons\">" + icon + "</i>\n" +
                "<div class=\"mdui-list-item-content\">" + title + "</div>\n" +
                "</li></a>";
    }

    public void register() throws ModuleExecption {
        ModuleManager.getInstance().register(this);
    }

    public Plugin getPlugin() {
        return plugin;
    }

    public void remove() {
        ModuleManager.getInstance().remove(this);
    }
}