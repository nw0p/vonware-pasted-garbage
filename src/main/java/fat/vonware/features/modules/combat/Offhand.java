package fat.vonware.features.modules.combat;

import fat.vonware.features.modules.*;
import fat.vonware.features.setting.*;
import java.util.concurrent.*;

import fat.vonware.util.Timer;
import net.minecraft.network.Packet;
import net.minecraft.util.*;
import net.minecraft.init.*;
import net.minecraft.entity.player.*;
import net.minecraft.world.*;
import net.minecraftforge.fml.common.eventhandler.*;
import org.lwjgl.input.*;
import fat.vonware.event.events.*;
import net.minecraft.network.play.client.*;

import java.util.*;
import fat.vonware.util.*;
import net.minecraft.entity.*;
import net.minecraft.inventory.*;
import net.minecraft.client.gui.inventory.*;
import net.minecraft.item.*;
import net.minecraft.block.*;

public class Offhand extends Module
{
    private static Offhand instance;
    private final Queue<InventoryUtil.Task> taskList;
    private final Timer timer;
    private final Timer secondTimer;
    public Setting<Boolean> crystal;
    public Setting<Float> crystalHealth;
    public Setting<Float> crystalHoleHealth;
    public Setting<Boolean> bed;
    public Setting<Float> bedHealth;
    public Setting<Boolean> gapple;
    public Setting<Float> gappleHealth;
    public Setting<Boolean> armorCheck;
    public Setting<Integer> actions;
    public Setting<Boolean> fallDistance;
    public Setting<Float> Height;
    public Mode2 currentMode;
    public int totems;
    public int crystals;
    public int gapples;
    public int beds;
    public int lastTotemSlot;
    public int lastGappleSlot;
    public int lastCrystalSlot;
    public int lastBedSlot;
    public int lastObbySlot;
    public int lastWebSlot;
    public boolean holdingCrystal;
    public boolean holdingBed;
    public boolean holdingTotem;
    public boolean holdingGapple;
    public boolean didSwitchThisTick;
    private boolean second;
    private boolean switchedForHealthReason;

    public Offhand() {
        super("Offhand", "Allows you to switch up your ", Category.COMBAT, true, false, false);
        this.taskList = new ConcurrentLinkedQueue<InventoryUtil.Task>();
        this.timer = new Timer();
        this.secondTimer = new Timer();
        this.crystal = (Setting<Boolean>)this.register(new Setting("Crystal", true));
        this.fallDistance = (Setting<Boolean>)this.register(new Setting("FallDistance", false, "Toggles whether falling will switch to totem."));
        this.Height = (Setting<Float>)this.register(new Setting("Height", 0.0f, 0.0f, 30.0f, v -> this.fallDistance.getValue(), "Fall distance to switch to totem."));
        this.crystalHealth = (Setting<Float>)this.register(new Setting("CrystalHP", 13.0f, 0.1f, 36.0f, v -> this.crystal.getValue()));
        this.crystalHoleHealth = (Setting<Float>)this.register(new Setting("CrystalHoleHP", 3.5f, 0.1f, 36.0f, v -> this.crystal.getValue()));
        this.bed = (Setting<Boolean>)this.register(new Setting("Bed", true, v -> !this.crystal.getValue()));
        this.bedHealth = (Setting<Float>)this.register(new Setting("BedHP", 13.0f, 0.1f, 36.0f, v -> this.bed.getValue()));
        this.gapple = (Setting<Boolean>)this.register(new Setting("Gapple", true));
        this.gappleHealth = (Setting<Float>)this.register(new Setting("GappleHP", 8.0f, 0.1f, 36.0f, v -> this.gapple.getValue()));
        this.armorCheck = (Setting<Boolean>)this.register(new Setting("ArmorCheck", true));
        this.actions = (Setting<Integer>)this.register(new Setting("Packets", 4, 1, 4));
        this.currentMode = Mode2.TOTEMS;
        this.totems = 0;
        this.crystals = 0;
        this.gapples = 0;
        this.beds = 0;
        this.lastTotemSlot = -1;
        this.lastGappleSlot = -1;
        this.lastCrystalSlot = -1;
        this.lastBedSlot = -1;
        this.lastObbySlot = -1;
        this.lastWebSlot = -1;
        this.holdingCrystal = false;
        this.holdingBed = false;
        this.holdingTotem = false;
        this.holdingGapple = false;
        this.didSwitchThisTick = false;
        this.second = false;
        this.switchedForHealthReason = false;
        Offhand.instance = this;
    }

    public static Offhand getInstance() {
        if (Offhand.instance == null) {
            Offhand.instance = new Offhand();
        }
        return Offhand.instance;
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(final ProcessRightClickBlockEvent event) {
        if (event.hand == EnumHand.MAIN_HAND && event.stack.getItem() == Items.END_CRYSTAL && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.objectMouseOver != null && event.pos == Offhand.mc.objectMouseOver.getBlockPos()) {
            event.setCanceled(true);
            Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
            Offhand.mc.playerController.processRightClick((EntityPlayer) Offhand.mc.player, (World) Offhand.mc.world, EnumHand.OFF_HAND);
        }
    }

    @Override
    public void onUpdate() {
        if (this.timer.passedMs(50L)) {
            if (Offhand.mc.player != null && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Mouse.isButtonDown(1)) {
                Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
                Offhand.mc.gameSettings.keyBindUseItem.pressed = Mouse.isButtonDown(1);
            }
        }
        else if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL) {
            Offhand.mc.gameSettings.keyBindUseItem.pressed = false;
        }
        if (nullCheck()) {
            return;
        }
        this.doOffhand();
        if (this.secondTimer.passedMs(50L) && this.second) {
            this.second = false;
            this.timer.reset();
        }
    }

    @SubscribeEvent
    public void onPacketSend(final PacketEvent.Send event) {
        if (!fullNullCheck() && Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE && Offhand.mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown()) {
            if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
                final CPacketPlayerTryUseItemOnBlock packet2 = (CPacketPlayerTryUseItemOnBlock)event.getPacket();
                if (packet2.getHand() == EnumHand.MAIN_HAND) {
                    if (this.timer.passedMs(50L)) {
                        Offhand.mc.player.setActiveHand(EnumHand.OFF_HAND);
                        Offhand.mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.OFF_HAND));
                    }
                    event.setCanceled(true);
                }
            }
            else if (event.getPacket() instanceof CPacketPlayerTryUseItem && ((CPacketPlayerTryUseItem)event.getPacket()).getHand() == EnumHand.OFF_HAND && !this.timer.passedMs(50L)) {
                event.setCanceled(true);
            }
        }
    }

    @Override
    public String getDisplayInfo() {
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL) {
            return "Crystals";
        }
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING) {
            return "Totems";
        }
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE) {
            return "Gapples";
        }
        if (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.BED) {
            return "Beds";
        }
        return null;
    }

    public void doOffhand() {
        this.didSwitchThisTick = false;
        this.holdingCrystal = (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL);
        this.holdingTotem = (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING);
        this.holdingGapple = (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.GOLDEN_APPLE);
        this.holdingBed = (Offhand.mc.player.getHeldItemOffhand().getItem() == Items.BED);
        this.totems = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        if (this.holdingTotem) {
            this.totems += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.TOTEM_OF_UNDYING).mapToInt(ItemStack::getCount).sum();
        }
        this.crystals = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        if (this.holdingCrystal) {
            this.crystals += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.END_CRYSTAL).mapToInt(ItemStack::getCount).sum();
        }
        this.beds = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.BED).mapToInt(ItemStack::getCount).sum();
        if (this.holdingBed) {
            this.beds += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.BED).mapToInt(ItemStack::getCount).sum();
        }
        this.gapples = Offhand.mc.player.inventory.mainInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        if (this.holdingGapple) {
            this.gapples += Offhand.mc.player.inventory.offHandInventory.stream().filter(itemStack -> itemStack.getItem() == Items.GOLDEN_APPLE).mapToInt(ItemStack::getCount).sum();
        }
        this.doSwitch();
    }

    public void doSwitch() {
        this.currentMode = Mode2.TOTEMS;
        if (this.gapple.getValue() && Offhand.mc.player.getHeldItemMainhand().getItem() instanceof ItemSword && Offhand.mc.gameSettings.keyBindUseItem.isKeyDown() && !(Offhand.mc.world.getBlockState(Objects.requireNonNull(Offhand.mc.player.rayTrace(5.0, Offhand.mc.getRenderPartialTicks())).getBlockPos()).getBlock() instanceof BlockContainer) && EntityUtil.getHealth((Entity) Offhand.mc.player, true) > this.gappleHealth.getValue()) {
            this.currentMode = Mode2.GAPPLES;
        }
        else if (this.currentMode != Mode2.CRYSTALS && this.crystal.getValue() && ((EntityUtil.isSafe((Entity) Offhand.mc.player) && EntityUtil.getHealth((Entity) Offhand.mc.player, true) > this.crystalHoleHealth.getValue()) || EntityUtil.getHealth((Entity) Offhand.mc.player, true) > this.crystalHealth.getValue())) {
            this.currentMode = Mode2.CRYSTALS;
        }
        else if (this.currentMode != Mode2.BEDS && this.bed.getValue() && EntityUtil.getHealth((Entity) Offhand.mc.player, true) > this.bedHealth.getValue()) {
            this.currentMode = Mode2.BEDS;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.crystals == 0) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.BEDS && this.beds == 0) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.CRYSTALS && ((!EntityUtil.isSafe((Entity) Offhand.mc.player) && EntityUtil.getHealth((Entity) Offhand.mc.player, true) <= this.crystalHealth.getValue()) || EntityUtil.getHealth((Entity) Offhand.mc.player, true) <= this.crystalHoleHealth.getValue())) {
            if (this.currentMode == Mode2.CRYSTALS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.BEDS && EntityUtil.getHealth((Entity) Offhand.mc.player, true) <= this.bedHealth.getValue()) {
            if (this.currentMode == Mode2.BEDS) {
                this.switchedForHealthReason = true;
            }
            this.setMode(Mode2.TOTEMS);
        }
        if (this.switchedForHealthReason && ((EntityUtil.isSafe((Entity) Offhand.mc.player) && EntityUtil.getHealth((Entity) Offhand.mc.player, true) > this.crystalHoleHealth.getValue()) || EntityUtil.getHealth((Entity) Offhand.mc.player, true) > this.crystalHealth.getValue())) {
            this.setMode(Mode2.CRYSTALS);
            this.switchedForHealthReason = false;
        }
        if (this.switchedForHealthReason && EntityUtil.getHealth((Entity) Offhand.mc.player, true) > this.bedHealth.getValue()) {
            this.setMode(Mode2.BEDS);
            this.switchedForHealthReason = false;
        }
        if (this.currentMode == Mode2.CRYSTALS && this.armorCheck.getValue() && (Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR)) {
            this.setMode(Mode2.TOTEMS);
        }
        if (this.currentMode == Mode2.BEDS && this.armorCheck.getValue() && (Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Items.AIR || Offhand.mc.player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Items.AIR)) {
            this.setMode(Mode2.TOTEMS);
        }
        if ((this.currentMode == Offhand.Mode2.CRYSTALS || this.currentMode == Offhand.Mode2.GAPPLES) && Offhand.mc.player.fallDistance > this.Height.getValue() && this.fallDistance.getValue()) {
            this.setMode(Offhand.Mode2.TOTEMS);
        }
        if (Offhand.mc.currentScreen instanceof GuiContainer && !(Offhand.mc.currentScreen instanceof GuiInventory)) {
            return;
        }
        final Item currentOffhandItem = Offhand.mc.player.getHeldItemOffhand().getItem();
        Label_1142: {
            switch (this.currentMode) {
                case TOTEMS: {
                    if (this.totems <= 0) {
                        break Label_1142;
                    }
                    if (this.holdingTotem) {
                        break Label_1142;
                    }
                    this.lastTotemSlot = InventoryUtil.findItemInventorySlot(Items.TOTEM_OF_UNDYING, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastTotemSlot);
                    this.putItemInOffhand(this.lastTotemSlot, lastSlot);
                    break Label_1142;
                }
                case GAPPLES: {
                    if (this.gapples <= 0) {
                        break Label_1142;
                    }
                    if (this.holdingGapple) {
                        break Label_1142;
                    }
                    this.lastGappleSlot = InventoryUtil.findItemInventorySlot(Items.GOLDEN_APPLE, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastGappleSlot);
                    this.putItemInOffhand(this.lastGappleSlot, lastSlot);
                    break Label_1142;
                }
                case BEDS: {
                    if (this.beds <= 0) {
                        break Label_1142;
                    }
                    if (this.holdingBed) {
                        break Label_1142;
                    }
                    this.lastBedSlot = InventoryUtil.findItemInventorySlot(Items.BED, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastBedSlot);
                    this.putItemInOffhand(this.lastBedSlot, lastSlot);
                    break;
                }
            }
            if (this.crystals > 0) {
                if (!this.holdingCrystal) {
                    this.lastCrystalSlot = InventoryUtil.findItemInventorySlot(Items.END_CRYSTAL, false);
                    final int lastSlot = this.getLastSlot(currentOffhandItem, this.lastCrystalSlot);
                    this.putItemInOffhand(this.lastCrystalSlot, lastSlot);
                }
            }
        }
        for (int i = 0; i < this.actions.getValue(); ++i) {
            final InventoryUtil.Task task = this.taskList.poll();
            if (task != null) {
                task.run();
                if (task.isSwitching()) {
                    this.didSwitchThisTick = true;
                }
            }
        }
    }

    private int getLastSlot(final Item item, final int slotIn) {
        if (item == Items.END_CRYSTAL) {
            return this.lastCrystalSlot;
        }
        if (item == Items.GOLDEN_APPLE) {
            return this.lastGappleSlot;
        }
        if (item == Items.TOTEM_OF_UNDYING) {
            return this.lastTotemSlot;
        }
        if (InventoryUtil.isBlock(item, BlockObsidian.class)) {
            return this.lastObbySlot;
        }
        if (InventoryUtil.isBlock(item, BlockWeb.class)) {
            return this.lastWebSlot;
        }
        if (item == Items.AIR) {
            return -1;
        }
        return slotIn;
    }

    private void putItemInOffhand(final int slotIn, final int slotOut) {
        if (slotIn != -1 && this.taskList.isEmpty()) {
            this.taskList.add(new InventoryUtil.Task(slotIn));
            this.taskList.add(new InventoryUtil.Task(45));
            this.taskList.add(new InventoryUtil.Task(slotOut));
            this.taskList.add(new InventoryUtil.Task());
        }
    }

    public void setMode(final Mode2 mode) {
        this.currentMode = ((this.currentMode == mode) ? Mode2.TOTEMS : mode);
    }

    public enum Mode2
    {
        TOTEMS,
        GAPPLES,
        CRYSTALS,
        BEDS;
    }
}
