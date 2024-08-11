package fat.vonware.util;

import com.mojang.realmsclient.gui.ChatFormatting;
import fat.vonware.Vonware;
import fat.vonware.features.modules.client.HUD;
import fat.vonware.util.holeesp.HoleUtil;
import net.minecraft.block.*;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityAgeable;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureType;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.entity.item.EntityMinecart;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.monster.EntityIronGolem;
import net.minecraft.entity.monster.EntityPigZombie;
import net.minecraft.entity.passive.EntityAmbientCreature;
import net.minecraft.entity.passive.EntitySquid;
import net.minecraft.entity.passive.EntityVillager;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityFireball;
import net.minecraft.entity.projectile.EntityShulkerBullet;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.potion.Potion;
import net.minecraft.util.EnumHand;
import net.minecraft.util.MovementInput;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import java.awt.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.List;
import java.util.*;

public class EntityUtil
        implements Util {

    private static final List<Block> burrowList;
    public static void resetTimer() {
        EntityUtil.mc.timer.tickLength = 50.0f;
    }
    public static void setTimer(final float speed) {
        EntityUtil.mc.timer.tickLength = 50.0f / speed;
    }
    public static void attackEntity(Entity entity, boolean packet, boolean swingArm) {
        if (packet) {
            mc.player.connection.sendPacket(new CPacketUseEntity(entity));
        } else {
            mc.playerController.attackEntity(mc.player, entity);
        }
        if (swingArm) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
    }
    public static boolean isPlayerSafe(final EntityPlayer player) {
        final Vec3d playerVec = player.getPositionVector();
        final BlockPos position = new BlockPos(playerVec);
        return HoleUtil.isHole(position) || isInBurrow(player);
    }
    public static boolean isInHole(final Entity player) {
        final double decimalPoint = player.posY - Math.floor(player.posY);
        final BlockPos pos = new BlockPos(player.posX, (decimalPoint > 0.8) ? (Math.floor(player.posY) + 1.0) : Math.floor(player.posY), player.posZ);
        return EntityUtil.mc.world.getBlockState(pos.east()).getBlock() != Blocks.AIR && EntityUtil.mc.world.getBlockState(pos.west()).getBlock() != Blocks.AIR && EntityUtil.mc.world.getBlockState(pos.north()).getBlock() != Blocks.AIR && EntityUtil.mc.world.getBlockState(pos.south()).getBlock() != Blocks.AIR;
    }
    public static double getDistance(double p_X, double p_Y, double p_Z, double x, double y, double z) {
        double d0 = p_X - x;
        double d1 = p_Y - y;
        double d2 = p_Z - z;
        return MathHelper.sqrt(d0 * d0 + d1 * d1 + d2 * d2);
    }
    public static boolean isSafe(final Entity entity) {
        return isSafe(entity, 0, false);
    }

    public static float getHealth(final Entity entity, final boolean absorption) {
        if (isLiving(entity)) {
            final EntityLivingBase livingBase = (EntityLivingBase)entity;
            return livingBase.getHealth() + (absorption ? livingBase.getAbsorptionAmount() : 0.0f);
        }
        return 0.0f;
    }

    public static void moveEntityStrafe(final double speed, final Entity entity) {
        if (entity != null) {
            final MovementInput movementInput = EntityUtil.mc.player.movementInput;
            double forward = movementInput.moveForward;
            double strafe = movementInput.moveStrafe;
            float yaw = EntityUtil.mc.player.rotationYaw;
            if (forward == 0.0 && strafe == 0.0) {
                entity.motionX = 0.0;
                entity.motionZ = 0.0;
            } else {
                if (forward != 0.0) {
                    if (strafe > 0.0) {
                        yaw += ((forward > 0.0) ? -45 : 45);
                    } else if (strafe < 0.0) {
                        yaw += ((forward > 0.0) ? 45 : -45);
                    }
                    strafe = 0.0;
                    if (forward > 0.0) {
                        forward = 1.0;
                    } else if (forward < 0.0) {
                        forward = -1.0;
                    }
                }
                entity.motionX = forward * speed * Math.cos(Math.toRadians(yaw + 90.0f)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0f));
                entity.motionZ = forward * speed * Math.sin(Math.toRadians(yaw + 90.0f)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0f));
            }
        }
    }
    public static boolean isInBurrow(final EntityPlayer player) {
        final BlockPos pos = PositionUtil.getPosition((Entity)player);
        return isBurrow(pos, player) || isBurrow(pos.up(), player);
    }
    public static boolean isBurrow(final BlockPos pos, final EntityPlayer player) {
        final IBlockState state = EntityUtil.mc.world.getBlockState(pos);
        return EntityUtil.burrowList.contains(state.getBlock()) && state.getBoundingBox((IBlockAccess)EntityUtil.mc.world, pos).offset(pos).maxY > player.posY;
    }
    static {
        burrowList = Arrays.asList(Blocks.BEDROCK, Blocks.OBSIDIAN, Blocks.ENDER_CHEST, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, (Block)Blocks.BEACON, (Block)Blocks.PISTON, Blocks.REDSTONE_BLOCK, Blocks.ENCHANTING_TABLE, Blocks.ANVIL);
    }
    public static boolean isPhasing() {
        return isPhasing((EntityPlayer)EntityUtil.mc.player);
    }

    public static boolean isPhasing(final EntityPlayer player) {
        final AxisAlignedBB bb = player.getEntityBoundingBox();
        for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; ++x) {
            for (int y = MathHelper.floor(bb.minY); y < MathHelper.floor(bb.maxY) + 1; ++y) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; ++z) {
                    final BlockPos pos = new BlockPos(x, y, z);
                    final IBlockState state = EntityUtil.mc.world.getBlockState(pos);
                    final AxisAlignedBB blockBB = state.getSelectedBoundingBox((World)EntityUtil.mc.world, pos);
                    if (state.getMaterial().blocksMovement() && bb.intersects(blockBB)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    public static double getEyeHeight(final Entity entity) {
        return entity.posY + entity.getEyeHeight();
    }
    public static boolean isBurrow(EntityPlayer target) {
        if(mc.world == null || mc.player == null || target == null)
        {
            return false;
        }
        final BlockPos blockPos = new BlockPos(target.posX, target.posY, target.posZ);
        return mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPos).getBlock().equals(Blocks.ENDER_CHEST);
    }
    public static BlockPos getOtherPlayerPos(final EntityPlayer player) {
        final double decimalPoint = player.posY - Math.floor(player.posY);
        return new BlockPos(player.posX, (decimalPoint > 0.8) ? (Math.floor(player.posY) + 1.0) : Math.floor(player.posY), player.posZ);
    }
    public static EntityPlayer getTarget(final double targetRange) {
        return (EntityPlayer) mc.world.getLoadedEntityList().stream().filter(Objects::nonNull).filter(entity -> entity instanceof EntityPlayer).filter(EntityUtil::isAlive).filter(entity -> !entity.getName().equals(mc.player.getName())).filter(entity -> entity.getEntityId() != mc.player.getEntityId()).filter(entity -> !Vonware.friendManager.isFriend(((EntityPlayer) entity).getDisplayNameString())).filter(entity -> mc.player.getDistance(entity) <= targetRange).min(Comparator.comparingDouble(entity -> mc.player.getDistance(entity))).orElse(null);
    }
    public static Vec3d[] getVarOffsets(int n, int n2, int n3) {
        List<Vec3d> list = EntityUtil.getVarOffsetList(n, n2, n3);
        Vec3d[] vec3dArray = new Vec3d[list.size()];
        return list.toArray(vec3dArray);
    }
    public static List<Vec3d> getVarOffsetList(int n, int n2, int n3) {
        ArrayList<Vec3d> arrayList = new ArrayList<>();
        arrayList.add(new Vec3d(n, n2, n3));
        return arrayList;
    }
    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * (double) time, entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * (double) time, entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * (double) time);
    }

    public static Vec3d getInterpolatedPos(Entity entity, float partialTicks) {
        return new Vec3d(entity.lastTickPosX, entity.lastTickPosY, entity.lastTickPosZ).add(EntityUtil.getInterpolatedAmount(entity, partialTicks));
    }

    public static Vec3d getInterpolatedRenderPos(Entity entity, float partialTicks) {
        return EntityUtil.getInterpolatedPos(entity, partialTicks).subtract(mc.getRenderManager().renderPosX, mc.getRenderManager().renderPosY, mc.getRenderManager().renderPosZ);
    }
    public static Vec3d getInterpolatedAmount(Entity entity, double x, double y, double z) {
        return new Vec3d((entity.posX - entity.lastTickPosX) * x, (entity.posY - entity.lastTickPosY) * y, (entity.posZ - entity.lastTickPosZ) * z);
    }

    public static Vec3d getInterpolatedAmount(Entity entity, float partialTicks) {
        return EntityUtil.getInterpolatedAmount(entity, partialTicks, partialTicks, partialTicks);
    }

    public static boolean isSafe(Entity entity, int height, boolean floor) {
        return EntityUtil.getUnsafeBlocks(entity, height, floor).size() == 0;
    }
    public static BlockPos getPlayerPos(EntityPlayer player) {
        return new BlockPos(Math.floor(player.posX), Math.floor(player.posY), Math.floor(player.posZ));
    }

    public static List<Vec3d> getUnsafeBlocks(Entity entity, int height, boolean floor) {
        return EntityUtil.getUnsafeBlocksFromVec3d(entity.getPositionVector(), height, floor);
    }

    public static boolean isNeutralMob(Entity entity) {
        return entity instanceof EntityPigZombie || entity instanceof EntityWolf || entity instanceof EntityEnderman;
    }
    public static List<Vec3d> getUnsafeBlocksFromVec3d(Vec3d pos, int height, boolean floor) {
        ArrayList<Vec3d> vec3ds = new ArrayList<Vec3d>();
        for (Vec3d vector : EntityUtil.getOffsets(height, floor)) {
            BlockPos targetPos = new BlockPos(pos).add(vector.x, vector.y, vector.z);
            Block block = mc.world.getBlockState(targetPos).getBlock();
            if (!(block instanceof BlockAir) && !(block instanceof BlockLiquid) && !(block instanceof BlockTallGrass) && !(block instanceof BlockFire) && !(block instanceof BlockDeadBush) && !(block instanceof BlockSnow))
                continue;
            vec3ds.add(vector);
        }
        return vec3ds;
    }
    public static boolean isInLiquid() {
        return EntityUtil.mc.player.isInWater() || EntityUtil.mc.player.isInLava();
    }

    public static List<Vec3d> getOffsetList(int y, boolean floor) {
        ArrayList<Vec3d> offsets = new ArrayList<Vec3d>();
        offsets.add(new Vec3d(-1.0, y, 0.0));
        offsets.add(new Vec3d(1.0, y, 0.0));
        offsets.add(new Vec3d(0.0, y, -1.0));
        offsets.add(new Vec3d(0.0, y, 1.0));
        if (floor) {
            offsets.add(new Vec3d(0.0, y - 1, 0.0));
        }
        return offsets;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        List<Vec3d> offsets = EntityUtil.getOffsetList(y, floor);
        Vec3d[] array = new Vec3d[offsets.size()];
        return offsets.toArray(array);
    }
    public static boolean isLiving(Entity entity) {
        return entity instanceof EntityLivingBase;
    }

    public static boolean isAlive(Entity entity) {
        return EntityUtil.isLiving(entity) && !entity.isDead && ((EntityLivingBase) entity).getHealth() > 0.0f;
    }

    public static boolean isDead(Entity entity) {
        return !EntityUtil.isAlive(entity);
    }

    public static float getHealth(Entity entity) {
        if (EntityUtil.isLiving(entity)) {
            EntityLivingBase livingBase = (EntityLivingBase) entity;
            return livingBase.getHealth() + livingBase.getAbsorptionAmount();
        }
        return 0.0f;
    }

    public static boolean isntValid(Entity entity, double range) {
        return entity == null || EntityUtil.isDead(entity) || entity.equals(mc.player) || entity instanceof EntityPlayer && Vonware.friendManager.isFriend(entity.getName()) || mc.player.getDistanceSq(entity) > MathUtil.square(range);
    }
    public static double getMaxSpeed() {
        double maxModifier = 0.2873;
        if (mc.player.isPotionActive(Objects.requireNonNull(Potion.getPotionById(1)))) {
            maxModifier *= 1.0 + 0.2 * (double) (Objects.requireNonNull(mc.player.getActivePotionEffect(Objects.requireNonNull(Potion.getPotionById(1)))).getAmplifier() + 1);
        }
        return maxModifier;
    }
    public static Color getColor(Entity entity, int red, int green, int blue, int alpha, boolean colorFriends) {
        Color color = new Color((float) red / 255.0f, (float) green / 255.0f, (float) blue / 255.0f, (float) alpha / 255.0f);
        if (entity instanceof EntityPlayer && colorFriends && Vonware.friendManager.isFriend((EntityPlayer) entity)) {
            color = new Color(0.33333334f, 1.0f, 1.0f, (float) alpha / 255.0f);
        }
        return color;
    }
    public static EntityPlayer getClosestEnemy(double distance) {
        EntityPlayer closest = null;
        for (EntityPlayer player : mc.world.playerEntities) {
            if (EntityUtil.isntValid(player, distance)) continue;
            if (closest == null) {
                closest = player;
                continue;
            }
            if (!(mc.player.getDistanceSq(player) < mc.player.getDistanceSq(closest)))
                continue;
            closest = player;
        }
        return closest;
    }

    public static double[] forward(double speed) {
        float forward = mc.player.movementInput.moveForward;
        float side = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.prevRotationYaw + (mc.player.rotationYaw - mc.player.prevRotationYaw) * mc.getRenderPartialTicks();
        if (forward != 0.0f) {
            if (side > 0.0f) {
                yaw += (float) (forward > 0.0f ? -45 : 45);
            } else if (side < 0.0f) {
                yaw += (float) (forward > 0.0f ? 45 : -45);
            }
            side = 0.0f;
            if (forward > 0.0f) {
                forward = 1.0f;
            } else if (forward < 0.0f) {
                forward = -1.0f;
            }
        }
        double sin = Math.sin(Math.toRadians(yaw + 90.0f));
        double cos = Math.cos(Math.toRadians(yaw + 90.0f));
        double posX = (double) forward * speed * cos + (double) side * speed * sin;
        double posZ = (double) forward * speed * sin - (double) side * speed * cos;
        return new double[]{posX, posZ};
    }
    public static Map<String, Integer> getTextRadarPlayers() {
        Map<String, Integer> output = new HashMap<>();
        DecimalFormat dfHealth = new DecimalFormat("#.#");
        dfHealth.setRoundingMode(RoundingMode.CEILING);
        DecimalFormat dfDistance = new DecimalFormat("#.#");
        dfDistance.setRoundingMode(RoundingMode.CEILING);
        StringBuilder healthSB = new StringBuilder();
        StringBuilder distanceSB = new StringBuilder();
        for (EntityPlayer player : EntityUtil.mc.world.playerEntities) {
            if (player.getName().equalsIgnoreCase(EntityUtil.mc.player.getName()) && !(boolean)HUD.INSTANCE.selfTextRadar.getValue()) {
                continue;
            }
            if (player.isInvisible() && player.getName().equals(EntityUtil.mc.player.getName()))
                continue;
            int hpRaw = (int) EntityUtil.getHealth(player);
            String hp = dfHealth.format(hpRaw);
            healthSB.append("\u00a7");
            if (hpRaw >= 20) {
                healthSB.append("a");
            } else if (hpRaw >= 10) {
                healthSB.append("e");
            } else if (hpRaw >= 5) {
                healthSB.append("6");
            } else {
                healthSB.append("c");
            }
            healthSB.append(hp);
            int distanceInt = (int) EntityUtil.mc.player.getDistance(player);
            String distance = dfDistance.format(distanceInt);
            distanceSB.append("\u00a7");
            if (distanceInt >= 25) {
                distanceSB.append("a");
            } else if (distanceInt > 10) {
                distanceSB.append("6");
            } else {
                distanceSB.append("c");
            }
            distanceSB.append(distance);
            output.put(healthSB + " " + (Vonware.friendManager.isFriend(player) ? "\u00a7b" : "\u00a7r") + player.getName() + " " + distanceSB + " " + Vonware.potionManager.getTextRadarPotion(player), (int) EntityUtil.mc.player.getDistance(player));
            healthSB.setLength(0);
            distanceSB.setLength(0);
        }
        if (!output.isEmpty()) {
            output = MathUtil.sortByValue(output, false);
        }
        return output;
    }
}

