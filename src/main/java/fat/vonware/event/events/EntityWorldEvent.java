package fat.vonware.event.events;
import fat.vonware.event.EventStage;
import net.minecraft.entity.*;

public class EntityWorldEvent extends EventStage
{
    private final Entity entity;

    public Entity getEntity() {
        return this.entity;
    }

    public EntityWorldEvent(final Entity entity) {
        this.entity = entity;
    }

    public static class Add extends EntityWorldEvent
    {
        public Add(final Entity entity) {
            super(entity);
        }
    }

    public static class Remove extends EntityWorldEvent
    {
        public Remove(final Entity entity) {
            super(entity);
        }
    }
}
