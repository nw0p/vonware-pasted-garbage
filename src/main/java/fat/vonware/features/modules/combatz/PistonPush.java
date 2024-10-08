package fat.vonware.features.modules.combatz;

import java.util.Comparator;
import java.util.stream.Collectors;
import fat.vonware.Vonware;
import fat.vonware.features.modules.Module;
import fat.vonware.features.modules.exploit.Speedmine;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.BlockUtil;
import fat.vonware.util.EntityUtil;
import fat.vonware.util.InventoryUtil;
import fat.vonware.util.RotationUtil;
import fat.vonware.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.block.BlockPistonBase;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;

public class PistonPush
        extends Module {
    EntityPlayer target;
    final Timer breakTimer;
    public final Setting<Boolean> pullBack;
    final Timer delayTime;
    public final Setting<Integer> delay;
    public final Setting<Boolean> autoToggle;
    public final Setting<Boolean> onlyBurrow;
    int rotationPitch = 0;
    public final Setting<Boolean> onlyGround = this.register(new Setting<>("onlyGround", false));
    public final Setting<Double> pistonCheck;
    public final Setting<Integer> surroundCheck;
    int rotationYaw = 0;
    public final Setting<Double> range;

    public PistonPush() {
        super("PistonPush", "", Module.Category.COMBATZ, true, false, false);
        this.autoToggle = this.register(new Setting<>("autoToggle", true));
        this.pullBack = this.register(new Setting<>("pullBack", true));
        this.onlyBurrow = this.register(new Setting<>("onlyBurrow", Boolean.TRUE, this::new0));
        this.range = this.register(new Setting<>("Range", 4.0, 0.0, 6.0));
        this.delay = this.register(new Setting<>("Delay", 5, 0, 10));
        this.surroundCheck = this.register(new Setting<>("SurroundCheck", 2, 0, 4));
        this.pistonCheck = this.register(new Setting<>("PistonCheck", 2.0, 0.0, 4.0));
        this.delayTime = new Timer();
        this.breakTimer = new Timer();
    }

    Boolean canPush(EntityPlayer entityPlayer) {
        int n = 0;
        if (this.pullBack.getValue() && !this.onlyBurrow.getValue()) {
            return true;
        }
        if (!PistonPush.mc.world.isAirBlock(new BlockPos(entityPlayer.posX, entityPlayer.posY + 2.5, entityPlayer.posZ))) {
            return false;
        }
        if (!PistonPush.mc.world.isAirBlock(new BlockPos(entityPlayer.posX, entityPlayer.posY + 0.5, entityPlayer.posZ))) {
            return true;
        }
        if (!PistonPush.mc.world.isAirBlock(new BlockPos(entityPlayer.posX + 1.0, entityPlayer.posY + 0.5, entityPlayer.posZ))) {
            ++n;
        }
        if (!PistonPush.mc.world.isAirBlock(new BlockPos(entityPlayer.posX - 1.0, entityPlayer.posY + 0.5, entityPlayer.posZ))) {
            ++n;
        }
        if (!PistonPush.mc.world.isAirBlock(new BlockPos(entityPlayer.posX, entityPlayer.posY + 0.5, entityPlayer.posZ + 1.0))) {
            ++n;
        }
        if (!PistonPush.mc.world.isAirBlock(new BlockPos(entityPlayer.posX, entityPlayer.posY + 0.5, entityPlayer.posZ - 1.0))) {
            ++n;
        }
        return n > this.surroundCheck.getValue() - 1;
    }

    private static Float attackCrystal2(Entity entity) {
        return PistonPush.mc.player.getDistance(entity);
    }

    public boolean attackCrystal(BlockPos blockPos) {
        for (Entity entity : PistonPush.mc.world.loadedEntityList.stream().filter(PistonPush::attackCrystal1).sorted(Comparator.comparing(PistonPush::attackCrystal2)).collect(Collectors.toList())) {
            if (!(entity instanceof EntityEnderCrystal)) continue;
            if (!(entity.getDistance(blockPos.getX(), blockPos.getY(), blockPos.getZ()) < 4.0)) {
                continue;
            }
            if (!this.breakTimer.passedMs(300L)) {
                return true;
            }
            this.breakTimer.reset();
            PistonPush.mc.player.connection.sendPacket(new CPacketUseEntity(entity));
            PistonPush.mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            BlockPos blockPos2 = new BlockPos(entity.posX, entity.posY, entity.posZ);
            EnumFacing enumFacing = BlockUtil.getFirstFacing(blockPos2);
            if (enumFacing == null) {
                return true;
            }
            BlockPos blockPos3 = blockPos2.offset(enumFacing);
            EnumFacing enumFacing2 = enumFacing.getOpposite();
            Vec3d vec3d = new Vec3d(blockPos3).add(0.5, 0.5, 0.5).add(new Vec3d(enumFacing2.getDirectionVec()).scale(0.5));
            RotationUtil.faceVector(vec3d, true);
            return true;
        }
        return false;
    }

    public static int getPitch(BlockPos blockPos) {
        EnumFacing enumFacing = BlockUtil.getFirstFacing(blockPos);
        if (enumFacing == null) {
            return 0;
        }
        BlockPos blockPos2 = blockPos.offset(enumFacing);
        EnumFacing enumFacing2 = enumFacing.getOpposite();
        Vec3d vec3d = new Vec3d(blockPos2).add(0.5, 0.5, 0.5).add(new Vec3d(enumFacing2.getDirectionVec()).scale(0.5));
        float[] fArray = RotationUtil.getLegitRotations(vec3d);
        return (int)fArray[0];
    }

    public static int getYaw(BlockPos blockPos) {
        EnumFacing enumFacing = BlockUtil.getFirstFacing(blockPos);
        if (enumFacing == null) {
            return 0;
        }
        BlockPos blockPos2 = blockPos.offset(enumFacing);
        EnumFacing enumFacing2 = enumFacing.getOpposite();
        Vec3d vec3d = new Vec3d(blockPos2).add(0.5, 0.5, 0.5).add(new Vec3d(enumFacing2.getDirectionVec()).scale(0.5));
        float[] fArray = RotationUtil.getLegitRotations(vec3d);
        return (int)fArray[1];
    }

    @Override
    public void onUpdate() {
        if (!this.delayTime.passedMs(this.delay.getValue() * 50)) {
            return;
        }
        if (InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK) == -1) {
            return;
        }
        if (InventoryUtil.findHotbarBlock(BlockPistonBase.class) == -1) {
            return;
        }
        this.target = this.getTarget(this.range.getValue());
        if (this.target == null) {
            return;
        }
        BlockPos blockPos = new BlockPos(this.target.posX, this.target.posY + 0.5, this.target.posZ);
        if (this.getBlock(blockPos.add(0, 2, 0)) == Blocks.AIR) {
            int n;
            BlockPos blockPos2;
            for (EnumFacing enumFacing : EnumFacing.VALUES) {
                if (enumFacing == EnumFacing.DOWN) continue;
                if (enumFacing == EnumFacing.UP) {
                    continue;
                }
                blockPos2 = blockPos.offset(enumFacing).offset(EnumFacing.UP);
                if (!(this.getBlock(blockPos2) instanceof BlockPistonBase) || this.getBlock(blockPos2.offset(enumFacing, -2)) != Blocks.AIR || this.getBlock(blockPos2.offset(enumFacing, -2).offset(EnumFacing.UP)) != Blocks.AIR) continue;
                this.delayTime.reset();
                for (EnumFacing enumFacing2 : EnumFacing.VALUES) {
                    if (this.getBlock(blockPos2.offset(enumFacing2)) != Blocks.REDSTONE_BLOCK) continue;
                    if (this.autoToggle.getValue()) {
                        this.disable();
                    }
                    return;
                }
                EnumFacing[] enumFacingArray = EnumFacing.VALUES;
                int n2 = enumFacingArray.length;
                for (n = 0; n < n2; ++n) {
                    EnumFacing enumFacing2;
                    enumFacing2 = enumFacingArray[n];
                    if (BlockUtil.PistonCheck(blockPos2.offset(enumFacing2))) {
                        continue;
                    }
                    int n3 = PistonPush.mc.player.inventory.currentItem;
                    PistonPush.mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
                    PistonPush.mc.playerController.updateController();
                    BlockUtil.placeBlock(blockPos2.offset(enumFacing2), EnumHand.MAIN_HAND, true, true, false);
                    PistonPush.mc.player.inventory.currentItem = n3;
                    PistonPush.mc.playerController.updateController();
                    return;
                }
            }
            for (EnumFacing enumFacing : EnumFacing.VALUES) {
                if (enumFacing == EnumFacing.DOWN) continue;
                if (enumFacing == EnumFacing.UP) {
                    continue;
                }
                blockPos2 = blockPos.offset(enumFacing).offset(EnumFacing.UP);
                if ((PistonPush.mc.player.posY - this.target.posY <= -1.0 || PistonPush.mc.player.posY - this.target.posY >= 2.0) && BlockUtil.distanceToXZ((double)blockPos2.getX() + 0.5, (double)blockPos2.getZ() + 0.5) < this.pistonCheck.getValue()) {
                    continue;
                }
                boolean bl = false;
                EnumFacing[] enumFacingArray = EnumFacing.VALUES;
                n = enumFacingArray.length;
                for (int i = 0; i < n; ++i) {
                    EnumFacing enumFacing3 = enumFacingArray[i];
                    if (BlockUtil.PistonCheck(blockPos2.offset(enumFacing3))) continue;
                    bl = true;
                    break;
                }
                if (!bl) {
                    continue;
                }
                if (!BlockUtil.PistonCheck(blockPos2) && this.attackCrystal(blockPos2)) {
                    return;
                }
                if (BlockUtil.cantPistonPlace(blockPos2) || this.getBlock(blockPos2.offset(enumFacing, -2)) != Blocks.AIR || this.getBlock(blockPos2.offset(enumFacing, -2).offset(EnumFacing.UP)) != Blocks.AIR) continue;
                if (BlockUtil.PistonCheck(blockPos2)) {
                    if (BlockUtil.PistonCheck(blockPos2.add(0, -1, 0))) {
                        continue;
                    }
                    int n4 = PistonPush.mc.player.inventory.currentItem;
                    PistonPush.mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
                    PistonPush.mc.playerController.updateController();
                    BlockUtil.placeBlock(blockPos2.add(0, -1, 0), EnumHand.MAIN_HAND, true, true, false);
                    PistonPush.mc.player.inventory.currentItem = n4;
                    PistonPush.mc.playerController.updateController();
                }
                this.delayTime.reset();
                if (this.getBlock(blockPos2) == Blocks.AIR) {
                    this.rotationPitch = PistonPush.getPitch(blockPos2);
                    this.rotationYaw = PistonPush.getYaw(blockPos2);
                    if (enumFacing == EnumFacing.EAST) {
                        RotationUtil.faceYawAndPitch(-90.0f, 0.0f);
                    } else if (enumFacing == EnumFacing.WEST) {
                        RotationUtil.faceYawAndPitch(90.0f, 0.0f);
                    } else if (enumFacing == EnumFacing.NORTH) {
                        RotationUtil.faceYawAndPitch(180.0f, 0.0f);
                    } else if (enumFacing == EnumFacing.SOUTH) {
                        RotationUtil.faceYawAndPitch(0.0f, 0.0f);
                    }
                    int n5 = PistonPush.mc.player.inventory.currentItem;
                    PistonPush.mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(BlockPistonBase.class);
                    PistonPush.mc.playerController.updateController();
                    BlockUtil.placeBlock(blockPos2, EnumHand.MAIN_HAND, false, true, false);
                    PistonPush.mc.player.inventory.currentItem = n5;
                    PistonPush.mc.playerController.updateController();
                    RotationUtil.facePos(blockPos2);
                }
                return;
            }
            if (this.getBlock(blockPos) == Blocks.AIR && this.onlyBurrow.getValue() || !this.pullBack.getValue()) {
                return;
            }
            for (EnumFacing enumFacing : EnumFacing.VALUES) {
                if (enumFacing == EnumFacing.DOWN) continue;
                if (enumFacing == EnumFacing.UP) {
                    continue;
                }
                blockPos2 = blockPos.offset(enumFacing).offset(EnumFacing.UP);
                if (!(this.getBlock(blockPos2) instanceof BlockPistonBase) || this.getBlock(blockPos2.offset(EnumFacing.UP)) != Blocks.AIR && this.getBlock(blockPos2.offset(EnumFacing.UP)) != Blocks.REDSTONE_BLOCK) continue;
                this.delayTime.reset();
                for (EnumFacing enumFacing4 : EnumFacing.VALUES) {
                    if (this.getBlock(blockPos2.offset(enumFacing4)) != Blocks.REDSTONE_BLOCK) continue;
                    this.mine(blockPos2.offset(enumFacing4));
                    if (this.autoToggle.getValue()) {
                        this.disable();
                    }
                    return;
                }
                for (EnumFacing enumFacing4 : EnumFacing.VALUES) {
                    if (BlockUtil.PistonCheck(blockPos2.offset(enumFacing4))) {
                        continue;
                    }
                    int n6 = PistonPush.mc.player.inventory.currentItem;
                    PistonPush.mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
                    PistonPush.mc.playerController.updateController();
                    BlockUtil.placeBlock(blockPos2.offset(enumFacing4), EnumHand.MAIN_HAND, true, true, false);
                    PistonPush.mc.player.inventory.currentItem = n6;
                    PistonPush.mc.playerController.updateController();
                    return;
                }
            }
            for (EnumFacing enumFacing : EnumFacing.VALUES) {
                if (enumFacing == EnumFacing.DOWN) continue;
                if (enumFacing == EnumFacing.UP) {
                    continue;
                }
                blockPos2 = blockPos.offset(enumFacing).offset(EnumFacing.UP);
                if ((PistonPush.mc.player.posY - this.target.posY <= -1.0 || PistonPush.mc.player.posY - this.target.posY >= 2.0) && BlockUtil.distanceToXZ((double)blockPos2.getX() + 0.5, (double)blockPos2.getZ() + 0.5) < this.pistonCheck.getValue()) {
                    continue;
                }
                boolean bl = false;
                for (EnumFacing enumFacing3 : EnumFacing.VALUES) {
                    if (BlockUtil.PistonCheck(blockPos2.offset(enumFacing3))) continue;
                    bl = true;
                    break;
                }
                if (!bl) {
                    continue;
                }
                if (!BlockUtil.PistonCheck(blockPos2) && this.attackCrystal(blockPos2)) {
                    return;
                }
                if (BlockUtil.cantPistonPlace(blockPos2) || this.getBlock(blockPos2.offset(EnumFacing.UP)) != Blocks.AIR && this.getBlock(blockPos2.offset(EnumFacing.UP)) != Blocks.REDSTONE_BLOCK) continue;
                if (BlockUtil.PistonCheck(blockPos2)) {
                    if (BlockUtil.PistonCheck(blockPos2.add(0, -1, 0))) {
                        continue;
                    }
                    int n7 = PistonPush.mc.player.inventory.currentItem;
                    PistonPush.mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(Blocks.REDSTONE_BLOCK);
                    PistonPush.mc.playerController.updateController();
                    BlockUtil.placeBlock(blockPos2.add(0, -1, 0), EnumHand.MAIN_HAND, true, true, false);
                    PistonPush.mc.player.inventory.currentItem = n7;
                    PistonPush.mc.playerController.updateController();
                }
                this.delayTime.reset();
                if (this.getBlock(blockPos2) == Blocks.AIR) {
                    this.rotationPitch = PistonPush.getPitch(blockPos2);
                    this.rotationYaw = PistonPush.getYaw(blockPos2);
                    if (enumFacing == EnumFacing.EAST) {
                        RotationUtil.faceYawAndPitch(-90.0f, 0.0f);
                    } else if (enumFacing == EnumFacing.WEST) {
                        RotationUtil.faceYawAndPitch(90.0f, 0.0f);
                    } else if (enumFacing == EnumFacing.NORTH) {
                        RotationUtil.faceYawAndPitch(180.0f, 0.0f);
                    } else if (enumFacing == EnumFacing.SOUTH) {
                        RotationUtil.faceYawAndPitch(0.0f, 0.0f);
                    }
                    int n8 = PistonPush.mc.player.inventory.currentItem;
                    PistonPush.mc.player.inventory.currentItem = InventoryUtil.findHotbarBlock(BlockPistonBase.class);
                    PistonPush.mc.playerController.updateController();
                    BlockUtil.placeBlock(blockPos2, EnumHand.MAIN_HAND, false, true, false);
                    PistonPush.mc.player.inventory.currentItem = n8;
                    PistonPush.mc.playerController.updateController();
                }
                return;
            }
        }
    }

    private static boolean attackCrystal1(Entity entity) {
        return entity instanceof EntityEnderCrystal && !entity.isDead;
    }

    EntityPlayer getTarget(double d) {
        EntityPlayer entityPlayer = null;
        double d2 = d;
        for (EntityPlayer entityPlayer2 : PistonPush.mc.world.playerEntities) {
            if (!this.canPush(entityPlayer2) || EntityUtil.isntValid(entityPlayer2, d) || !entityPlayer2.onGround && this.onlyGround.getValue() || Vonware.friendManager.isFriend(entityPlayer2.getName())) continue;
            if (PistonPush.mc.player.posY - entityPlayer2.posY >= 5.0) {
                continue;
            }
            if (entityPlayer == null) {
                entityPlayer = entityPlayer2;
                d2 = PistonPush.mc.player.getDistanceSq(entityPlayer2);
                continue;
            }
            if (PistonPush.mc.player.getDistanceSq(entityPlayer2) >= d2) {
                continue;
            }
            entityPlayer = entityPlayer2;
            d2 = PistonPush.mc.player.getDistanceSq(entityPlayer2);
        }
        return entityPlayer;
    }

    Block getBlock(BlockPos blockPos) {
        return PistonPush.mc.world.getBlockState(blockPos).getBlock();
    }

    public void mine(BlockPos blockPos) {
        if (Speedmine.breakPos != null && Speedmine.breakPos.equals(blockPos)) {
            return;
        }
        if (PistonPush.mc.world.getBlockState(blockPos).getBlock() != Blocks.AIR && PistonPush.mc.world.getBlockState(blockPos).getBlock() != Blocks.WEB && PistonPush.mc.world.getBlockState(blockPos).getBlock() != Blocks.REDSTONE_WIRE && PistonPush.mc.world.getBlockState(blockPos).getBlock() != Blocks.WATER && PistonPush.mc.world.getBlockState(blockPos).getBlock() != Blocks.LAVA) {
            PistonPush.mc.playerController.onPlayerDamageBlock(blockPos, BlockUtil.getRayTraceFacing(blockPos));
        }
    }

    private boolean new0(Boolean bl) {
        return this.pullBack.getValue();
    }

    @Override
    public String getDisplayInfo() {
        if (this.target != null) {
            return this.target.getName();
        }
        return null;
    }
}

