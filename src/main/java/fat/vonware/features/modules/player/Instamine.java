package fat.vonware.features.modules.player;
import fat.vonware.event.events.BlockEvent;
import fat.vonware.event.events.PacketEvent;
import fat.vonware.event.events.Render3DEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.InventoryUtil;
import fat.vonware.util.BlockUtil;
import fat.vonware.util.RenderUtil;
import fat.vonware.util.Timer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import java.awt.*;
import net.minecraftforge.fml.common.eventhandler.*;
import net.minecraft.init.*;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.world.*;
import net.minecraft.block.state.*;
import net.minecraft.block.*;

public class Instamine extends Module
{
    private BlockPos renderBlock;
    private BlockPos lastBlock;
    private boolean packetCancel;
    private Timer breakTimer;
    private EnumFacing direction;
    private final Setting<Boolean> silent;
    private final Setting<Boolean> whileEating;
    private final Setting<Integer> delay;
    private final Setting<Integer> red;
    private final Setting<Integer> green;
    private final Setting<Integer> blue;
    private final Setting<Integer> alpha;
    private final Setting<Integer> outlineAlpha;

    public Instamine() {
        super("Instamine", "mine but instant", Module.Category.PLAYER, true, false, false);
        this.packetCancel = false;
        this.breakTimer = new Timer();
        this.silent = (Setting<Boolean>)this.register(new Setting("Silent", true));
        this.whileEating = (Setting<Boolean>)this.register(new Setting("WhileEating", true));
        this.delay = (Setting<Integer>)this.register(new Setting("Delay", 50, 0, 500));
        this.red = (Setting<Integer>)this.register(new Setting("Red", 160, 0, 255));
        this.green = (Setting<Integer>)this.register(new Setting("Green", 0, 0, 255));
        this.blue = (Setting<Integer>)this.register(new Setting("Blue", 0, 0, 255));
        this.alpha = (Setting<Integer>)this.register(new Setting("Alpha", 120, 0, 255));
        this.outlineAlpha = (Setting<Integer>)this.register(new Setting("OutlineAlpha", 150, 0, 255));
    }

    public String getDisplayInfo() {
        return this.silent.getValue() ? "Silent" : "Normal";
    }

    public void onLogout() {
        this.renderBlock = null;
        this.lastBlock = null;
        this.direction = null;
    }

    public void onEnable() {
        this.renderBlock = null;
        this.lastBlock = null;
        this.direction = null;
    }

    public void onDisable() {
        this.renderBlock = null;
        this.lastBlock = null;
        this.direction = null;
    }

    @SubscribeEvent
    public void onRender3D(final Render3DEvent event) {
        if (this.renderBlock != null) {
            final Color color = new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.outlineAlpha.getValue());
            RenderUtil.drawBoxESP(this.renderBlock, color, false, color, 1.2f, true, true, this.alpha.getValue(), true);
        }
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            final CPacketPlayerDigging digPacket = (CPacketPlayerDigging)event.getPacket();
            if (digPacket.getAction() == CPacketPlayerDigging.Action.START_DESTROY_BLOCK && this.packetCancel) {
                event.setCanceled(true);
            }
        }
    }

    public void onTick() {
        if (this.renderBlock == null || !this.breakTimer.passedMs(this.delay.getValue())) {
            try {
                Instamine.mc.playerController.blockHitDelay = 0;
            }
            catch (Exception ex) {}
            return;
        }
        this.breakTimer.reset();
        if (Instamine.mc.world.getBlockState(this.renderBlock).getBlock() == Blocks.AIR) {
            return;
        }
        if (this.whileEating.getValue() || !Instamine.mc.player.isHandActive()) {
            if (!this.silent.getValue()) {
                return;
            }
            final int pickSlot = InventoryUtil.getItemHotbar(Items.DIAMOND_PICKAXE);
            if (pickSlot == -1) {
                return;
            }
            Instamine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(pickSlot));
        }
        Instamine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, this.renderBlock, this.direction));
        if (this.silent.getValue()) {
            Instamine.mc.player.connection.sendPacket((Packet)new CPacketHeldItemChange(Instamine.mc.player.inventory.currentItem));
        }
    }

    @SubscribeEvent
    public void onBlockEvent(final BlockEvent event) {
        if (fullNullCheck()) {
            return;
        }
        if (event.getStage() == 3 && Instamine.mc.playerController.curBlockDamageMP > 0.1f) {
            Instamine.mc.playerController.isHittingBlock = true;
        }
        if (event.getStage() == 4 && BlockUtil.canBreak(event.pos)) {
            Instamine.mc.playerController.isHittingBlock = false;
            if (this.canBreak(event.pos)) {
                if (this.lastBlock == null || event.pos.getX() != this.lastBlock.getX() || event.pos.getY() != this.lastBlock.getY() || event.pos.getZ() != this.lastBlock.getZ()) {
                    this.packetCancel = false;
                    Instamine.mc.player.swingArm(EnumHand.MAIN_HAND);
                    Instamine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.pos, event.facing));
                    this.packetCancel = true;
                }
                else {
                    this.packetCancel = true;
                }
                Instamine.mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.pos, event.facing));
                this.renderBlock = event.pos;
                this.lastBlock = event.pos;
                this.direction = event.facing;
                event.setCanceled(true);
            }
        }
    }

    private boolean canBreak(final BlockPos pos) {
        final IBlockState blockState = Instamine.mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, (World)Instamine.mc.world, pos) != -1.0f;
    }
}
