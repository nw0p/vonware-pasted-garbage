package fat.vonware.features.modules.misc;

import fat.vonware.event.events.PacketEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import net.minecraft.client.Minecraft;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SmartDisconnect extends Module {

    public Setting<Integer> timeout = this.register(new Setting("Timeout", 3000, 0, 30000));

    public SmartDisconnect() {
        super("SmartDisconnect", "epic auto jqq but with a taste of passcode's ping", Category.MISC, true, false, false);
    }

    private long last = Long.MAX_VALUE;

    @Override
    public void onTick() {
        if (mc.getConnection() != null && System.currentTimeMillis() - last >= timeout.getValue()) {
            mc.getConnection().getNetworkManager().closeChannel(new TextComponentString("[SmartDisconnect] " + Minecraft.getMinecraft().getCurrentServerData().serverIP + " Has not replied for " + timeout.getValue() + "ms."));
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        last = System.currentTimeMillis();
    }
}
