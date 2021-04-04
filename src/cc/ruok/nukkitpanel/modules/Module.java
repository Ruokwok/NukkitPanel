package cc.ruok.nukkitpanel.modules;

import cc.ruok.nukkitpanel.api.API;
import cc.ruok.nukkitpanel.api.Handler;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

public class Module {

    private String id;

    private String html;

    private String icon;

    private String title;

    public Module(String id) {
        this.id = id;
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
}