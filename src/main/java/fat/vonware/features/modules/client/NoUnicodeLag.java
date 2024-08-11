package fat.vonware.features.modules.client;

import fat.vonware.event.events.PacketEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoUnicodeLag extends Module {

    public NoUnicodeLag() {
        super("NoUnicodeLag", "lag lol :xdv", Category.CLIENT, true, false, false);
    }

    public Setting<Boolean> sendMessage = this.register(new Setting("Send Message", true));
    public String[] list = {"\u263B", "\u2465", "\u267B", "\u2525", "\uB9A2", "\uC384", "\u7E3A", "\u2C37", "\u84B9", "\uA225", "\u84B9", "\u3AC3", "\u317E", "\u2C36", "\u3AC3", "\u347E", "\u2673", "\u7340", "\u7465", "\u2220", "\uB9A2", "\uC384", "\u773D", "\u656B", "\u3459", "\u6F4D", "\u5262", "\u4A30", "\u7156", "\u6173", "\u374F", "\u7257", "\u4439", "\u4954", "\u7A42", "\u4336", "\u5546", "\u644B", "\u675A", "\u6970", "\u3347", "\u3151", "\u5838", "\u756C", "\u6A6E", "\u3268", "\u4439", "\u4579", "\u5076", "\u4C74", "\u7835", "\u4840", "\u536D", "\u2266", "\u250A", "\uB9A2", "\uC384", "\u7E3A", "\u3935", "\u3535", "\u0101", "\u0201", "\u0301", "\u0401", "\u0501", "\u0601", "\u0701", "\u0801", "\u0901", "\u0A01", "\u0B01", "\u0E01", "\u0F01", "\u1001", "\u1101", "\u1201", "\u1301", "\u1401", "\u1501", "\u1601", "\u1701", "\u1801", "\u1901", "\u1A01", "\u1B01", "\u1C01", "\u1D01", "\u1E01", "\u7001", "\u7101", "\u7201", "\u7301", "\u7401", "\u7501", "\u7601", "\u7701", "\u7801", "\u7901", "\u7A01", "\u7B01", "\u7C01", "\u7D01", "\u7E01", "\u7F01", "\u8001", "\u8101", "\u8201", "\u8301", "\u8401", "\u8501", "\u8601", "\u8701", "\u8801", "\u8901", "\u8A01", "\u8B01", "\u8C01", "\u8D01", "\u8E01", "\u8F01", "\u9001", "\u9101", "\u9201", "\u9301", "\u9401", "\u9501", "\u9601", "\u9701", "\u9801", "\u9901", "\u9A01", "\u9B01", "\u9C01", "\u9D01", "\u9E01", "\u9F01", "\uA001", "\uA101", "\uA201", "\uA301", "\uA401", "\uA501", "\uA601", "\uA701", "\uA801", "\uA901", "\uAA01", "\uAC01", "\uAD01", "\uAE01", "\uAF01", "\uB001", "\uB101", "\uB201", "\uB301", "\uB401", "\uB501", "\uB601", "\uB701", "\uB801", "\uB901", "\uBA01", "\uBB01", "\uBC01", "\uBD01", "\u6500", "\u7300", "\u7400", "\u7200", "\u7500"};

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketChat) {
            String message = ((SPacketChat) event.getPacket()).getChatComponent().getUnformattedText();
            for (String unicode : list) {
                if (message.contains(unicode)) {
                    mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString("Blocked possible unicode lag message!"), 3950211);
                    event.setCanceled(true);
                }
            }
        }
    }
}
