package fat.vonware.event.events;

import fat.vonware.manager.EventManager;
import net.minecraft.entity.EntityLivingBase;

public class SwingAnimationEvent extends EventManager {
    private final EntityLivingBase entity;
    private int speed;

    public SwingAnimationEvent(EntityLivingBase entity, Integer speed) {
        this.entity = entity;
        this.speed = speed;
    }

    public int getSpeed() {
        return this.speed;
    }

    public void setSpeed(int speed) {
        this.speed = speed;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }
}