package fat.vonware.features.modules.misc;

import fat.vonware.event.events.ConnectionEvent;
import fat.vonware.event.events.EntityWorldEvent;
import fat.vonware.features.modules.*;
import java.util.*;
import fat.vonware.features.setting.*;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.*;
import com.mojang.realmsclient.gui.*;
import fat.vonware.features.command.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PopCounter extends Module
{

    public static HashMap<String, Integer> TotemPopContainer;
    private static PopCounter INSTANCE;
    public Setting<Boolean> clearOnLogout = this.register(new Setting("ClearOnLogout", true));
    public Setting<Boolean> clearOnVisRange = this.register(new Setting("clearOnVisRange", true));
    public Setting<PopNotifier> popNotifier;

    public PopCounter() {
        super("PopCounter", "Counts other players totem pops.", Category.MISC, true, false, false);
        this.popNotifier = (Setting<PopNotifier>)this.register(new Setting("PopNotifier", PopNotifier.VONWARE));
        this.setInstance();
    }

    public static PopCounter getInstance() {
        if (PopCounter.INSTANCE == null) {
            PopCounter.INSTANCE = new PopCounter();
        }
        return PopCounter.INSTANCE;
    }

    private void setInstance() {
        PopCounter.INSTANCE = this;
    }

    @Override
    public void onEnable() {
        PopCounter.TotemPopContainer.clear();
    }
    @SubscribeEvent
    public void ClearOnLog(ConnectionEvent.Leave event) {
        if (clearOnLogout.getValue()) {
            TotemPopContainer.clear();
        }
    }
    @SubscribeEvent
    public void ClearOnVisualRange(EntityWorldEvent.Remove event) {
        if (clearOnVisRange.getValue() && event.getEntity() instanceof EntityPlayer) {
            TotemPopContainer.put(((EntityPlayer )event.getEntity()).getName(),  0);
            // Debug  Command.sendSilentMessage( "nigga named left" + event.getEntity().getName(), event.getEntity().getEntityId());

        }
    }


    public String death1(final EntityPlayer player) {
        final int l_Count = PopCounter.TotemPopContainer.get(player.getName());
        PopCounter.TotemPopContainer.remove(player.getName());
        if (l_Count == 1) {
            if (!this.isEnabled()) {
                return ChatFormatting.GRAY + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " Totem!";
            }
            switch (this.popNotifier.getValue()) {
                case FUTURE: {
                    final String text = ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.getName() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " totem.";
                    return text;
                }
                case PHOBOS: {
                    final String text = ChatFormatting.GOLD + player.getName() + ChatFormatting.RED + " died after popping " + ChatFormatting.GOLD + l_Count + ChatFormatting.RED + " totem.";
                    return text;
                }
                case DOTGOD: {
                    final String text = ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.LIGHT_PURPLE + " time!";
                    return text;
                }
                case VONWARE: {
                    final String text = ChatFormatting.DARK_GRAY + "[" + ChatFormatting.DARK_AQUA + "PopCounter" + ChatFormatting.DARK_GRAY + "] " + ChatFormatting.GRAY + player.getName() + " died after popping " + ChatFormatting.GRAY + l_Count + ChatFormatting.GRAY + " Totem!";
                    return text;
                }
            }
        }
        else {
            if (!this.isEnabled()) {
                return ChatFormatting.GRAY + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " Totems!";
            }
            switch (this.popNotifier.getValue()) {
                case FUTURE: {
                    final String text = ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.getName() + ChatFormatting.GRAY + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " totems.";
                    return text;
                }
                case PHOBOS: {
                    final String text = ChatFormatting.GOLD + player.getName() + ChatFormatting.RED + " died after popping " + ChatFormatting.GOLD + l_Count + ChatFormatting.RED + " totems.";
                    return text;
                }
                case DOTGOD: {
                    final String text = ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.getName() + " died after popping " + ChatFormatting.GREEN + l_Count + ChatFormatting.LIGHT_PURPLE + " times!";
                    return text;
                }
                case VONWARE: {
                    final String text = ChatFormatting.DARK_GRAY + "[" + ChatFormatting.DARK_AQUA + "PopCounter" + ChatFormatting.DARK_GRAY + "] " + ChatFormatting.GRAY + player.getName() + " died after popping " + ChatFormatting.GRAY + l_Count + ChatFormatting.GRAY + " Totems!";
                    return text;
                }
            }
        }
        return null;
    }

    public void onDeath(final EntityPlayer player) {
        if (fullNullCheck()) {
            return;
        }
        if (getInstance().isDisabled()) {
            return;
        }
        if (PopCounter.mc.player.equals((Object)player)) {
            return;
        }
        if (PopCounter.TotemPopContainer.containsKey(player.getName())) {
            Command.sendSilentMessage(this.death1(player), player.getEntityId());
        }
    }

    public String pop(final EntityPlayer player) {
        int l_Count = 1;
        if (PopCounter.TotemPopContainer.containsKey(player.getName())) {
            l_Count = PopCounter.TotemPopContainer.get(player.getName());
            PopCounter.TotemPopContainer.put(player.getName(), ++l_Count);
        }
        else {
            PopCounter.TotemPopContainer.put(player.getName(), l_Count);
        }
        if (l_Count == 1) {
            if (!this.isEnabled()) {
                return ChatFormatting.GRAY + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " Totem.";
            }
            switch (this.popNotifier.getValue()) {
                case FUTURE: {
                    final String text = ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.getName() + ChatFormatting.GRAY + " just popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " totem.";
                    return text;
                }
                case PHOBOS: {
                    final String text = ChatFormatting.GOLD + player.getName() + ChatFormatting.RED + " popped " + ChatFormatting.GOLD + l_Count + ChatFormatting.RED + " totem.";
                    return text;
                }
                case DOTGOD: {
                    final String text = ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.getName() + " has popped " + ChatFormatting.RED + l_Count + ChatFormatting.LIGHT_PURPLE + " time in total!";
                    return text;
                }
                case VONWARE: {
                    final String text = ChatFormatting.DARK_GRAY + "[" + ChatFormatting.DARK_AQUA + "PopCounter" + ChatFormatting.DARK_GRAY + "] " + ChatFormatting.GRAY + player.getName() + " popped " + ChatFormatting.GRAY + l_Count + ChatFormatting.GRAY + " Totem!";
                    return text;
                }
            }
        }
        else {
            if (!this.isEnabled()) {
                return ChatFormatting.GRAY + player.getName() + " popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " Totems.";
            }
            switch (this.popNotifier.getValue()) {
                case FUTURE: {
                    final String text = ChatFormatting.RED + "[Future] " + ChatFormatting.GREEN + player.getName() + ChatFormatting.GRAY + " just popped " + ChatFormatting.GREEN + l_Count + ChatFormatting.GRAY + " totems.";
                    return text;
                }
                case PHOBOS: {
                    final String text = ChatFormatting.GOLD + player.getName() + ChatFormatting.RED + " popped " + ChatFormatting.GOLD + l_Count + ChatFormatting.RED + " totems.";
                    return text;
                }
                case DOTGOD: {
                    final String text = ChatFormatting.DARK_PURPLE + "[" + ChatFormatting.LIGHT_PURPLE + "DotGod.CC" + ChatFormatting.DARK_PURPLE + "] " + ChatFormatting.LIGHT_PURPLE + player.getName() + " has popped " + ChatFormatting.RED + l_Count + ChatFormatting.LIGHT_PURPLE + " times in total!";
                    return text;
                }
                case VONWARE: {
                    final String text = ChatFormatting.DARK_GRAY + "[" + ChatFormatting.DARK_AQUA + "PopCounter" + ChatFormatting.DARK_GRAY + "] " + ChatFormatting.GRAY + player.getName() + " popped " + ChatFormatting.GRAY + l_Count + ChatFormatting.GRAY + " Totems!";
                    return text;
                }
            }
        }
        return "";
    }

    public void onTotemPop(final EntityPlayer player) {
        if (fullNullCheck()) {
            return;
        }
        if (getInstance().isDisabled()) {
            return;
        }
        if (PopCounter.mc.player.equals((Object)player)) {
            return;
        }
        Command.sendSilentMessage(pop(player), player.getEntityId());
    }

    static {
        PopCounter.TotemPopContainer = new HashMap<String, Integer>();
        PopCounter.INSTANCE = new PopCounter();
    }

    public enum PopNotifier
    {
        PHOBOS,
        FUTURE,
        DOTGOD,
        VONWARE;
    }
}
