package fat.vonware.features.modules.render;

import fat.vonware.features.modules.*;
import fat.vonware.features.setting.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import net.minecraft.entity.player.*;
import net.minecraft.entity.effect.*;
import net.minecraft.world.*;
import net.minecraft.entity.*;
import net.minecraft.init.*;

public class KillEffects extends Module
{
    public Setting<Boolean> lightning;
    public Setting<Integer> amount;
    public Setting<Boolean> sound;
    public Setting<Integer> numberSound;
    ArrayList<EntityPlayer> playersDead;

    public KillEffects() {
        super("KillEffects", "Effects when you kill.", Module.Category.RENDER, true, false, false);
        this.lightning = (Setting<Boolean>)this.register(new Setting("Lightning", true));
        this.amount = (Setting<Integer>)this.register(new Setting("Number Lightning", 1, 1, 10));
        this.sound = (Setting<Boolean>)this.register(new Setting("Sound", true));
        this.numberSound = (Setting<Integer>)this.register(new Setting("Number Sound", 1, 1, 10));
        this.playersDead = new ArrayList<EntityPlayer>();
    }

    public void onEnable() {
        this.playersDead.clear();
    }

    public void onUpdate() {
        if (KillEffects.mc.world == null) {
            this.playersDead.clear();
            return;
        }
        AtomicInteger i = new AtomicInteger();
        AtomicInteger j = new AtomicInteger();
        KillEffects.mc.world.playerEntities.forEach(entity -> {
            if (this.playersDead.contains(entity)) {
                if (entity.getHealth() > 0.0f) {
                    this.playersDead.remove(entity);
                }
            }
            else if (entity.getHealth() == 0.0f) {
                if (this.lightning.getValue()) {
                    for (i.set(0); i.get() < this.amount.getValue(); i.incrementAndGet()) {
                        KillEffects.mc.world.spawnEntity((Entity)new EntityLightningBolt((World)KillEffects.mc.world, entity.posX, entity.posY, entity.posZ, true));
                    }
                }
                if (this.sound.getValue()) {
                    for (j.set(0); j.get() < this.numberSound.getValue(); j.incrementAndGet()) {
                        KillEffects.mc.player.playSound(SoundEvents.ENTITY_LIGHTNING_THUNDER, 0.5f, 1.0f);
                    }
                }
                this.playersDead.add(entity);
            }
        });
    }
}
