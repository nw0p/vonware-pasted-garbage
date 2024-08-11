package fat.vonware.util;

import fat.vonware.Vonware;
import fat.vonware.features.command.Command;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.item.EntityXPOrb;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

public class BlockUtil
        implements Util {
    public static final List<Block> blackList = Arrays.asList(Blocks.ENDER_CHEST, Blocks.CHEST, Blocks.TRAPPED_CHEST, Blocks.CRAFTING_TABLE, Blocks.ANVIL, Blocks.BREWING_STAND, Blocks.HOPPER, Blocks.DROPPER, Blocks.DISPENSER, Blocks.TRAPDOOR, Blocks.ENCHANTING_TABLE);
    public static final List<Block> shulkerList = Arrays.asList(Blocks.WHITE_SHULKER_BOX, Blocks.ORANGE_SHULKER_BOX, Blocks.MAGENTA_SHULKER_BOX, Blocks.LIGHT_BLUE_SHULKER_BOX, Blocks.YELLOW_SHULKER_BOX, Blocks.LIME_SHULKER_BOX, Blocks.PINK_SHULKER_BOX, Blocks.GRAY_SHULKER_BOX, Blocks.SILVER_SHULKER_BOX, Blocks.CYAN_SHULKER_BOX, Blocks.PURPLE_SHULKER_BOX, Blocks.BLUE_SHULKER_BOX, Blocks.BROWN_SHULKER_BOX, Blocks.GREEN_SHULKER_BOX, Blocks.RED_SHULKER_BOX, Blocks.BLACK_SHULKER_BOX);
    public static final List<Block> unSafeBlocks = Arrays.asList(Blocks.OBSIDIAN, Blocks.BEDROCK, Blocks.ENDER_CHEST, Blocks.ANVIL);
    public static List<Block> unSolidBlocks = Arrays.asList(Blocks.FLOWING_LAVA, Blocks.FLOWER_POT, Blocks.SNOW, Blocks.CARPET, Blocks.END_ROD, Blocks.SKULL, Blocks.FLOWER_POT, Blocks.TRIPWIRE, Blocks.TRIPWIRE_HOOK, Blocks.WOODEN_BUTTON, Blocks.LEVER, Blocks.STONE_BUTTON, Blocks.LADDER, Blocks.UNPOWERED_COMPARATOR, Blocks.POWERED_COMPARATOR, Blocks.UNPOWERED_REPEATER, Blocks.POWERED_REPEATER, Blocks.UNLIT_REDSTONE_TORCH, Blocks.REDSTONE_TORCH, Blocks.REDSTONE_WIRE, Blocks.AIR, Blocks.PORTAL, Blocks.END_PORTAL, Blocks.WATER, Blocks.FLOWING_WATER, Blocks.LAVA, Blocks.FLOWING_LAVA, Blocks.SAPLING, Blocks.RED_FLOWER, Blocks.YELLOW_FLOWER, Blocks.BROWN_MUSHROOM, Blocks.RED_MUSHROOM, Blocks.WHEAT, Blocks.CARROTS, Blocks.POTATOES, Blocks.BEETROOTS, Blocks.REEDS, Blocks.PUMPKIN_STEM, Blocks.MELON_STEM, Blocks.WATERLILY, Blocks.NETHER_WART, Blocks.COCOA, Blocks.CHORUS_FLOWER, Blocks.CHORUS_PLANT, Blocks.TALLGRASS, Blocks.DEADBUSH, Blocks.VINE, Blocks.FIRE, Blocks.RAIL, Blocks.ACTIVATOR_RAIL, Blocks.DETECTOR_RAIL, Blocks.GOLDEN_RAIL, Blocks.TORCH);

    public static List<BlockPos> getCircle(final BlockPos pos, final float r) {
        final ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        final int cx = pos.getX();
        final int cy = pos.getY();
        final int cz = pos.getZ();
        for (int x = cx - (int) r; x <= cx + r; ++x) {
            for (int z = cz - (int) r; z <= cz + r; ++z) {
                final double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z);
                if (dist < r * r) {
                    final BlockPos position = new BlockPos(x, cy, z);
                    circleblocks.add(position);
                }
            }
        }
        return circleblocks;
    }
    public static EnumFacing getFacing(BlockPos pos) {
        for (EnumFacing facing : EnumFacing.values()) {
            RayTraceResult rayTraceResult = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double)mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double)pos.getX() + 0.5 + (double)facing.getDirectionVec().getX() * 1.0 / 2.0, (double)pos.getY() + 0.5 + (double)facing.getDirectionVec().getY() * 1.0 / 2.0, (double)pos.getZ() + 0.5 + (double)facing.getDirectionVec().getZ() * 1.0 / 2.0), false, true, false);
            if (rayTraceResult != null && (rayTraceResult.typeOfHit != RayTraceResult.Type.BLOCK || !rayTraceResult.getBlockPos().equals((Object)pos))) continue;
            return facing;
        }
        if ((double)pos.getY() > mc.player.posY + (double)mc.player.getEyeHeight()) {
            return EnumFacing.DOWN;
        }
        return EnumFacing.UP;
    }
    public static boolean canReplace(BlockPos pos) {
        return getState(pos).getMaterial().isReplaceable();
    }
    public static boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = BlockUtil.mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)BlockUtil.mc.world, pos) != -1.0f;
    }


    public static boolean isBedrock(final BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).getBlock() == Blocks.BEDROCK;
    }

    public static boolean isSafe(final BlockPos pos) {
        return isObby(pos) || isBedrock(pos) || isEnderChest(pos);
    }

    public static boolean isEnderChest(final BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).getBlock() == Blocks.ENDER_CHEST;
    }

    public static boolean isAir(final BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).getBlock() == Blocks.AIR;
    }

    public static boolean isObby(final BlockPos pos) {
        return BlockUtil.mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN;
    }

    public static double getMineDistance(final BlockPos to) {
        return getMineDistance((Entity) BlockUtil.mc.player, to);
    }

    public static boolean isHole(BlockPos blockPos) {
        return BlockUtil.validObi(blockPos) || BlockUtil.validBedrock(blockPos);
    }

    public static boolean validObi(BlockPos blockPos) {
        return !(BlockUtil.validBedrock(blockPos) || BlockUtil.mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() != Blocks.OBSIDIAN && BlockUtil.mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() != Blocks.BEDROCK || BlockUtil.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() != Blocks.OBSIDIAN && BlockUtil.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() != Blocks.BEDROCK || BlockUtil.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() != Blocks.OBSIDIAN && BlockUtil.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() != Blocks.BEDROCK || BlockUtil.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() != Blocks.OBSIDIAN && BlockUtil.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() != Blocks.BEDROCK || BlockUtil.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() != Blocks.OBSIDIAN && BlockUtil.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() != Blocks.BEDROCK || BlockUtil.mc.world.getBlockState(blockPos).getMaterial() != Material.AIR || BlockUtil.mc.world.getBlockState(blockPos.add(0, 1, 0)).getMaterial() != Material.AIR || BlockUtil.mc.world.getBlockState(blockPos.add(0, 2, 0)).getMaterial() != Material.AIR);
    }

    public static boolean validBedrock(BlockPos blockPos) {
        return BlockUtil.mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() == Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() == Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() == Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() == Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() == Blocks.BEDROCK && BlockUtil.mc.world.getBlockState(blockPos).getMaterial() == Material.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(0, 1, 0)).getMaterial() == Material.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(0, 2, 0)).getMaterial() == Material.AIR;
    }

    public static double getMineDistance(final Entity from, final BlockPos to) {
        final double x = from.posX - (to.getX() + 0.5);
        final double y = from.posY - (to.getY() + 0.5) + 1.5;
        final double z = from.posZ - (to.getZ() + 0.5);
        return x * x + y * y + z * z;
    }

    public static boolean PistonCheck(BlockPos blockPos) {
        if (BlockUtil.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(0, -1, 0)).getBlock() == Blocks.AIR) {
            return true;
        }
        if (BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.AIR) {
            return true;
        }
        return BlockUtil.checkEntityCrystal(EntityUtil.getVarOffsets(0, 0, 0), blockPos) != null;
    }

    public static double distanceToXZ(double d, double d2) {
        double d3 = BlockUtil.mc.player.posX - d;
        double d4 = BlockUtil.mc.player.posZ - d2;
        return Math.sqrt(d3 * d3 + d4 * d4);
    }

    public static boolean cantPistonPlace(BlockPos blockPos) {
        if (BlockUtil.mc.world.getBlockState(blockPos.add(0, 0, 1)).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(0, 0, -1)).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(1, 0, 0)).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(-1, 0, 0)).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(blockPos.add(0, 1, 0)).getBlock() == Blocks.AIR && BlockUtil.mc.world.getBlockState(blockPos).getBlock() != Blocks.AIR) {
            return true;
        }
        return BlockUtil.checkEntity(EntityUtil.getVarOffsets(0, 0, 0), blockPos) != null;
    }

    static Entity checkEntity(Vec3d[] vec3dArray, BlockPos blockPos) {
        Entity entity = null;
        for (Vec3d vec3d : vec3dArray) {
            BlockPos blockPos2 = new BlockPos(blockPos).add(vec3d.x, vec3d.y, vec3d.z);
            for (Entity entity2 : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos2))) {
                if (!(entity2 instanceof EntityPlayer)) continue;
                if (entity != null) {
                    continue;
                }
                entity = entity2;
            }
        }
        return entity;
    }

    static Entity checkEntityCrystal(Vec3d[] vec3dArray, BlockPos blockPos) {
        Entity entity = null;
        for (Vec3d vec3d : vec3dArray) {
            BlockPos blockPos2 = new BlockPos(blockPos).add(vec3d.x, vec3d.y, vec3d.z);
            for (Entity entity2 : BlockUtil.mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(blockPos2))) {
                if (!(entity2 instanceof EntityPlayer) && !(entity2 instanceof EntityEnderCrystal)) continue;
                if (entity != null) {
                    continue;
                }
                entity = entity2;
            }
        }
        return entity;
    }

    public static boolean isIntercepted(BlockPos blockPos) {
        for (Entity entity : BlockUtil.mc.world.loadedEntityList) {
            BlockPos blockPos2 = blockPos;
            if (entity instanceof EntityItem || entity instanceof EntityEnderCrystal || !new AxisAlignedBB(blockPos2).intersects(entity.getEntityBoundingBox()))
                continue;
            return true;
        }
        return false;
    }

    public static void placeBlockss(BlockPos blockPos, boolean bl, boolean bl2, boolean bl3) {
        for (EnumFacing enumFacing : EnumFacing.values()) {
            boolean bl4 = bl;
            boolean bl5 = bl2;
            boolean bl6 = bl3;
            BlockPos blockPos2 = blockPos;
            if (BlockUtil.mc.world.getBlockState(blockPos2.offset(enumFacing)).getBlock().equals((Object) Blocks.AIR) || BlockUtil.isIntercepted(blockPos2))
                continue;
            if (bl5) {
                BlockUtil.mc.player.connection.sendPacket((Packet) new CPacketPlayerTryUseItemOnBlock(blockPos2.offset(enumFacing), enumFacing.getOpposite(), EnumHand.MAIN_HAND, Float.intBitsToFloat(Float.floatToIntBits(2.7f)), Float.intBitsToFloat(Float.floatToIntBits(3.8f)), Float.intBitsToFloat(Float.floatToIntBits(30.0f))));
            } else {
                BlockUtil.mc.playerController.processRightClickBlock(BlockUtil.mc.player, BlockUtil.mc.world, blockPos2.offset(enumFacing), enumFacing.getOpposite(), new Vec3d((Vec3i) blockPos2), EnumHand.MAIN_HAND);
            }
            if (bl4) {
                BlockUtil.mc.player.swingArm(EnumHand.MAIN_HAND);
            }
            if (bl6) {
                RotationUtil.faceVector(new Vec3d((Vec3i) blockPos2), true);
            }
            return;
        }
    }


    public static List<EnumFacing> getPossibleSides(BlockPos pos) {
        ArrayList<EnumFacing> facings = new ArrayList<EnumFacing>();
        for (EnumFacing side : EnumFacing.values()) {
            IBlockState blockState;
            BlockPos neighbour = pos.offset(side);
            if (!mc.world.getBlockState(neighbour).getBlock().canCollideCheck(mc.world.getBlockState(neighbour), false) || (blockState = mc.world.getBlockState(neighbour)).getMaterial().isReplaceable())
                continue;
            facings.add(side);
        }
        return facings;
    }

    public static EnumFacing getFirstFacing(BlockPos pos) {
        Iterator<EnumFacing> iterator = BlockUtil.getPossibleSides(pos).iterator();
        if (iterator.hasNext()) {
            EnumFacing facing = iterator.next();
            return facing;
        }
        return null;
    }

    public static EnumFacing getRayTraceFacing(BlockPos pos) {
        RayTraceResult result = mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d((double) pos.getX() + 0.5, (double) pos.getX() - 0.5, (double) pos.getX() + 0.5));
        if (result == null || result.sideHit == null) {
            return EnumFacing.UP;
        }
        return result.sideHit;
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace) {
        return BlockUtil.isPositionPlaceable(pos, rayTrace, true);
    }

    public static int isPositionPlaceable(BlockPos pos, boolean rayTrace, boolean entityCheck) {
        Block block = mc.world.getBlockState(pos).getBlock();
        if (!(block instanceof BlockAir || block instanceof BlockLiquid || block instanceof BlockTallGrass || block instanceof BlockFire || block instanceof BlockDeadBush || block instanceof BlockSnow)) {
            return 0;
        }
        if (!BlockUtil.rayTracePlaceCheck(pos, rayTrace, 0.0f)) {
            return -1;
        }
        if (entityCheck) {
            for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
                if (entity instanceof EntityItem || entity instanceof EntityXPOrb) continue;
                return 1;
            }
        }
        for (EnumFacing side : BlockUtil.getPossibleSides(pos)) {
            if (!BlockUtil.canBeClicked(pos.offset(side))) continue;
            return 3;
        }
        return 2;
    }

    public static void rightClickBlock(BlockPos pos, Vec3d vec, EnumHand hand, EnumFacing direction, boolean packet) {
        if (packet) {
            float f = (float) (vec.x - (double) pos.getX());
            float f1 = (float) (vec.y - (double) pos.getY());
            float f2 = (float) (vec.z - (double) pos.getZ());
            mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, direction, hand, f, f1, f2));
        } else {
            mc.playerController.processRightClickBlock(mc.player, mc.world, pos, direction, vec, hand);
        }
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
    }


    public static boolean placeBlock(BlockPos pos, EnumHand hand, boolean rotate, boolean packet, boolean isSneaking) {
        boolean sneaking = false;
        EnumFacing side = BlockUtil.getFirstFacing(pos);
        if (side == null) {
            return isSneaking;
        }
        BlockPos neighbour = pos.offset(side);
        EnumFacing opposite = side.getOpposite();
        Vec3d hitVec = new Vec3d(neighbour).add(0.5, 0.5, 0.5).add(new Vec3d(opposite.getDirectionVec()).scale(0.5));
        Block neighbourBlock = mc.world.getBlockState(neighbour).getBlock();
        if (!mc.player.isSneaking() && (blackList.contains(neighbourBlock) || shulkerList.contains(neighbourBlock))) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            mc.player.setSneaking(true);
            sneaking = true;
        }
        if (rotate) {
            RotationUtil.faceVector(hitVec, true);
        }
        BlockUtil.rightClickBlock(neighbour, hitVec, hand, opposite, packet);
        mc.player.swingArm(EnumHand.MAIN_HAND);
        mc.rightClickDelayTimer = 4;
        return sneaking || isSneaking;
    }



    public static List<BlockPos> getSphere(final Entity entity, final float radius, final boolean ignoreAir) {
        final List<BlockPos> sphere = new ArrayList<BlockPos>();
        final BlockPos pos = PositionUtil.getPosition(entity);
        final int posX = pos.getX();
        final int posY = pos.getY();
        final int posZ = pos.getZ();
        final int radiuss = (int) radius;
        for (int x = posX - radiuss; x <= posX + radius; ++x) {
            for (int z = posZ - radiuss; z <= posZ + radius; ++z) {
                for (int y = posY - radiuss; y < posY + radius; ++y) {
                    if ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y) < radius * radius) {
                        final BlockPos position = new BlockPos(x, y, z);
                        if (!ignoreAir || BlockUtil.mc.world.getBlockState(position).getBlock() != Blocks.AIR) {
                            sphere.add(position);
                        }
                    }
                }
            }
        }
        return sphere;
    }

    public static List<BlockPos> getSphere(BlockPos pos, float r, int h, boolean hollow, boolean sphere, int plus_y) {
        ArrayList<BlockPos> circleblocks = new ArrayList<BlockPos>();
        int cx = pos.getX();
        int cy = pos.getY();
        int cz = pos.getZ();
        int x = cx - (int) r;
        while ((float) x <= (float) cx + r) {
            int z = cz - (int) r;
            while ((float) z <= (float) cz + r) {
                int y = sphere ? cy - (int) r : cy;
                while (true) {
                    float f = y;
                    float f2 = sphere ? (float) cy + r : (float) (cy + h);
                    if (!(f < f2)) break;
                    double dist = (cx - x) * (cx - x) + (cz - z) * (cz - z) + (sphere ? (cy - y) * (cy - y) : 0);
                    if (dist < (double) (r * r) && (!hollow || dist >= (double) ((r - 1.0f) * (r - 1.0f)))) {
                        BlockPos l = new BlockPos(x, y + plus_y, z);
                        circleblocks.add(l);
                    }
                    ++y;
                }
                ++z;
            }
            ++x;
        }
        return circleblocks;
    }



    public static boolean canBeClicked(BlockPos pos) {
        return BlockUtil.getBlock(pos).canCollideCheck(BlockUtil.getState(pos), false);
    }

    public static Block getBlock(BlockPos pos) {
        return getState(pos).getBlock();
    }


    private static IBlockState getState(BlockPos pos) {
        return mc.world.getBlockState(pos);
    }


    public static BlockPos[] toBlockPos(Vec3d[] vec3ds) {
        BlockPos[] list = new BlockPos[vec3ds.length];
        for (int i = 0; i < vec3ds.length; ++i) {
            list[i] = new BlockPos(vec3ds[i]);
        }
        return list;
    }

    public static Boolean isPosInFov(BlockPos pos) {
        int dirnumber = RotationUtil.getDirection4D();
        if (dirnumber == 0 && (double) pos.getZ() - mc.player.getPositionVector().z < 0.0) {
            return false;
        }
        if (dirnumber == 1 && (double) pos.getX() - mc.player.getPositionVector().x > 0.0) {
            return false;
        }
        if (dirnumber == 2 && (double) pos.getZ() - mc.player.getPositionVector().z > 0.0) {
            return false;
        }
        return dirnumber != 3 || (double) pos.getX() - mc.player.getPositionVector().x >= 0.0;
    }
    public static boolean isBlockUnSolid(Block block) {
        return unSolidBlocks.contains(block);
    }


    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; ++i) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }

    public static boolean rayTracePlaceCheck(BlockPos pos, boolean shouldCheck, float height) {
        return !shouldCheck || mc.world.rayTraceBlocks(new Vec3d(mc.player.posX, mc.player.posY + (double) mc.player.getEyeHeight(), mc.player.posZ), new Vec3d(pos.getX(), (float) pos.getY() + height, pos.getZ()), false, true, false) == null;
    }
}


