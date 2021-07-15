package cc.ruok.nukkitpanel.modules;

import java.io.IOException;

public class BlocklyNukkit extends Module {

    public BlocklyNukkit() {
        super("blockly-nukkit", "BlocklyNukkit");
        try {
            setHTMLofResource("blockly-nukkit.html");
        } catch (IOException e) {
            e.printStackTrace();
        }
        setTitle("BlocklyNukkit");
    }



}
