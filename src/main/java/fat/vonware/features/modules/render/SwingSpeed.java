package fat.vonware.features.modules.render;

import fat.vonware.event.events.Packet;
import fat.vonware.features.setting.Setting;

import fat.vonware.features.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SwingSpeed extends Module {
    public static Setting<AnimationVersion> AnimationsVersion;
    public static Setting<Boolean> playersDisableAnimations;
    public static Setting<Boolean> changeMainhand;
    public static Setting<Float> mainhand;
    public static Setting<Boolean> changeOffhand;
    public static Setting<Float> offhand;
    public static Setting<Integer> changeSwing;
    public static Setting<Integer> swingDelay;
    public static Setting<Hand> hand;

    public SwingSpeed() {
        super("SwingModify", "Allows you to change animations in your hand", Module.Category.RENDER, true, false, false);
        AnimationsVersion = this.register(new Setting<AnimationVersion>("Version", AnimationVersion.OneDotEight));
        playersDisableAnimations = this.register(new Setting<Boolean>("Disable Animations", false));
        changeMainhand = this.register(new Setting<Boolean>("Change Mainhand", true));
        mainhand = this.register(new Setting<Float>("Mainhand", Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(4.7509747f) ^ 0x7F1807FC)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(1.63819E38f) ^ 0x7EF67CC9)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(30.789412f) ^ 0x7E7650B7))));
        changeOffhand = this.register(new Setting<Boolean>("Change Offhand", true));
        offhand = this.register(new Setting<Float>("Offhand", Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(15.8065405f) ^ 0x7EFCE797)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(3.3688825E38f) ^ 0x7F7D7251)), Float.valueOf(Float.intBitsToFloat(Float.floatToIntBits(7.3325067f) ^ 0x7F6AA3E5))));
        changeSwing = this.register(new Setting<Integer>("Swing Speed", 6, 0, 20));
        swingDelay = this.register(new Setting<Integer>("Swing Delay", 6, 1, 20));
        hand = this.register(new Setting<Hand>("Hand", Hand.OFFHAND));
    }

    @Override
    public void onUpdate() {
        if (playersDisableAnimations.getValue().booleanValue()) {
            for (EntityPlayer player : SwingSpeed.mc.world.playerEntities) {
                player.limbSwing = Float.intBitsToFloat(Float.floatToIntBits(1.8755627E38f) ^ 0x7F0D1A06);
                player.limbSwingAmount = Float.intBitsToFloat(Float.floatToIntBits(6.103741E37f) ^ 0x7E37AD83);
                player.prevLimbSwingAmount = Float.intBitsToFloat(Float.floatToIntBits(4.8253957E37f) ^ 0x7E11357F);
            }
        }
        if (changeMainhand.getValue().booleanValue() && SwingSpeed.mc.entityRenderer.itemRenderer.equippedProgressMainHand != mainhand.getValue().floatValue()) {
            SwingSpeed.mc.entityRenderer.itemRenderer.equippedProgressMainHand = mainhand.getValue().floatValue();
            SwingSpeed.mc.entityRenderer.itemRenderer.itemStackMainHand = SwingSpeed.mc.player.getHeldItemMainhand();
        }
        if (changeOffhand.getValue().booleanValue() && SwingSpeed.mc.entityRenderer.itemRenderer.equippedProgressOffHand != offhand.getValue().floatValue()) {
            SwingSpeed.mc.entityRenderer.itemRenderer.equippedProgressOffHand = offhand.getValue().floatValue();
            SwingSpeed.mc.entityRenderer.itemRenderer.itemStackOffHand = SwingSpeed.mc.player.getHeldItemOffhand();
        }
        if (AnimationsVersion.getValue() == AnimationVersion.OneDotEight && (double) SwingSpeed.mc.entityRenderer.itemRenderer.prevEquippedProgressMainHand >= 0.9) {
            SwingSpeed.mc.entityRenderer.itemRenderer.equippedProgressMainHand = 1.0f;
            SwingSpeed.mc.entityRenderer.itemRenderer.itemStackMainHand = SwingSpeed.mc.player.getHeldItemMainhand();
        }
        if (mc.world == null)
            return;
        if (hand.getValue().equals(Hand.OFFHAND)) {
            mc.player.swingingHand = EnumHand.OFF_HAND;
        }
        if (hand.getValue().equals(Hand.MAINHAND)) {
            mc.player.swingingHand = EnumHand.MAIN_HAND;
        }

    }

    @SubscribeEvent
    public void onPacket(final Packet event) {
        if (nullCheck() || event.getType() == Packet.Type.INCOMING) {
            return;
        }
        if (event.getPacket() instanceof CPacketAnimation) {
            event.setCanceled(true);
        }
    }



    public static enum AnimationVersion {
        OneDotEight,
        OneDotTwelve;

    }
    public static enum Hand {
        OFFHAND,
        MAINHAND,
        PACKETSWING
    }
}