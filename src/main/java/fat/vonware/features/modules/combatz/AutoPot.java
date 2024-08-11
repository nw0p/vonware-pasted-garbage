
package fat.vonware.features.modules.combatz;

import com.google.common.collect.Sets;
import com.mojang.realmsclient.gui.ChatFormatting;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.InventoryUtil;
import fat.vonware.util.Timer;
import net.minecraft.block.Block;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPotion;
import net.minecraft.item.ItemSplashPotion;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionUtils;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;

import java.util.Objects;
import java.util.Set;

public class AutoPot extends Module {
    private static final Set<Block> BAD_BLOCKS;
    protected final Setting<Pages> pages;
    protected final Setting<Boolean> heal;
    protected final Setting<Integer> healthThreshold;
    protected final Setting<Integer> healthDelay;
    protected final Setting<Boolean> speed;
    protected final Setting<Integer> speedDelay;
    protected final Setting<Boolean> refill;
    protected final Setting<refillModes> refillMode;
    protected final Setting<Boolean> switchBack;
    protected final Setting<Boolean> onlyGround;
    protected final Setting<RotationMode> rotationMode;
    protected final Setting<Integer> customPitch;
    protected final Setting<Boolean> movementCorrect;
    protected final Setting<Boolean> extraMoveCorrect;
    protected final Setting<Integer> extraMoveValue;
    protected final Setting<AirRots> airRots;
    protected final Setting<Boolean> redoFromCamera;
    private final Timer healthStopWatch;
    private final Timer speedStopWatch;
    int incrementalMsgId;

    public AutoPot() {
        super("AutoPot", "black", Category.COMBATZ, true, false, false);
        this.pages = this.register(new Setting("Page", AutoPot.Pages.Health));
        this.heal = this.register(new Setting("HealPot", true));
        this.healthThreshold = this.register(new Setting("HealthThreshold", 15, 1, 19));
        this.healthDelay = this.register(new Setting("ThrowDelay", 100, 0, 1000));
        this.speed = this.register(new Setting("SpeedPot", true));
        this.speedDelay = this.register(new Setting("SpeedDelay", 100, 0, 1000));
        this.refill = this.register(new Setting("Refill", false));
        this.refillMode = this.register(new Setting("Refill Mode", AutoPot.refillModes.Quick));
        this.switchBack = this.register(new Setting("SwitchBack", true));
        this.onlyGround = this.register(new Setting("Only Ground", false));
        this.rotationMode = this.register(new Setting("Rotation Style", AutoPot.RotationMode.Normal));
        this.customPitch = this.register(new Setting("Custom Pitch", 90, -90, 90));
        this.movementCorrect = this.register(new Setting("Movement Correction", false));
        this.extraMoveCorrect = this.register(new Setting("Extra Move Correction", false));
        this.extraMoveValue = this.register(new Setting("Extra Move Value", 1, 1, 10));
        this.airRots = this.register(new Setting("Air Rotations", AutoPot.AirRots.Add));
        this.redoFromCamera = this.register(new Setting("Redo-From-Camera", false));
        this.healthStopWatch = new Timer();
        this.speedStopWatch = new Timer();
        this.incrementalMsgId = 100000;
    }

    public void onEnable() {
        this.resetStopWatches();
    }

    public void onDisable() {
        this.resetStopWatches();
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        update();
    }

    public String getDisplayInfo() {
        if (!mc.player.isPotionActive(MobEffects.SPEED)) {
            return "Health: " + InventoryUtil.getCount(Item.getItemById(6));
        } else {
            int remainingTime = ((PotionEffect) Objects.requireNonNull(mc.player.getActivePotionEffect(MobEffects.SPEED))).getDuration();
            return "§bSpeed: " + remainingTime / 20 + "s";
        }
    }

    private void update() {
        int i;
        if (this.heal.getValue() && (mc.player.onGround || !this.onlyGround.getValue()) && this.healthStopWatch.passed((long)(Integer)this.healthDelay.getValue()) && mc.player.getHealth() < (float)(Integer)this.healthThreshold.getValue()) {
            i = this.findPotions(MobEffects.INSTANT_HEALTH, 2);
            if (i == -1) {
                i = this.findPotions(MobEffects.INSTANT_HEALTH, 1);
            }

            if (i != -1) {
                this.switchThrowPot(i);
                this.healthStopWatch.reset();
            }
        }

        if (this.speed.getValue() && this.speedStopWatch.passed((long)(Integer)this.speedDelay.getValue()) && mc.player.getActivePotionEffects().stream().noneMatch((potionEffect) -> {
            return potionEffect.getPotion() == MobEffects.SPEED;
        })) {
            i = this.findPotions(MobEffects.SPEED, 2);
            if (i == -1) {
                i = this.findPotions(MobEffects.SPEED, 1);
            }

            if (i != -1) {
                this.switchThrowPot(i);
                this.speedStopWatch.reset();
            }
        }
        if (this.refill.getValue()) {
            ItemStack stacks;
            ItemPotion itemMine;
            switch ((refillModes)this.refillMode.getValue()) {
                case Pickup:
                    if (mc.player.inventory.getCurrentItem().getItem() == Items.SPLASH_POTION) {
                        for(i = 0; i < 45; ++i) {
                            stacks = mc.player.openContainer.getSlot(i).getStack();
                            if (!stacks.isEmpty()) {
                                itemMine = Items.SPLASH_POTION;
                                if (!mc.player.getHeldItemMainhand().isEmpty() && !(mc.currentScreen instanceof GuiChest) && stacks.getItem() == itemMine) {
                                    mc.playerController.windowClick(0, i, 1, ClickType.PICKUP, mc.player);
                                    mc.playerController.windowClick(0, 36, 1, ClickType.PICKUP, mc.player);
                                }
                            }
                        }
                    }
                    break;
                case Quick:
                    if (mc.player.inventory.getCurrentItem().getItem() == Items.SPLASH_POTION) {
                        for(i = 0; i < 45; ++i) {
                            stacks = mc.player.openContainer.getSlot(i).getStack();
                            if (!stacks.isEmpty()) {
                                itemMine = Items.SPLASH_POTION;
                                if (!mc.player.getHeldItemMainhand().isEmpty() && !(mc.currentScreen instanceof GuiChest) && stacks.getItem() == itemMine) {
                                    mc.playerController.windowClick(0, i, 1, ClickType.QUICK_MOVE, mc.player);
                                    mc.playerController.windowClick(0, 36, 1, ClickType.QUICK_MOVE, mc.player);
                                }
                            }
                        }
                    }
                    break;
                case Swap:
                    if (mc.player.inventory.getCurrentItem().getItem() == Items.SPLASH_POTION) {
                        for(i = 0; i < 45; ++i) {
                            stacks = mc.player.openContainer.getSlot(i).getStack();
                            if (!stacks.isEmpty()) {
                                itemMine = Items.SPLASH_POTION;
                                if (!mc.player.getHeldItemMainhand().isEmpty() && !(mc.currentScreen instanceof GuiChest) && stacks.getItem() == itemMine) {
                                    mc.playerController.windowClick(0, i, 1, ClickType.SWAP, mc.player);
                                    mc.playerController.windowClick(0, 36, 1, ClickType.SWAP, mc.player);
                                }
                            }
                        }
                    }
            }
        }

    }

    private void switchThrowPot(int slot) {
        int oldSlot = mc.player.inventory.currentItem;
        float[] rotation = this.getRotationStyle();
        InventoryUtil.switchTo(slot);
        if (rotation != null) {
            ++this.incrementalMsgId;
            if (mc.player.movementInput.forwardKeyDown && !mc.player.movementInput.leftKeyDown && !mc.player.movementInput.rightKeyDown) {
                rotation[0] -= 5.0F;
                if (mc.player.isSprinting()) {
                    rotation[0] -= 2.0F;
                }

                if (this.extraMoveCorrect.getValue()) {
                    rotation[0] -= (float) (Integer) this.extraMoveValue.getValue();
                }

                if (!mc.player.onGround && !this.onlyGround.getValue()) {
                    if (this.airRots.getValue() == AutoPot.AirRots.Add) {
                        rotation[0] -= 2.0F;
                    } else if (this.airRots.getValue() == AutoPot.AirRots.NoRotate) {
                        rotation[0] = mc.player.cameraPitch;
                        rotation[1] = mc.player.cameraYaw;
                    } else if (this.airRots.getValue() == AutoPot.AirRots.Redo) {
                        rotation[0] = mc.player.cameraPitch + 15.0F;
                    }
                }
            }
            mc.player.connection.sendPacket(new CPacketPlayer.Rotation(rotation[1], rotation[0], mc.player.onGround));
            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
            InventoryUtil.switchTo(oldSlot);
        }
        InventoryUtil.switchTo(oldSlot);
    }

    public float[] getRotationStyle() {
        if (this.rotationMode.getValue() == AutoPot.RotationMode.Normal) {
            return this.findBestRotation();
        } else if (this.rotationMode.getValue() == AutoPot.RotationMode.Simple) {
            return new float[]{90.0F, mc.player.cameraYaw};
        } else {
            return this.rotationMode.getValue() == AutoPot.RotationMode.Custom ? new float[]{(float)(Integer)this.customPitch.getValue(), mc.player.cameraYaw} : null;
        }
    }

    public float[] findBestRotation() {
        if (!BAD_BLOCKS.contains(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY, mc.player.posZ)).getBlock())) {
            return new float[]{90.0F, mc.player.rotationYaw};
        } else if (!BAD_BLOCKS.contains(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ)).getBlock())) {
            return new float[]{90.0F, mc.player.rotationYaw};
        } else {
            return !BAD_BLOCKS.contains(mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ)).getBlock()) ? new float[]{-90.0F, mc.player.rotationYaw} : null;
        }
    }

    private int findPotions(Potion effect, int amplifier) {
        int slot = InventoryUtil.findInHotbar((itemStack) -> {
            return this.potCheck(itemStack, effect, amplifier);
        });
        return slot;
    }

    private boolean potCheck(ItemStack itemStack, Potion effect, int amplifier) {
        boolean isSplashPot = itemStack.getItem() instanceof ItemSplashPotion;
        boolean hasCorrectEffect = PotionUtils.getEffectsFromStack(itemStack).stream().anyMatch((potEffect) -> {
            return potEffect.getPotion() == effect && potEffect.getAmplifier() == amplifier;
        });
        return isSplashPot && hasCorrectEffect;
    }

    private void resetStopWatches() {
        this.healthStopWatch.reset();
        this.speedStopWatch.reset();
    }

    static {
        BAD_BLOCKS = Sets.newHashSet(new Block[]{Blocks.AIR, Blocks.WATER, Blocks.LAVA, Blocks.PORTAL, Blocks.END_PORTAL});
    }

    public static enum refillModes {
        Pickup,
        Quick,
        Swap;

        private refillModes() {
        }
    }

    public static enum RotationMode {
        Normal,
        Custom,
        Simple;

        private RotationMode() {
        }
    }

    private static enum AirRots {
        Add,
        Redo,
        NoRotate;

        private AirRots() {
        }
    }

    private static enum Pages {
        Speed,
        Health,
        Misc,
        Rotation;

        private Pages() {
        }
    }
}
