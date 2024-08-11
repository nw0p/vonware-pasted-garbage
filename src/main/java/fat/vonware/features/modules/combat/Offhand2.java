package fat.vonware.features.modules.combat;

import java.util.*;

import fat.vonware.features.setting.Setting;
import fat.vonware.util.EntityUtil;
import fat.vonware.util.InventoryUtil;
import fat.vonware.features.modules.Module;
import fat.vonware.util.PositionUtil;
import fat.vonware.util.holeesp.HoleUtil;
import net.minecraft.item.*;
import net.minecraft.init.*;
import net.minecraft.entity.player.*;

public class Offhand2 extends Module
{
    public enum OffhandMode
    {
        TOTEMS(Items.TOTEM_OF_UNDYING),
        CRYSTALS(Items.END_CRYSTAL),
        GAPPLES(Items.GOLDEN_APPLE),
        BED(Items.BED),
        SHIELD(Items.SHIELD);

        private final Item item;

        private OffhandMode(final Item item) {
            this.item = item;
        }

        public Item getItem() {
            return this.item;
        }
    }
    public Setting<Boolean> swordGap = this.register(new Setting<Boolean>("SwordGap", false));
    public Setting<Boolean> gapOverride = this.register(new Setting<Boolean>("GapOverride", false));
    public Setting<Boolean> recursive = this.register(new Setting<Boolean>("Recursive", false));
    public Setting<Boolean> lethal = this.register(new Setting<Boolean>("Lethal", false));
    public Setting<Float> health = this.register(new Setting<Float>("Health",16.0f, 1.0f, 20.0f));
    public Setting<Float> holeHealth = this.register(new Setting<Float>("HoleHealth",12.0f, 1.0f, 20.0f));


    protected final Setting<OffhandMode> mode;
    protected final Map<Item, Integer> lastSlots;
    protected boolean lookedUp;
    protected boolean gap;
    protected boolean fent;

    public Offhand2() {
        super("Offhand2","", Category.COMBAT, true, false,false);
        this.mode = this.register(new Setting("Mode", OffhandMode.TOTEMS));
           this.lastSlots = new HashMap<Item, Integer>();
           this.fent = false;
          }

    @Override
    public void onUpdate() {
        if (mc.player != null && InventoryUtil.validScreen()) {
            if (swordGap.getValue()) {
                gap = (mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword);
            }
            final Item item = getItem(gap);
            switchToItem(item);
        }
    }
    protected void switchToItem(final Item item) {
        final ItemStack drag = Offhand2.mc.player.inventory.getItemStack();
        final Item dragItem = drag.getItem();
        final Item offhandItem = Offhand2.mc.player.getHeldItemOffhand().getItem();
        if (offhandItem != item) {
            if (dragItem == item) {
                this.fent = true;
                InventoryUtil.clickLocked(-2, 45, dragItem, offhandItem);
                this.fent = false;
                this.lookedUp = false;
            }
            else {
                final Integer last = this.lastSlots.get(item);
                int slot;
                if (last != null && InventoryUtil.get((int)last).getItem() == item) {
                    slot = last;
                }
                else {
                    slot = this.findItem(item);
                }
                if (slot != -1 && slot != -2) {
                    this.lastSlots.put(item, slot);
                    this.lookedUp = false;
                    final Item slotItem = InventoryUtil.get(slot).getItem();
                    this.fent = true;
                    InventoryUtil.clickLocked(-1, slot, (Item)null, slotItem);
                    this.fent = false;
                }
            }
        }
        else if (!drag.isEmpty() && !this.lookedUp) {
            final Integer lastSlot = this.lastSlots.get(dragItem);
            if (lastSlot != null && InventoryUtil.get((int)lastSlot).isEmpty()) {
                this.fent = true;
                InventoryUtil.clickLocked(-2, (int)lastSlot, dragItem, InventoryUtil.get((int)lastSlot).getItem());
                this.fent = false;
            }
            else {
                final int slot2 = this.findEmpty();
                if (slot2 != -1 && slot2 != -2) {
                    this.lastSlots.put(dragItem, slot2);
                    InventoryUtil.clickLocked(-2, slot2, dragItem, InventoryUtil.get(slot2).getItem());
                }
            }
            this.lookedUp = true;
        }
    }

    private int findItem(final Item item) {
        for (int i = 9; i < (this.recursive.getValue() ? 45 : 36); ++i) {
            if (InventoryUtil.get(i).getItem() == item) {
                return i;
            }
        }
        return -1;
    }

    private int findEmpty() {
        if (Offhand2.mc.player.inventory.getItemStack().getItem() == Items.AIR) {
            return -2;
        }
        for (int i = 9; i < 45; ++i) {
            if (InventoryUtil.get(i).getItem() == Items.AIR) {
                return i;
            }
        }
        return -1;
    }

    protected Item getItem(final boolean gapple) {
        Item item = Items.TOTEM_OF_UNDYING;
        if ((boolean)this.lethal.getValue() && (Offhand2.mc.player.fallDistance > 10.0f || Offhand2.mc.player.capabilities.isFlying)) {
            return item;
        }
        final boolean inHole = HoleUtil.isHole(PositionUtil.getPosition());
        if (EntityUtil.getHealth((EntityPlayer) Offhand2.mc.player) >= this.getHealth(inHole, gapple, (boolean)this.gapOverride.getValue())) {
            if (!gapple) {
                item = ((OffhandMode)this.mode.getValue()).getItem();
            }
            else {
                item = Items.GOLDEN_APPLE;
            }
        }
        return item;
    }

    protected float getHealth(final boolean safe, final boolean gapple, final boolean antigap) {
        return (float)(gapple ? (antigap ? 0.0f : (safe ? this.holeHealth.getValue() : ((float)this.health.getValue()))) : (safe ? this.holeHealth.getValue() : ((float)this.health.getValue())));
    }

    public boolean isFent() {
        return this.fent;
    }
}
