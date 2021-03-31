package cc.ruok.nukkitpanel.api.json;

public class FileReadJson extends Json{

    public FileReadJson() {
        type = "READ_FILE";
    }

    public String file;

    public String content;

    public int code;

}
