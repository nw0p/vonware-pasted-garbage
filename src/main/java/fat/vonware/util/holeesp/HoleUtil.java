package fat.vonware.util.holeesp;

import fat.vonware.util.BlockUtil;
import fat.vonware.util.holeesp.Enums.SafetyEnum;
import fat.vonware.util.Util;
import net.minecraft.block.Block;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;

import java.util.ArrayList;
import java.util.List;

public class HoleUtil implements Util
{
    public static BlockPos[] HOLE_OFFSETS;
    public static BlockPos[] AROUND_OFFSETS;

    public static boolean isHole(BlockPos pos) {
        return isMixedHole(pos) || isBedrockHole(pos) || isObbyHole(pos);
    }

    public static boolean isInHole(EntityPlayer player) {
        return isHole(player.getPosition());
    }

    public static boolean isWebHole(BlockPos pos) {
        if (!BlockUtil.isAir(pos.up()) || !BlockUtil.isAir(pos.up().up())) {
            return false;
        }
        for (BlockPos off : HoleUtil.HOLE_OFFSETS) {
            if (!isWeb(pos.add((Vec3i)off))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isWeb(BlockPos pos) {
        return HoleUtil.mc.world.getBlockState(pos).getBlock() == Blocks.WEB || HoleUtil.mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN || HoleUtil.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK;
    }

    public static boolean isObbyHole(BlockPos pos) {
        assert HoleUtil.mc.world != null;
        if (!BlockUtil.isAir(pos.up()) || !BlockUtil.isAir(pos.up(2))) {
            return false;
        }
        for (BlockPos off : HoleUtil.HOLE_OFFSETS) {
            if (!BlockUtil.isObby(pos.add((Vec3i)off))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isMixedHole(BlockPos pos) {
        assert HoleUtil.mc.world != null;
        if (isBedrockHole(pos)) {
            return false;
        }
        if (!BlockUtil.isAir(pos.up()) || !BlockUtil.isAir(pos.up(2))) {
            return false;
        }
        for (BlockPos off : HoleUtil.HOLE_OFFSETS) {
            if (!BlockUtil.isSafe(pos.add((Vec3i)off))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isBedrockHole(BlockPos pos) {
        if (!BlockUtil.isAir(pos.up()) || !BlockUtil.isAir(pos.up(2))) {
            return false;
        }
        for (BlockPos off : HoleUtil.HOLE_OFFSETS) {
            if (!BlockUtil.isBedrock(pos.add((Vec3i)off))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isTrapHole(BlockPos pos) {
        if (isHole(pos) || !BlockUtil.isAir(pos.up()) || BlockUtil.isAir(pos.up(2)) || !BlockUtil.isSafe(pos.down())) {
            return false;
        }
        if (!BlockUtil.isSafe(pos.down())) {
            return false;
        }
        for (BlockPos off : HoleUtil.AROUND_OFFSETS) {
            if (!BlockUtil.isSafe(pos.up().add((Vec3i)off))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isTerrainHole(BlockPos pos) {
        if (isHole(pos)) {
            return false;
        }
        if (!BlockUtil.isAir(pos.up()) || !BlockUtil.isAir(pos.up(2))) {
            return false;
        }
        for (BlockPos off : HoleUtil.HOLE_OFFSETS) {
            Block block = HoleUtil.mc.world.getBlockState(pos.add((Vec3i)off)).getBlock();
            if (block != Blocks.STONE && block != Blocks.DIRT && block != Blocks.NETHERRACK && block != Blocks.COBBLESTONE && block != Blocks.GRAVEL && block != Blocks.END_STONE && !BlockUtil.isSafe(pos.add((Vec3i)off))) {
                return false;
            }
        }
        return true;
    }

    public static boolean isDoubleHole(BlockPos pos) {
        Hole hole = getDoubleHole(pos);
        return hole != null && BlockUtil.isAir(hole.getFirst().up()) && BlockUtil.isAir(hole.getSecond().up()) && BlockUtil.isAir(hole.getFirst().up(2)) && BlockUtil.isAir(hole.getFirst().up(2));
    }

    public static Hole getDoubleHole(BlockPos pos) {
        if (isBedrock(pos, 0, 1)) {
            return new Hole(pos, pos.add(0, 0, 1), SafetyEnum.BEDROCK);
        }
        if (isBedrock(pos, 1, 0)) {
            return new Hole(pos, pos.add(1, 0, 0), SafetyEnum.BEDROCK);
        }
        if (isObby(pos, 0, 1)) {
            return new Hole(pos, pos.add(0, 0, 1), SafetyEnum.OBBY);
        }
        if (isObby(pos, 1, 0)) {
            return new Hole(pos, pos.add(1, 0, 0), SafetyEnum.OBBY);
        }
        if (isMixed(pos, 0, 1)) {
            return new Hole(pos, pos.add(0, 0, 1), SafetyEnum.MIXED);
        }
        if (isMixed(pos, 1, 0)) {
            return new Hole(pos, pos.add(1, 0, 0), SafetyEnum.MIXED);
        }
        return null;
    }

    public static Vec3d getCenter(Hole hole) {
        double x = hole.getFirst().getX() + 0.5;
        double z = hole.getFirst().getZ() + 0.5;
        if (hole.getSecond() != null) {
            x = (x + hole.getSecond().getX() + 0.5) / 2.0;
            z = (z + hole.getSecond().getZ() + 0.5) / 2.0;
        }
        return new Vec3d(x, (double)hole.getFirst().getY(), z);
    }

    public static boolean isObby(BlockPos pos, int offX, int offZ) {
        return BlockUtil.isAir(pos) && BlockUtil.isAir(pos.add(offX, 0, offZ)) && BlockUtil.isObby(pos.add(0, -1, 0)) && BlockUtil.isObby(pos.add(offX, -1, offZ)) && BlockUtil.isObby(pos.add(offX * 2, 0, offZ * 2)) && BlockUtil.isObby(pos.add(-offX, 0, -offZ)) && BlockUtil.isObby(pos.add(offZ, 0, offX)) && BlockUtil.isObby(pos.add(-offZ, 0, -offX)) && BlockUtil.isObby(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && BlockUtil.isObby(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
    }

    public static boolean isMixed(BlockPos pos, int offX, int offZ) {
        return BlockUtil.isAir(pos) && BlockUtil.isAir(pos.add(offX, 0, offZ)) && BlockUtil.isSafe(pos.add(0, -1, 0)) && BlockUtil.isSafe(pos.add(offX, -1, offZ)) && BlockUtil.isSafe(pos.add(offX * 2, 0, offZ * 2)) && BlockUtil.isSafe(pos.add(-offX, 0, -offZ)) && BlockUtil.isSafe(pos.add(offZ, 0, offX)) && BlockUtil.isSafe(pos.add(-offZ, 0, -offX)) && BlockUtil.isSafe(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && BlockUtil.isSafe(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
    }

    public static boolean isBedrock(BlockPos pos, int offX, int offZ) {
        return BlockUtil.isAir(pos) && BlockUtil.isAir(pos.add(offX, 0, offZ)) && BlockUtil.isBedrock(pos.add(0, -1, 0)) && BlockUtil.isBedrock(pos.add(offX, -1, offZ)) && BlockUtil.isBedrock(pos.add(offX * 2, 0, offZ * 2)) && BlockUtil.isBedrock(pos.add(-offX, 0, -offZ)) && BlockUtil.isBedrock(pos.add(offZ, 0, offX)) && BlockUtil.isBedrock(pos.add(-offZ, 0, -offX)) && BlockUtil.isBedrock(pos.add(offX, 0, offZ).add(offZ, 0, offX)) && BlockUtil.isBedrock(pos.add(offX, 0, offZ).add(-offZ, 0, -offX));
    }

    public static Hole getHole(BlockPos pos, boolean doubles) {
        return getHole(pos, doubles, false);
    }

    public static Hole getHole(BlockPos pos, boolean doubles, boolean terrain) {
        if (!BlockUtil.isAir(pos)) {
            return null;
        }
        Hole hole = null;
        if (isBedrockHole(pos)) {
            hole = new Hole(pos, SafetyEnum.BEDROCK);
        }
        else if (isObbyHole(pos)) {
            hole = new Hole(pos, SafetyEnum.OBBY);
        }
        else if (isMixedHole(pos)) {
            hole = new Hole(pos, SafetyEnum.MIXED);
        }
        else if (terrain && isTerrainHole(pos)) {
            hole = new Hole(pos, SafetyEnum.TERRAIN);
        }
        if (doubles && isDoubleHole(pos)) {
            hole = getDoubleHole(pos);
        }
        return hole;
    }

    public static List<Hole> getHoles(float range, boolean doubles, boolean webs) {
        return getHoles((Entity)HoleUtil.mc.player, range, doubles, webs, false, false);
    }

    public static List<Hole> getHoles(float range, boolean doubles, boolean webs, boolean trap, boolean terrain) {
        return getHoles((Entity)HoleUtil.mc.player, range, doubles, webs, trap, terrain);
    }

    public static List<Hole> getHoles(Entity player, float range, boolean doubles, boolean webs, boolean trap, boolean terrain) {
        List<Hole> holes = new ArrayList<Hole>();
        for (BlockPos pos : BlockUtil.getSphere(player, range, false)) {
            if (!BlockUtil.isAir(pos)) {
                continue;
            }
            if (webs && isWebHole(pos)) {
                holes.add(new Hole(pos, SafetyEnum.MIXED));
            } else if (isBedrockHole(pos)) {
                holes.add(new Hole(pos, SafetyEnum.BEDROCK));
            } else if (isObbyHole(pos)) {
                holes.add(new Hole(pos, SafetyEnum.OBBY));
            } else if (isMixedHole(pos)) {
                holes.add(new Hole(pos, SafetyEnum.MIXED));
            } else if (trap && isTrapHole(pos)) {
                holes.add(new Hole(pos, SafetyEnum.TRAPPED));
            } else if (terrain && isTerrainHole(pos)) {
                holes.add(new Hole(pos, SafetyEnum.TERRAIN));
            } else {
                if (!doubles || !isDoubleHole(pos)) {
                    continue;
                }
                holes.add(getDoubleHole(pos));
            }
        }
        return holes;
    }

    static {
        HOLE_OFFSETS = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1), new BlockPos(0, -1, 0) };
        AROUND_OFFSETS = new BlockPos[] { new BlockPos(1, 0, 0), new BlockPos(-1, 0, 0), new BlockPos(0, 0, 1), new BlockPos(0, 0, -1) };
    }
}