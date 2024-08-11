package fat.vonware.features.modules.render;


import fat.vonware.event.events.Render3DEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PlayerAnimation extends Module {
    public PlayerAnimation() {
        super("PlayerTweaks", "Stiffy people", Module.Category.RENDER, true, false, false);
    }
    public Setting<Boolean> crouch = register(new Setting<>("Crouch", true));
    public Setting<Boolean> still = register(new Setting<>("Still", true));

    @Override
    public void onUpdate() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if(crouch.getValue() && player != mc.player){
                player.setSneaking(true);
            }
        }
    }
    @Override
    public void onDisable() {
        for (EntityPlayer player : mc.world.playerEntities) {
            if(crouch.getValue() && player != mc.player){
                player.setSneaking(true);
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent e) {
        for (EntityPlayer player : mc.world.playerEntities) {
            if (still.getValue() && player != mc.player) {
                player.limbSwing = 0;
                player.limbSwingAmount = 0;
            }
        }
    }

}