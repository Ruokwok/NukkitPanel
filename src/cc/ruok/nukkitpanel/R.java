package cc.ruok.nukkitpanel;

import cc.ruok.nukkitpanel.utils.Common;
import cc.ruok.nukkitpanel.utils.Format;
import cn.nukkit.Server;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URL;
import java.util.Map;

public class R {

    public static Map<String, String> lang;

    private static String mainPage;
    private static String consolePage;
    private static String pluginsPage;
    private static String playersPage;
    private static String filesPage;
    private static String logPage;
    private static String loginPage;
    private static String taskPage;

    public static void load() throws IOException {
        mainPage = Format.formatHTML(IOUtils.toString(Main.class.getResource("/resources/admin/index.html"), "utf8"));
        consolePage = Format.formatHTML(IOUtils.toString(Main.class.getResource("/resources/admin/console.html"), "utf8"));
        pluginsPage = Format.formatHTML(IOUtils.toString(Main.class.getResource("/resources/admin/plugins.html"), "utf8"));
        playersPage = Format.formatHTML(IOUtils.toString(Main.class.getResource("/resources/admin/players.html"), "utf8"));
        filesPage = Format.formatHTML(IOUtils.toString(Main.class.getResource("/resources/admin/files.html"), "utf8"));
        logPage = Format.formatHTML(IOUtils.toString(Main.class.getResource("/resources/admin/log.html"), "utf8"));
        loginPage = Format.formatHTML(IOUtils.toString(Main.class.getResource("/resources/admin/login.html"), "utf8"));
        taskPage = Format.formatHTML(IOUtils.toString(Main.class.getResource("/resources/admin/task.html"), "utf8"));
        lang = getLanguagePackage();
        for (Map.Entry<String, String> entry : lang.entrySet()) {
            mainPage = mainPage.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
            consolePage = consolePage.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
            pluginsPage = pluginsPage.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
            playersPage = playersPage.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
            filesPage = filesPage.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
            logPage = logPage.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
            loginPage = loginPage.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
            taskPage = taskPage.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
        }
    }

    public static String format(String html) {
        for (Map.Entry<String, String> entry : lang.entrySet()) {
            html = html.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
        }
        return html;
    }

    public static String format(String html, String l) {
        Map<String, String> language = getLanguagePackage(l);
        for (Map.Entry<String, String> entry : language.entrySet()) {
            html = html.replaceAll("\\{\\{" + entry.getKey() + "}}", entry.getValue());
        }
        return html;
    }

    public static Map<String, String> getLanguagePackage(String lang) {
        String pkg = "";
        URL url = Main.class.getResource("/resources/language/" + Server.getInstance().getLanguage().getLang() + ".lang");
        if (lang.equals("auto")) {
            if (url == null) {
                try {
                    pkg = IOUtils.toString(Main.class.getResource("/resources/language/eng.lang"), "utf8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                try {
                    pkg = IOUtils.toString(url, "utf8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            try {
                pkg = IOUtils.toString(Main.class.getResource("/resources/language/" + lang + ".lang"), "utf8");
            } catch (IOException e) {
                    try {
                        pkg = IOUtils.toString(Main.class.getResource("/resources/language/eng.lang"), "utf8");
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
        }
        return Common.ini2map(pkg);
    }

    public static Map<String, String> getLanguagePackage() {
        return getLanguagePackage(Config.getLang());
    }

    public static String getMainPage(InetSocketAddress address) {
        return Format.formatAddress(mainPage, address);
    }

    public static String getConsolePage(InetSocketAddress address) {
        return Format.formatAddress(consolePage, address);
    }

    public static String getPluginsPage(InetSocketAddress address) {
        return Format.formatAddress(pluginsPage, address);
    }

    public static String getPlayersPage(InetSocketAddress address) {
        return Format.formatAddress(playersPage, address);
    }

    public static String getFilesPage(InetSocketAddress address) {
        return Format.formatAddress(filesPage, address);
    }

    public static String getLogPage(InetSocketAddress address) {
        return Format.formatAddress(logPage, address);
    }

    public static String getLoginPage(InetSocketAddress address) {
        return Format.formatAddress(loginPage, address);
    }

    public static String getTaskPage(InetSocketAddress address) {
        return Format.formatAddress(taskPage, address);
    }

    public static String getPage(String page, InetSocketAddress address, String lang) throws IOException {
        String html = Format.formatHTML(IOUtils.toString(Main.class.getResource("/resources/" + page), "utf8"));
        return Format.formatAddress(format(html, lang), address);
    }

    public static String getPage(String page, InetSocketAddress address) throws IOException {
        String html = Format.formatHTML(IOUtils.toString(Main.class.getResource("/resources/" + page), "utf8"));
        return Format.formatAddress(format(html), address);
    }

    public static String get(String str) {
        return lang.get(str);
    }
}
