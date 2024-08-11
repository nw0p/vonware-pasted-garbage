package fat.vonware.features.modules.combat;

import fat.vonware.features.command.Command;
import fat.vonware.util.*;
import fat.vonware.util.holeesp.HoleUtil;
import com.mojang.realmsclient.gui.ChatFormatting;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import net.minecraft.block.BlockAir;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.function.ToDoubleFunction;

public class AutoCity extends Module
{

    public Setting<String> type = this.register(new Setting<String>("RotationType", "Packet", "Packet | Normal"));
    public final Setting<Boolean> rotate = this.register(new Setting<Boolean>("Rotate", false));
    public final Setting<Boolean> burrow = this.register(new Setting<Boolean>("Burrow", true));
    public final Setting<Boolean> NoSwing = this.register(new Setting<Boolean>("NoSwing", true));
    public final Setting<Boolean> move = this.register(new Setting<Boolean>("Move Check", false));
    public final Setting<Boolean> holeCheck = this.register(new Setting<Boolean>("Hole Check", false));
    public final Setting<Boolean> pickCheck = this.register(new Setting<Boolean>("Pickaxe Check", true));
    public final Setting<Float> targetRange = this.register(new Setting("TargetRange", 10f, 2f, 15f));
    public final Setting<Float> resetRange = this.register(new Setting("ResetRange",  3f, 1f, 6f));
    /*protected final Setting<RotationType> type;
    public enum RotationType {
        Packet,
        Normal
    }*/


    BlockPos mining;
    long startTime;
    EntityPlayer target;
    int old;
    boolean swapBack;
    public AutoCity() {
        super("AutoCity", "Automatically mines city blocks of opponents", Module.Category.COMBAT, true, false, false);
        //this.type = this.register(new Setting("Outline Mode", RotationType.Packet, v -> rotate.getValue()));
    }

    @Override
    public void onEnable()
    {
        startTime = 0L;
        old = 1;
        swapBack = false;
    }

    @Override
    public String getDisplayInfo()
    {
        if (mc.player == null)
        {
            return "";
        }
        if (target != null)
        {
            return (target).getDisplayNameString().toLowerCase();
        }
        return ChatFormatting.RED + "none" + ChatFormatting.RESET;

    }
    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (pickCheck.getValue())
        {
            int i = InventoryUtil.findHotbar(ItemPickaxe.class);
            if (i == -1)
            {
                Command.sendMessage(ChatFormatting.GRAY + "No pickaxe found! Disabling AutoCity.");
                disable();
            }
        }
        if(move.getValue() && MovementUtil.anyMovementKeys())
        {
            return;
        }
        if ((target = EntityUtil.getTarget((targetRange.getValue()).doubleValue())) == null) {
            return;
        }
        if (mining != null) {
            if (mc.world.getBlockState(mining).getBlock() instanceof BlockAir) {
                mining = null;
                return;
            }
            if (holeCheck.getValue() && !HoleUtil.isHole(EntityUtil.getOtherPlayerPos(target)) && !EntityUtil.isBurrow(target)) {
                mining = null;
                return;
            }
            if (mc.player.getDistanceSq(mining) > MathUtil.square(resetRange.getValue().intValue()))
            {
                mining = null;
            }
        }
        if (mining == null && getBurrowBlock(target) != null && burrow.getValue())
        {
            mine(getBurrowBlock(target));
        }
        else if (mining == null && HoleUtil.isHole(EntityUtil.getOtherPlayerPos(target)) && getCityBlockSurround(target) != null) {
            mine(getCityBlockSurround(target));
        }
    }

    private void mine(BlockPos blockPos) {
        if (mc.player.getDistanceSq(blockPos) > MathUtil.square(resetRange.getValue()))
        {
            return;
        }
        if (rotate.getValue())
        {
            float[] rotations = RotationUtil.getRotations(blockPos);
            RotationUtil.doRotation(type.getValue(), rotations);
        }
        mc.playerController.onPlayerDamageBlock(blockPos, EnumFacing.UP);
        if (!NoSwing.getValue()) {
            mc.player.swingArm(EnumHand.MAIN_HAND);
        }
        mining = blockPos;
        startTime = System.currentTimeMillis();
    }

    @Override
    public void onDisable() {
        mining = null;
        swapBack = false;
    }

    public static List<BlockPos> getSurroundBlocks(EntityPlayer player) {
        if (player == null) {
            return null;
        }
        List<BlockPos> positions = new ArrayList<>();
        for (EnumFacing direction : EnumFacing.values()) {
            if (direction != EnumFacing.UP) {
                if (direction != EnumFacing.DOWN) {
                    BlockPos pos = EntityUtil.getOtherPlayerPos(player).offset(direction);
                    if (mc.world.getBlockState(pos).getBlock() == Blocks.OBSIDIAN && canCityBlock(pos, direction)) {
                        positions.add(pos);
                    }
                }
            }
        }
        return positions;
    }
    public BlockPos getBurrowBlock(EntityPlayer player)
    {
        if (player == null)
        {
            return null;
        }
        BlockPos blockPos = new BlockPos(player.posX, player.posY, player.posZ);
        if (mc.world.getBlockState(blockPos).getBlock().equals(Blocks.OBSIDIAN) || mc.world.getBlockState(blockPos).getBlock().equals(Blocks.ENDER_CHEST))
        {
            return blockPos;
        }
        else
        {
            return null;
        }
    }

    public static BlockPos getCityBlockSurround(EntityPlayer player) {
        List<BlockPos> posList = getSurroundBlocks(player);
        posList.sort(Comparator.comparingDouble((ToDoubleFunction<? super BlockPos>)MathUtil::distanceTo));
        return posList.isEmpty() ? null : posList.get(0);
    }
    public static boolean canCityBlock(BlockPos blockPos, EnumFacing direction) {
        return mc.world.getBlockState(blockPos.up()).getBlock() == Blocks.AIR || (mc.world.getBlockState(blockPos.offset(direction)).getBlock() == Blocks.AIR && mc.world.getBlockState(blockPos.offset(direction).up()).getBlock() == Blocks.AIR && (mc.world.getBlockState(blockPos.offset(direction).down()).getBlock() == Blocks.OBSIDIAN || mc.world.getBlockState(blockPos.offset(direction).down()).getBlock() == Blocks.BEDROCK));
    }
}
