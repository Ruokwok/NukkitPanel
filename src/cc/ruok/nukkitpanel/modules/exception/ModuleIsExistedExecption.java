package cc.ruok.nukkitpanel.modules.exception;

public class ModuleIsExistedExecption extends ModuleExecption {

    private String id;

    public ModuleIsExistedExecption(String id) {
        this.id = id;
    }

    @Override
    public String getMessage() {
        return "This ID is already in use. ->" + id;
    }

}
