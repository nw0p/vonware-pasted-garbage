package fat.vonware.event.events;
import fat.vonware.event.EventStage;
import net.minecraft.entity.*;
import net.minecraft.block.*;
import net.minecraft.util.math.*;

public class CollisionEvent extends EventStage
{
    private final Entity entity;
    private final BlockPos pos;
    private final Block block;
    private AxisAlignedBB bb;

    public CollisionEvent(final BlockPos pos, final AxisAlignedBB bb, final Entity entity, final Block block) {
        this.pos = pos;
        this.bb = bb;
        this.entity = entity;
        this.block = block;
    }

    public AxisAlignedBB getBB() {
        return this.bb;
    }

    public void setBB(final AxisAlignedBB bb) {
        this.bb = bb;
    }

    public Entity getEntity() {
        return this.entity;
    }

    public BlockPos getPos() {
        return this.pos;
    }

    public Block getBlock() {
        return this.block;
    }
}
