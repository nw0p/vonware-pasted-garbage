package fat.vonware.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.BlockPos;

public class PositionUtil implements Util
{
    public static BlockPos getPosition() {
        return getPosition((Entity)PositionUtil.mc.player);
    }

    public static BlockPos getPosition(final Entity entity) {
        return getPosition(entity, 0.0);
    }

    public static BlockPos getPosition(final Entity entity, final double yOffset) {
        double y = entity.posY + yOffset;
        if (entity.posY - Math.floor(entity.posY) > 0.5) {
            y = Math.ceil(entity.posY);
        }
        return new BlockPos(entity.posX, y, entity.posZ);
    }
}
