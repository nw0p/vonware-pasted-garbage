package fat.vonware.features.modules.combat;

import fat.vonware.Vonware;
import fat.vonware.event.events.PacketEvent;
import fat.vonware.util.EntityUtil;
import fat.vonware.util.Timer;
import fat.vonware.util.InventoryUtil;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import net.minecraft.block.BlockEnderChest;
import net.minecraft.block.BlockObsidian;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketBlockBreakAnim;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Blocker extends Module {
    public Setting<Boolean> extend = this.register(new Setting<Boolean>("Extend", true));
    public Setting<Boolean> cornerfeet = this.register(new Setting<Boolean>("CornerFeet", true));
    public Setting<Boolean> face = this.register(new Setting<Boolean>("Face", true));
    public Setting<Boolean> packet = this.register(new Setting<Boolean>("Packet", true));
    public Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));

    private final Map<BlockPos, Long> renderBlocks = new ConcurrentHashMap<>();
    private final Timer renderTimer = new Timer();

    public Blocker() {
        super("Blocker", "Attempts to extend your surround when it's being broken.", Category.COMBAT, true,false,false);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketBlockBreakAnim && EntityUtil.isInHole(mc.player)) {
            SPacketBlockBreakAnim packet = event.getPacket();
            BlockPos pos = packet.getPosition();

            if (mc.world.getBlockState(pos).getBlock() == (Blocks.BEDROCK) || mc.world.getBlockState(pos).getBlock() == (Blocks.AIR))
                return;

            BlockPos playerPos = new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ);
            BlockPos placePos = null;

            if (extend.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos = (playerPos.north().north());

                if (pos.equals(playerPos.east()))
                    placePos = (playerPos.east().east());

                if (pos.equals(playerPos.west()))
                    placePos = (playerPos.west().west());

                if (pos.equals(playerPos.south()))
                    placePos = (playerPos.south().south());
            }
            if (pos.equals(playerPos.north())) {
                if (cornerfeet.getValue()) placePos = (playerPos.north().west());
                if (cornerfeet.getValue()) placePos = (playerPos.north().east());
            }
            if (pos.equals(playerPos.east())) {
                if (cornerfeet.getValue()) placePos = (playerPos.east().south());
                if (cornerfeet.getValue()) placePos = (playerPos.east().north());
            }
            if (pos.equals(playerPos.west())) {
                if (cornerfeet.getValue()) placePos = (playerPos.west().south());
                if (cornerfeet.getValue()) placePos = (playerPos.west().north());
            }
            if (pos.equals(playerPos.south())) {
                if (cornerfeet.getValue()) placePos = (playerPos.south().west());
                if (cornerfeet.getValue()) placePos = (playerPos.south().east());
            }


            if (face.getValue()) {
                if (pos.equals(playerPos.north()))
                    placePos = (playerPos.north().add(0, 1, 0));

                if (pos.equals(playerPos.east()))
                    placePos = (playerPos.east().add(0, 1, 0));

                if (pos.equals(playerPos.west()))
                    placePos = (playerPos.west().add(0, 1, 0));

                if (pos.equals(playerPos.south()))
                    placePos = (playerPos.south().add(0, 1, 0));
            }

            if (placePos != null) {
                placeBlock(placePos);
            }
        }

        if (event.getPacket() instanceof SPacketBlockChange) {
            if (renderBlocks.containsKey(((SPacketBlockChange) event.getPacket()).getBlockPosition())) {
                renderTimer.reset();

                if (((SPacketBlockChange) event.getPacket()).getBlockState().getBlock() != Blocks.AIR && renderTimer.passedMs(400)) {
                    renderBlocks.remove(((SPacketBlockChange) event.getPacket()).getBlockPosition());
                }
            }
        }
    }

    private void placeBlock(BlockPos pos){
        if (!mc.world.isAirBlock(pos)) return;

        int oldSlot = mc.player.inventory.currentItem;

        int obbySlot = InventoryUtil.findHotbarBlock(BlockObsidian.class);
        int eChestSlot = InventoryUtil.findHotbarBlock(BlockEnderChest.class);

        if (obbySlot == -1 && eChestSlot == 1) return;

        for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(pos))) {
            if (entity instanceof EntityEnderCrystal) {
                mc.player.connection.sendPacket(new CPacketUseEntity(entity));
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
            }
        }

        mc.player.inventory.currentItem = obbySlot == -1 ? eChestSlot : obbySlot;

        mc.playerController.updateController();
        renderBlocks.put(pos, System.currentTimeMillis());

        Vonware.interactionManager.placeBlock(pos, rotate.getValue(), packet.getValue(), true);

        if (mc.player.inventory.currentItem != oldSlot) {
            mc.player.inventory.currentItem = oldSlot;
            mc.playerController.updateController();
        }

        mc.player.inventory.currentItem = oldSlot;
    }
}