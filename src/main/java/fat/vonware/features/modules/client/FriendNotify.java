package fat.vonware.features.modules.client;

import fat.vonware.features.modules.Module;
import net.minecraft.network.play.client.CPacketChatMessage;

public class FriendNotify extends Module {

    public static FriendNotify INSTANCE;

    public FriendNotify() {
        super("FriendNotifier", ".", Category.CLIENT, false, false, false);
        this.enabled.setValue(false);
        this.drawn.setValue(false);
        INSTANCE = this;
    }


    public void alert(String name, boolean remove) {
        if (isEnabled() && mc.getConnection() != null) {
            String msg = remove ? "You've been Removed!" : "You've been Friended!";
            mc.getConnection().sendPacket(new CPacketChatMessage("/msg " + name + " " + msg));
        }
    }
}

