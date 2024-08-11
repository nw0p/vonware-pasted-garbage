package fat.vonware.features.modules.combat;

import fat.vonware.features.modules.*;
import fat.vonware.features.setting.*;
import fat.vonware.util.Timer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.math.*;
import net.minecraft.init.*;
import fat.vonware.util.*;
import net.minecraft.block.*;
import net.minecraft.util.*;

public class AntiRegear extends Module
{
    private static AntiRegear INSTANCE;
    public final Setting<Integer> range = this.register(new Setting<Integer>("Range", 5, 1, 8));
    public final Setting<Boolean> pickOnly = this.register(new Setting<Boolean>("Pickaxe Only", true));
    public final Setting<Integer> delay = this.register(new Setting<Integer>("HitDelay", 3000, 0, 5000));
    public Timer timer = new Timer();


    public AntiRegear() {
        super("AntiRegear", "Breaks shulkers", Module.Category.COMBAT, true, false, false);
        this.setInstance();
    }

    public static AntiRegear getInstance() {
        if (AntiRegear.INSTANCE == null) {
            AntiRegear.INSTANCE = new AntiRegear();
        }
        return AntiRegear.INSTANCE;
    }

    private void setInstance() {
        AntiRegear.INSTANCE = this;
    }
    @Override
    public void onTick() {
        if (AntiRegear.nullCheck()||!timer.passedMs(delay.getValue()))

            return;
        final int mainSlot = AntiRegear.mc.player.inventory.currentItem;
        final int slotPick = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
        if (slotPick == -1) {
            return;
        }
        if (this.pickOnly.getValue().booleanValue() && !(AntiRegear.mc.player.inventory.getCurrentItem().getItem() instanceof ItemPickaxe)) {
            return;
        }
        for (final BlockPos blockPos : this.breakPos(this.range.getValue())) {

            if (BlockUtil.getMineDistance(blockPos) > MathUtil.square(this.range.getValue())) {
                continue;
            }
            if (AntiRegear.mc.world.getBlockState(blockPos).getBlock() instanceof BlockShulkerBox) {
                AntiRegear.mc.player.inventory.currentItem = slotPick;
                AntiRegear.mc.player.swingArm(EnumHand.MAIN_HAND);
                AntiRegear.mc.playerController.onPlayerDamageBlock(blockPos, BlockUtil.getRayTraceFacing(blockPos));
                AntiRegear.mc.player.inventory.currentItem = mainSlot;
                return;
            }
        }
    }

    private NonNullList<BlockPos> breakPos(final float placeRange) {
        final NonNullList<BlockPos> positions = NonNullList.create();

        int playerPosX = (int) AntiRegear.mc.player.posX;
        int playerPosY = (int) AntiRegear.mc.player.posY;
        int playerPosZ = (int) AntiRegear.mc.player.posZ;

        positions.addAll(BlockUtil.getSphere(new BlockPos(playerPosX, playerPosY, playerPosZ), (int)Math.floor(placeRange), 0, false, true, 0));

        return positions;
    }

    static {
        AntiRegear.INSTANCE = new AntiRegear();
    }
}
