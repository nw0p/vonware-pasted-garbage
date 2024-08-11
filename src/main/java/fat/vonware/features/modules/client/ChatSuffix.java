package fat.vonware.features.modules.client;

import fat.vonware.event.events.PacketEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class ChatSuffix extends Module {

    public ChatSuffix() {
        super("ChatSuffix", "suffix but chat", Category.CLIENT, true, false, false);
    }

    public Setting<Mode> mode = this.register(new Setting("Mode", Mode.Original));
    public String suffix;

    public enum Mode {
        Original,
        Phobos,
        FakeDotGod  ,
        SN0W,
        SEXMASTER,
        CHACHOOXWARE,
        TripleSix,
        Beta,

    }

    @Override
    public void onUpdate() {
        suffix = (mode.getValue() == Mode.Original ? "\u23D0 \u2756 \uFF36\u2661\u03B7\uFF57\u15E9\u2C64\u03B5.\u1572\u0454\u03BD \u2756" : mode.getValue() == Mode.Phobos ? "\u23D0 \u1D18\u029C\u1D0F\u0299\u1D0F\uA731" : mode.getValue() == Mode.FakeDotGod ? "\u23D0 \uFF24\uFF4F\uFF54\uFF27\uFF4F\uFF44\uFF0E\uFF23\uFF23" : mode.getValue() == Mode.SN0W ? "\u2744" : mode.getValue() == Mode.SEXMASTER ? "\u23d0 \uff33\uff45\ua1d3\uff2d\u039b\uff53\u01ac\u03b5\u0280\uff0e\uff23\uff23" : mode.getValue() == Mode.CHACHOOXWARE ? "\u23D0 \u1d04\u029c\u1d00\u1d04\u029c\u1d0f\u1d0f\u0445\u1d21\u1d00\u0280\u1d07": mode.getValue() == Mode.TripleSix ? " \u23D0 \u1D1B\u0280\u026A\u1D29\u029F\u1D07\u0455\u026A\u0445" :   "\u2733\u039E\uFF36\uFF4F\uFF4E\uFF57\uFF41\uFF52\uFF45.\u0432\uFF45\u01AC\u023A\u039E\u2733");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String message = ((CPacketChatMessage) event.getPacket()).message;
            if (message.startsWith("/")) {
                return;
            }
            ((CPacketChatMessage) event.getPacket()).message = message + " " + suffix;
        }
    }
}