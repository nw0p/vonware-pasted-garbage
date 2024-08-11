package fat.vonware.features.modules.misc;

import fat.vonware.Vonware;
import fat.vonware.event.events.PacketEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.TextUtil;
import net.minecraft.network.play.client.CPacketChatMessage;
import net.minecraft.network.play.server.SPacketChat;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ChatModifier
        extends Module {
    public static ChatModifier INSTANCE = new ChatModifier();
    public Setting<Boolean> clean = this.register(new Setting<Boolean>("NoChatBackground", Boolean.valueOf(false), "Cleans your chat"));
    public Setting<Boolean> infinite = this.register(new Setting<Boolean>("InfiniteChat", Boolean.valueOf(false), "Makes your chat infinite."));
    public Setting<TextUtil.Color> timeStamps = this.register(new Setting<TextUtil.Color>("Time", TextUtil.Color.NONE));
    public Setting<Boolean> rainbowTimeStamps = this.register(new Setting<Object>("RainbowTimeStamps", Boolean.valueOf(false), v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting<TextUtil.Color> bracket = this.register(new Setting<Object>("Bracket", TextUtil.Color.WHITE, v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public Setting<Boolean> space = this.register(new Setting<Object>("Space", Boolean.valueOf(true), v -> this.timeStamps.getValue() != TextUtil.Color.NONE));
    public boolean check;

    public ChatModifier() {
        super("BetterChat", "Modifies your chat", Category.MISC, true, false, false);
        this.setInstance();
    }

    public static ChatModifier getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new ChatModifier();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketChatMessage) {
            String s = ((CPacketChatMessage) event.getPacket()).getMessage();
            this.check = !s.startsWith(Vonware.commandManager.getPrefix());
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getStage() == 0 && this.timeStamps.getValue() != TextUtil.Color.NONE && event.getPacket() instanceof SPacketChat) {
            if (!((SPacketChat) event.getPacket()).isSystem()) {
                return;
            }
            String originalMessage = ((SPacketChat) event.getPacket()).chatComponent.getFormattedText();
            String message = this.getTimeString(originalMessage) + originalMessage;
            ((SPacketChat) event.getPacket()).chatComponent = new TextComponentString(message);
        }
    }

    public String getTimeString(String message) {
        String date = new SimpleDateFormat("k:mm").format(new Date());
        if (this.rainbowTimeStamps.getValue().booleanValue()) {
            String timeString = "<" + date + ">" + (this.space.getValue() != false ? " " : "");
            StringBuilder builder = new StringBuilder(timeString);
            builder.insert(0, "\u00a7+");
            return builder.toString();
        }
        return (this.bracket.getValue() == TextUtil.Color.NONE ? "" : TextUtil.coloredString("<", this.bracket.getValue())) + TextUtil.coloredString(date, this.timeStamps.getValue()) + (this.bracket.getValue() == TextUtil.Color.NONE ? "" : TextUtil.coloredString(">", this.bracket.getValue())) + (this.space.getValue() != false ? " " : "") + "\u00a7r";
    }
}