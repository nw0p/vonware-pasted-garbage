package fat.vonware.manager;

import fat.vonware.features.Feature;
import fat.vonware.mixin.mixins.accessor.ITimer;
import fat.vonware.util.Timer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class TimerManager
extends Feature {
    float timer = 1.0f;
    boolean flagged = false;
    Timer flagTimer = new Timer();

    public void load() {
        MinecraftForge.EVENT_BUS.register((Object)this);
    }

    @SubscribeEvent
    public void onUpdate(TickEvent.ClientTickEvent e) {
        if (TimerManager.fullNullCheck()) {
            return;
        }
        if (this.flagged && this.flagTimer.passedMs(1000L)) {
            this.flagged = false;
        }

        /*

if (TimerManager.mc.timer.field_194149_e != 50.0f) {
            this.flagged = true;
            this.flagTimer.reset();
        } */
    }

    public void unload() {
        MinecraftForge.EVENT_BUS.unregister((Object)this);
        this.timer = 1.0f;
        ((ITimer)TimerManager.mc.timer).setTickLength(50.0f);
    }

    public void set(float timer) {
        if (timer > 0.0f) {
            ((ITimer)TimerManager.mc.timer).setTickLength(50.0f / timer);
        }
    }

    public float getTimer() {
        return this.timer;
    }

    public boolean isFlagged() {
        return this.flagged;
    }

    @Override
    public void reset() {
        this.timer = 1.0f;
        ((ITimer)TimerManager.mc.timer).setTickLength(50.0f);
    }
}

