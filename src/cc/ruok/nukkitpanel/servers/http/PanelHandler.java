package cc.ruok.nukkitpanel.servers.http;

import cc.ruok.nukkitpanel.Log;
import cc.ruok.nukkitpanel.servers.Auth;
import cc.ruok.nukkitpanel.Main;
import cc.ruok.nukkitpanel.R;
import cc.ruok.nukkitpanel.Config;
import cc.ruok.nukkitpanel.utils.Common;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.Map;
import java.util.Properties;

class PanelHandler {

    protected static void format(HttpExchange http) throws IOException {
        if (!Config.isOk()) {
            byte[] bytes = ("<head> <meta http-equiv=\"refresh\" content=\"0;url=" + Config.getEntry() + "/guide/\"></head>").getBytes();
            http.sendResponseHeaders(200, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        }
        if (!http.getRequestURI().getPath().endsWith("/")) {
            byte[] bytes = ("<head> <meta http-equiv=\"refresh\" content=\"0;url="+http.getRequestURI().getPath()+"/\"></head>").getBytes();
            http.sendResponseHeaders(200, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        }
    }

    static HttpHandler adminIndex() {
        return http -> {
            byte[] bytes = ("<head> <meta http-equiv=\"refresh\" content=\"0;url=" + Config.getEntry() + "/main/\"></head>").getBytes();
            http.sendResponseHeaders(200, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        };
    }


    /**
     * html资源
     */
    static HttpHandler adminMain() {
        return http -> {
            int code = 200;
            format(http);
            http.getResponseHeaders().add("Content-Type", "text/html; charset=" + Config.getCharset());
            byte[] bytes = R.getMainPage(http.getLocalAddress()).getBytes();
            http.sendResponseHeaders(code, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        };
    }

    static HttpHandler adminConsole() {
        return http -> {
            int code = 200;
            format(http);
            http.getResponseHeaders().add("Content-Type", "text/html; charset=" + Config.getCharset());
            byte[] bytes = R.getConsolePage(http.getLocalAddress()).getBytes();
            http.sendResponseHeaders(code, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        };
    }

    static HttpHandler adminTask() {
        return http -> {
            int code = 200;
            format(http);
            http.getResponseHeaders().add("Content-Type", "text/html; charset=" + Config.getCharset());
            byte[] bytes = R.getTaskPage(http.getLocalAddress()).getBytes();
            http.sendResponseHeaders(code, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        };
    }

    static HttpHandler adminLog() {
        return http -> {
            int code = 200;
            format(http);
            http.getResponseHeaders().add("Content-Type", "text/html; charset=" + Config.getCharset());
            byte[] bytes = R.getLogPage(http.getLocalAddress()).getBytes();
            http.sendResponseHeaders(code, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        };
    }

    static HttpHandler adminPlugins() {
        return http -> {
            int code = 200;
            format(http);
            http.getResponseHeaders().add("Content-Type", "text/html; charset=" + Config.getCharset());
            byte[] bytes = R.getPluginsPage(http.getLocalAddress()).getBytes();
            http.sendResponseHeaders(code, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        };
    }

    static HttpHandler adminPlayers() {
        return http -> {
            int code = 200;
            format(http);
            http.getResponseHeaders().add("Content-Type", "text/html; charset=" + Config.getCharset());
            byte[] bytes = R.getPlayersPage(http.getLocalAddress()).getBytes();
            http.sendResponseHeaders(code, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        };
    }

    static HttpHandler adminFiles() {
        return http -> {
            int code = 200;
            format(http);
            http.getResponseHeaders().add("Content-Type", "text/html; charset=" + Config.getCharset());
            byte[] bytes = R.getFilesPage(http.getLocalAddress()).getBytes();
            http.sendResponseHeaders(code, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        };
    }

    static HttpHandler download() {
        return http -> {
            String post = IOUtils.toString(http.getRequestBody()).replaceAll("&","\n").replaceAll("%2F","/");
            Properties properties = new Properties();
            properties.load(new StringReader(post));
            String token = properties.getProperty("token");
            String file = properties.getProperty("file");
            if (Auth.getInstance().check(token) == 1) {
                File f = new File(file);
                byte[] bytes = FileUtils.readFileToByteArray(f);
                Log.info(R.get("download-file") + ": " + file);
                http.getResponseHeaders().add("Content-Type", "application/octet-stream; charset=" + Config.getCharset());
                http.getResponseHeaders().add("Content-Disposition", "attachment;filename=" + f.getName());
                http.sendResponseHeaders(200, bytes.length);
                http.getResponseBody().write(bytes);
            } else {
                http.sendResponseHeaders(404, 0);
            }
            http.close();
        };
    }


    static HttpHandler adminLogin() {
        return http -> {
            int code = 200;
            format(http);
            http.getResponseHeaders().add("Content-Type", "text/html; charset=" + Config.getCharset());
            String str = R.getLoginPage(http.getLocalAddress()).replaceAll("\\{\\{js.salt}}", Auth.getInstance().getSalt());
            byte[] bytes = str.getBytes();
            http.sendResponseHeaders(code, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        };
    }


    /**
     * 图片资源
     */
    static HttpHandler image() {
        return http -> {
            String path = http.getRequestURI().getPath();
            path = (path.startsWith(Config.getEntry()))? path.replaceFirst(Config.getEntry(), "/admin") : path;
            InputStream file = Main.class.getResourceAsStream("/resources" + path);
            http.getResponseHeaders().add("Content-Type", "image/png;");
            byte[] bytes = IOUtils.toByteArray(file);
            http.sendResponseHeaders(200, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        };
    }

    static HttpHandler html(String path) {
        return http -> {
            int code = 200;
//            format(http);
            if (new File(Main.getInstance().getDataFolder() + "/panel.properties").exists()) {
                byte[] bytes = ("<head> <meta http-equiv=\"refresh\" content=\"0;url=" + Config.getEntry() + "/main/\"></head>").getBytes();
                http.sendResponseHeaders(200, bytes.length);
                http.getResponseBody().write(bytes);
                http.close();
                return;
            }
            http.getResponseHeaders().add("Content-Type", "text/html; charset=" + Config.getCharset());
            String jre = System.getProperty("java.version");
            String lang = "auto";
            String query = http.getRequestURI().getQuery();
            if (query != null && query.startsWith("lang=")) {
                try {
                    lang = Common.urlGet(query).get("lang");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            String page = R.getPage(path, http.getLocalAddress(), lang)
                    .replaceAll("\\{\\{guide.jre}}", jre)
                    .replaceAll("\\{\\{guide.lang}}", lang);
            if (jre.startsWith("1.8")) {
                page = page.replaceAll("\\{\\{guide.jre.status}}", "true");
            } else {
                page = page.replaceAll("\\{\\{guide.jre.status}}", "false");
            }
            try {
                String _page = R.format(page, lang);
                byte[] bytes = _page.getBytes();
                http.sendResponseHeaders(code, bytes.length);
                http.getResponseBody().write(bytes);
                http.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        };
    }


    /**
     * 文件资源
     */
    static HttpHandler file() {
        return http -> {
            String path = http.getRequestURI().getPath();
            path = (path.startsWith(Config.getEntry()))? path.replaceFirst(Config.getEntry(), "/admin") : path;
            InputStream file = Main.class.getResourceAsStream("/resources" + path);
            if (file == null) {
                byte[] bytes = "<h1>404 Not Found</h1><hr><p>Nukkit Panel</p>".getBytes();
                http.sendResponseHeaders(400, bytes.length);
                http.getResponseBody().write(bytes);
                http.close();
                return;
            }
            String type = "text/html";
            type = (path.endsWith(".html"))? "text/html": type;
            type = (path.endsWith(".js"))? "text/javascript": type;
            type = (path.endsWith(".css"))? "text/css": type;
            type = (path.endsWith(".png"))? "image/png": type;
            http.getResponseHeaders().add("Content-Type", type);
            byte[] bytes = IOUtils.toByteArray(file);
            http.sendResponseHeaders(200, bytes.length);
            http.getResponseBody().write(bytes);
            http.close();
        };
    }


    static HttpHandler website() {
        return http -> {
            String path = http.getRequestURI().getPath();
            File file = new File(Main.getInstance().getDataFolder() + "/wwwroot" + path);
            if (file.isDirectory()) {
                try {
                    file = new File(file.getPath() + "/index.html");
                } catch (Exception e) {e.printStackTrace();}
            }
            if (!file.exists()) {
                byte[] bytes = "<h1>404 Not Found</h1><hr><p>Nukkit Panel</p>".getBytes();
                http.sendResponseHeaders(400, bytes.length);
                http.getResponseBody().write(bytes);
                http.close();
                return;
            }
            path = file.getPath();
            String type = "application/octet-stream";
            type = (path.endsWith(".html"))? "text/html": type;
            type = (path.endsWith(".xhtml"))? "text/application/xhtml+xml": type;
            type = (path.endsWith(".txt"))? "text/html": type;
            type = (path.endsWith(".php"))? "text/html": type;
            type = (path.endsWith(".htm"))? "text/html": type;
            type = (path.endsWith(".json"))? "application/json": type;
            type = (path.endsWith(".xml"))? "application/xml": type;
            type = (path.endsWith(".js"))? "text/javascript": type;
            type = (path.endsWith(".css"))? "text/css": type;
            type = (path.endsWith(".png"))? "image/png": type;
            type = (path.endsWith(".jpg"))? "image/jpeg": type;
            type = (path.endsWith(".jpeg"))? "image/jpeg": type;
            type = (path.endsWith(".gif"))? "image/gif": type;
            type = (path.endsWith(".ico"))? "image/ico": type;
            type = (path.endsWith(".csv"))? "text/csv": type;
            http.getResponseHeaders().add("Content-Type", type);
            FileInputStream inputStream = new FileInputStream(file);
            byte[] bytes = IOUtils.toByteArray(inputStream);
            http.sendResponseHeaders(200, bytes.length);
            http.getResponseBody().write(bytes);
            inputStream.close();
            http.close();
        };
    }

}
