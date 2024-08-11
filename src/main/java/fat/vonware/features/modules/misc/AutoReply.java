package fat.vonware.features.modules.misc;

import fat.vonware.Vonware;
import fat.vonware.event.events.PacketEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.MathUtil;
import net.minecraft.network.play.server.*;
import net.minecraftforge.fml.common.eventhandler.*;

public class AutoReply extends Module
{
    Setting<Boolean> coords;

    public AutoReply() {
        super("AutoReply","autp reply rbh", Category.MISC, true, false, false);
        this.coords = (Setting<Boolean>)this.register(new Setting("CoordReply", true));
    }

    @SubscribeEvent
    public void onPacketReceive(final PacketEvent.Receive e) {
        if (fullNullCheck() || this.isDisabled()) {
            return;
        }
        if (e.getPacket() instanceof SPacketChat) {
            final SPacketChat p = (SPacketChat)e.getPacket();
            final String unormatted = p.getChatComponent().getUnformattedText();
            if (unormatted.contains("says: ") || unormatted.contains("whispers: ")) {
                final String ign = unormatted.split(" ")[0];
                if (AutoReply.mc.player.getName() == ign || !Vonware.friendManager.isFriend(ign) || MathUtil.getDistance(0.0, AutoReply.mc.player.posY, 0.0) > 5000.0) {
                    return;
                }
                final String msg = unormatted.toLowerCase();
                if (this.coords.getValue() && (msg.contains("cord") || msg.contains("coord") || msg.contains("coords") || msg.contains("cords") || msg.contains("wya") || msg.contains("where are you") || msg.contains("where r u") || msg.contains("where ru"))) {
                    if (msg.contains("discord") || msg.contains("record")) {
                        return;
                    }
                    final int x = (int)AutoReply.mc.player.posX;
                    final int z = (int)AutoReply.mc.player.posZ;
                    AutoReply.mc.player.sendChatMessage("/msg " + ign + " My coordinates are X: " + Math.round(mc.player.posX) + " Y: " + Math.round(mc.player.posY) + " Z: " + Math.round(mc.player.posZ) + " in the " + (mc.player.dimension == 0 ? "OverWorld" : mc.player.dimension == 1 ? "End" : mc.player.dimension == -1 ? "Nether" : "failed to detect dimension"));
                }
            }
        }
    }
}
