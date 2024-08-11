package fat.vonware.features.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import fat.vonware.Vonware;
import fat.vonware.features.command.Command;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.Util;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;

import java.util.ArrayList;
import java.util.List;

public class VisualRange extends Module {
    public Setting<Boolean> VisualRangeSound = this.register(new Setting<>("Sound", true));
    public Setting<Boolean> coords = this.register(new Setting<>("Coords", false));
    public Setting<Boolean> leaving = this.register(new Setting<>("Leaving", true));


    private List<EntityPlayer> knownPlayers = new ArrayList<>();

    public VisualRange() {
        super("VisualRange", "Visual range", Category.MISC, true, false, false);
    }

    public void onEnable() {
        List <String> people = new ArrayList <>();
        this.knownPlayers = new ArrayList<>();
    }

    public void onUpdate() {
        ArrayList<EntityPlayer> tickPlayerList = new ArrayList<>(VisualRange.mc.world.playerEntities);
        if (tickPlayerList.size() > 0) {
            for (EntityPlayer player : tickPlayerList) {
                if (player.getName().equals(VisualRange.mc.player.getName()) || this.knownPlayers.contains(player))
                    continue;
                this.knownPlayers.add(player);
                if (Vonware.friendManager.isFriend(player)) {
                    Command.sendSilentMessage("\u00a73" + player.getName() + "\u00a7b" + ChatFormatting.GRAY + " Has entered your Visual Range" + (this.coords.getValue() ? " at " + (int) player.posX + ", " + (int) player.posY + ", " + (int) player.posZ + "!" : ""));
                } else {
                    Command.sendSilentMessage("\u00a73" + player.getName() + "\u00a7b" + ChatFormatting.GRAY + " Has entered your Visual Range" + (this.coords.getValue() ? " at " + (int) player.posX + ", " + (int) player.posY + ", " + (int) player.posZ + "!" : ""));
                }
                if (this.VisualRangeSound.getValue()) {
                    VisualRange.mc.player.playSound(SoundEvents.BLOCK_NOTE_BELL, 1.0f, 1.0f);
                }
                return;
            }
        }
        if (this.knownPlayers.size() > 0) {
            for (EntityPlayer player : this.knownPlayers) {
                if (tickPlayerList.contains(player)) continue;
                this.knownPlayers.remove(player);
                if (this.leaving.getValue()) {
                    if (Vonware.friendManager.isFriend(player)) {
                        Command.sendSilentMessage("\u00a73" + player.getName() + "\u00a7r" + ChatFormatting.GRAY + " Has left your Visual Range" + (this.coords.getValue() ? " at " + (int) player.posX + ", " + (int) player.posY + ", " + (int) player.posZ + "!" : ""));
                    } else {
                        Command.sendSilentMessage("\u00a73" + player.getName() + "\u00a7r" + ChatFormatting.GRAY + " Has left your Visual Range" + (this.coords.getValue() ? " at " + (int) player.posX + ", " + (int) player.posY + ", " + (int) player.posZ + "!" : ""));
                    }
                }
                return;
            }
        }


        if ((((Util.mc.world == null) ? 1 : 0) | ((Util.mc.player == null) ? 1 : 0)) != 0)
            return;
        List<String> peoplenew = new ArrayList<>();
        List<EntityPlayer> playerEntities = Util.mc.world.playerEntities;
        for (Entity e : playerEntities) {
            if (e.getName().equals(Util.mc.player.getName()))
                continue;
            peoplenew.add(e.getName());
        }
    }
}
