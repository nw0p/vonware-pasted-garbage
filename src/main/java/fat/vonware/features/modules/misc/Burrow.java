package fat.vonware.features.modules.misc;

import fat.vonware.event.events.PacketEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.BlockUtil;
import fat.vonware.util.InventoryUtil;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Burrow extends Module {

    public Burrow() {
        super("Burrow", "burrow but burrow", Category.MISC, true, false, false);
    }

    public Setting<Float> timer = this.register(new Setting("Timer", 8.0f, 1.0f, 8.0f));
    public Setting<Boolean> breakCrystal = this.register(new Setting("Break Crystal", true));
    public Setting<Boolean> autoCenter = this.register(new Setting("Auto Center", false));
    public Setting<Integer> timeoutTicks = this.register(new Setting("Timeout Ticks", 10, 0, 100));
    public Setting<Boolean> autoDisable = this.register(new Setting("Auto Disable", true));
    public long velocityTime;
    public int ticksEnabled;

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null || !mc.player.onGround || mc.player.posY - mc.player.prevPosY > 0.01f || !canPlace(new BlockPos(mc.player.getPositionVector()))) {
            mc.timer.tickLength = 50.0f;
            return;
        }
        if (autoCenter.getValue()) {
            double x = (Math.floor(mc.player.posX) + 0.5f) - mc.player.posX;
            double z = (Math.floor(mc.player.posZ) + 0.5f) - mc.player.posZ;
            double speed = Math.hypot(x, z);
            double base = mc.player.isSneaking() ? 0.057f : 0.287f;
            if (speed > base) {
                x *= (base / speed);
                z *= (base / speed);
            }
            mc.player.motionX = x;
            mc.player.motionZ = z;
        }
        mc.timer.tickLength = 50.0f / timer.getValue();
        ticksEnabled = 0;
    }

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50.0f;
        ticksEnabled = 0;
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null || !mc.player.onGround || mc.player.posY - mc.player.prevPosY > 0.01f || !canPlace(new BlockPos(mc.player.getPositionVector()))) {
            mc.timer.tickLength = 50.0f;
            return;
        }
        ticksEnabled++;
        if (ticksEnabled >= timeoutTicks.getValue()) {
            mc.timer.tickLength = 50.0f;
            this.disable();
        }
        BlockPos pos = new BlockPos(mc.player.getPositionVector());
        if (InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN) != -1) {
            if (breakCrystal.getValue()) {
                breakCrystal(pos);
            }
            mc.player.connection.sendPacket(new CPacketPlayer.PositionRotation(mc.player.posX, mc.player.posY + 0.41999808688698, mc.player.posZ, mc.player.rotationYaw, 90.0f, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7500019, mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.9999962, mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.17000380178814, mc.player.posZ, false));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.17001330178815, mc.player.posZ, false));
            int oldSlot = mc.player.inventory.currentItem;
            mc.player.connection.sendPacket(new CPacketHeldItemChange(InventoryUtil.findHotbarBlock(Blocks.OBSIDIAN)));
            BlockUtil.placeBlock(pos, EnumHand.MAIN_HAND, false, true, true);
            mc.player.connection.sendPacket(new CPacketHeldItemChange(oldSlot));
            mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.2426308013947485, mc.player.posZ, false));
            if (velocityTime > System.currentTimeMillis()) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 3.3400880035762786, mc.player.posZ, false));
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ, false));
            } else {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 2.3400880035762786, mc.player.posZ, false));
            }
            if (autoDisable.getValue()) {
                disable();
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketExplosion) {
            if (((SPacketExplosion) event.getPacket()).posY > 2.0f && velocityTime <= System.currentTimeMillis()) {
                velocityTime = System.currentTimeMillis() + 3000L;
            }
        }
    }

    public void breakCrystal(BlockPos pos) {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal && entity.getEntityBoundingBox().intersects(new AxisAlignedBB(pos))) {
                mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                break;
            }
        }
    }

    public boolean canPlace(BlockPos pos) {
        return Math.abs(mc.player.posX - (pos.getX() + 0.5f)) < 0.79f && Math.abs(mc.player.posZ - (pos.getZ() + 0.5f)) < 0.79f && mc.world.getBlockState(pos).getMaterial().isReplaceable() && !mc.world.getBlockState(pos.down()).getMaterial().isReplaceable() && mc.world.getBlockState(pos.up().up()).getCollisionBoundingBox(mc.world, pos.up().up()) == null;
    }
}
