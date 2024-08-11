package fat.vonware.util;

import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.inventory.Slot;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketHeldItemChange;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Predicate;

public class InventoryUtil
        implements Util {
    public static void switchToHotbarSlot(int slot, boolean silent) {
        if (mc.player.inventory.currentItem == slot || slot < 0) {
            return;
        }
        if (silent) {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.playerController.updateController();
        } else {
            mc.player.connection.sendPacket(new CPacketHeldItemChange(slot));
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }
    }
    public static ItemStack getItemStack(int id) {
        try {
            return mc.player.inventory.getStackInSlot(id);
        } catch (NullPointerException e) {
            return null;
        }
    }
    public static void clickSlot(int id) {
        if (id != -1) {
            try {
                mc.playerController.windowClick(mc.player.openContainer.windowId, getClickSlot(id), 0, ClickType.PICKUP, mc.player);
            } catch (Exception ignored) {

            }
        }
    }
    public static int getClickSlot(int id) {
        if (id == -1) {
            return id;
        }

        if (id < 9) {
            id += 36;
            return id;
        }

        if (id == 39) {
            id = 5;
        } else if (id == 38) {
            id = 6;
        } else if (id == 37) {
            id = 7;
        } else if (id == 36) {
            id = 8;
        } else if (id == 40) {
            id = 45;
        }

        return id;
    }
    public static int getItemSlot(Item items) {
        for (int i = 0; i < 36; ++i) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == items) {
                if (i < 9) {
                    i += 36;
                }

                return i;
            }
        }
        return -1;
    }
    public static int itemCount;

    public static int getItemCount(Item item, boolean includeOffhand) {
        if (!includeOffhand) {
            itemCount = mc.player.inventory.mainInventory.stream().filter(stack -> stack.getItem() == item).mapToInt(ItemStack::getCount).sum();
        } else if (includeOffhand) {
            itemCount = mc.player.inventory.mainInventory.stream().filter(stack -> stack.getItem() == item).mapToInt(ItemStack::getCount).sum() + mc.player.inventory.offHandInventory.stream().filter(stack -> stack.getItem() == item).mapToInt(ItemStack::getCount).sum();
        }
        return itemCount;
    }
    public static boolean validScreen() {
        return !(InventoryUtil.mc.currentScreen instanceof GuiContainer) || InventoryUtil.mc.currentScreen instanceof GuiInventory;
    }
    public static void click(final int slot) {
        InventoryUtil.mc.playerController.windowClick(0, slot, 0, ClickType.PICKUP, (EntityPlayer)InventoryUtil.mc.player);
    }
    public static void clickLocked(final int slot, final int to, final Item inSlot, final Item inTo) {
        if ((slot == -1 || get(slot).getItem() == inSlot) && get(to).getItem() == inTo) {
            click(to);
        }
    }

    public static class Task {
        private final int slot;
        private final boolean update;
        private final boolean quickClick;

        public Task() {
            this.update = true;
            this.slot = -1;
            this.quickClick = false;
        }

        public Task(final int slot) {
            this.slot = slot;
            this.quickClick = false;
            this.update = false;
        }

        public Task(final int slot, final boolean quickClick) {
            this.slot = slot;
            this.quickClick = quickClick;
            this.update = false;
        }

        public void run() {
            if (this.update) {
                Util.mc.playerController.updateController();
            }
            if (this.slot != -1) {
                Util.mc.playerController.windowClick(0, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, (EntityPlayer) Util.mc.player);
            }
        }

        public boolean isSwitching() {
            return !this.update;
        }
    }

    public static int findHotbar(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            return i;
        }
        return -1;
    }

    public static ItemStack get(int slot) {
        return slot == -2 ? mc.player.inventory.getItemStack() : (ItemStack) mc.player.inventoryContainer.getInventory().get(slot);
    }

    public static int getCount(Item item) {
        int result = 0;
        for (int i = 0; i < 46; ++i) {
            ItemStack stack = (ItemStack) mc.player.inventoryContainer.getInventory().get(i);
            if (stack.getItem() == item) {
                result += stack.getCount();
            }
        }
        if (mc.player.inventory.getItemStack().getItem() == item) {
            result += mc.player.inventory.getItemStack().getCount();
        }
        return result;
    }

    public static void switchTo(int slot) {
        if (mc.player.inventory.currentItem != slot && slot > -1 && slot < 9) {
            mc.player.inventory.currentItem = slot;
            mc.playerController.updateController();
        }

    }

    public static int findInHotbar(Predicate<ItemStack> condition) {
        return findInHotbar(condition, true);
    }

    public static int findInHotbar(Predicate<ItemStack> condition, boolean offhand) {
        if (offhand && condition.test(mc.player.getHeldItemOffhand())) {
            return -2;
        } else {
            int result = -1;
            for (int i = 8; i > -1; --i) {
                if (condition.test(mc.player.inventory.getStackInSlot(i))) {
                    result = i;
                    if (mc.player.inventory.currentItem == i) {
                        break;
                    }
                }
            }
            return result;
        }
    }

    public static void switchToHotbarSlot(Class clazz, boolean silent) {
        int slot = InventoryUtil.findHotbarBlock(clazz);
        if (slot > -1) {
            InventoryUtil.switchToHotbarSlot(slot, silent);
        }
    }

    public static int findHotbarBlock(Class clazz) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY) continue;
            if (clazz.isInstance(stack.getItem())) {
                return i;
            }
            if (!(stack.getItem() instanceof ItemBlock) || !clazz.isInstance(block = ((ItemBlock) stack.getItem()).getBlock()))
                continue;
            return i;
        }
        return -1;
    }

    public static int findHotbarBlock(Block blockIn) {
        for (int i = 0; i < 9; ++i) {
            Block block;
            ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack == ItemStack.EMPTY || !(stack.getItem() instanceof ItemBlock) || (block = ((ItemBlock) stack.getItem()).getBlock()) != blockIn)
                continue;
            return i;
        }
        return -1;
    }

    public static int getItemHotbar(Item input) {
        for (int i = 0; i < 9; ++i) {
            Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (Item.getIdFromItem(item) != Item.getIdFromItem(input)) continue;
            return i;
        }
        return -1;
    }

    public static int findItemInventorySlot(Item item, boolean offHand) {
        AtomicInteger slot = new AtomicInteger();
        slot.set(-1);
        for (Map.Entry<Integer, ItemStack> entry : InventoryUtil.getInventoryAndHotbarSlots().entrySet()) {
            if (entry.getValue().getItem() != item || entry.getKey() == 45 && !offHand) continue;
            slot.set(entry.getKey());
            return slot.get();
        }
        return slot.get();
    }

    public static List<Integer> findEmptySlots(boolean withXCarry) {
        ArrayList<Integer> outPut = new ArrayList<Integer>();
        for (Map.Entry<Integer, ItemStack> entry : InventoryUtil.getInventoryAndHotbarSlots().entrySet()) {
            if (!entry.getValue().isEmpty && entry.getValue().getItem() != Items.AIR) continue;
            outPut.add(entry.getKey());
        }
        if (withXCarry) {
            for (int i = 1; i < 5; ++i) {
                Slot craftingSlot = mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (!craftingStack.isEmpty() && craftingStack.getItem() != Items.AIR) continue;
                outPut.add(i);
            }
        }
        return outPut;
    }

    public static boolean isBlock(Item item, Class clazz) {
        if (item instanceof ItemBlock) {
            Block block = ((ItemBlock) item).getBlock();
            return clazz.isInstance(block);
        }
        return false;
    }

    public static Map<Integer, ItemStack> getInventoryAndHotbarSlots() {
        return InventoryUtil.getInventorySlots(9, 44);
    }

    private static Map<Integer, ItemStack> getInventorySlots(int currentI, int last) {
        HashMap<Integer, ItemStack> fullInventorySlots = new HashMap<Integer, ItemStack>();
        for (int current = currentI; current <= last; ++current) {
            fullInventorySlots.put(current, mc.player.inventoryContainer.getInventory().get(current));
        }
        return fullInventorySlots;
    }

    public static boolean holdingItem(Class clazz) {
        boolean result = false;
        ItemStack stack = mc.player.getHeldItemMainhand();
        result = InventoryUtil.isInstanceOf(stack, clazz);
        if (!result) {
            ItemStack offhand = mc.player.getHeldItemOffhand();
            result = InventoryUtil.isInstanceOf(stack, clazz);
        }
        return result;
    }

    public static boolean isInstanceOf(ItemStack stack, Class clazz) {
        if (stack == null) {
            return false;
        }
        Item item = stack.getItem();
        if (clazz.isInstance(item)) {
            return true;
        }
        if (item instanceof ItemBlock) {
            Block block = Block.getBlockFromItem(item);
            return clazz.isInstance(block);
        }
        return false;
    }

    public static int getEmptyXCarry() {
        for (int i = 1; i < 5; ++i) {
            Slot craftingSlot = mc.player.inventoryContainer.inventorySlots.get(i);
            ItemStack craftingStack = craftingSlot.getStack();
            if (!craftingStack.isEmpty() && craftingStack.getItem() != Items.AIR) continue;
            return i;
        }
        return -1;
    }

    public static boolean isSlotEmpty(int i) {
        Slot slot = mc.player.inventoryContainer.inventorySlots.get(i);
        ItemStack stack = slot.getStack();
        return stack.isEmpty();
    }

    public static int findArmorSlot(EntityEquipmentSlot type, boolean binding) {
        int slot = -1;
        float damage = 0.0f;
        for (int i = 9; i < 45; ++i) {
            boolean cursed;
            ItemArmor armor;
            ItemStack s = Minecraft.getMinecraft().player.inventoryContainer.getSlot(i).getStack();
            if (s.getItem() == Items.AIR || !(s.getItem() instanceof ItemArmor) || (armor = (ItemArmor) s.getItem()).getEquipmentSlot() != type)
                continue;
            float currentDamage = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, s);
            boolean bl = cursed = binding && EnchantmentHelper.hasBindingCurse(s);
            if (!(currentDamage > damage) || cursed) continue;
            damage = currentDamage;
            slot = i;
        }
        return slot;
    }

    public static int findArmorSlot(EntityEquipmentSlot type, boolean binding, boolean withXCarry) {
        int slot = InventoryUtil.findArmorSlot(type, binding);
        if (slot == -1 && withXCarry) {
            float damage = 0.0f;
            for (int i = 1; i < 5; ++i) {
                boolean cursed;
                ItemArmor armor;
                Slot craftingSlot = mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (craftingStack.getItem() == Items.AIR || !(craftingStack.getItem() instanceof ItemArmor) || (armor = (ItemArmor) craftingStack.getItem()).getEquipmentSlot() != type)
                    continue;
                float currentDamage = armor.damageReduceAmount + EnchantmentHelper.getEnchantmentLevel(Enchantments.PROTECTION, craftingStack);
                boolean bl = cursed = binding && EnchantmentHelper.hasBindingCurse(craftingStack);
                if (!(currentDamage > damage) || cursed) continue;
                damage = currentDamage;
                slot = i;
            }
        }
        return slot;
    }

    public static int findItemInventorySlot(Item item, boolean offHand, boolean withXCarry) {
        int slot = InventoryUtil.findItemInventorySlot(item, offHand);
        if (slot == -1 && withXCarry) {
            for (int i = 1; i < 5; ++i) {
                Item craftingStackItem;
                Slot craftingSlot = mc.player.inventoryContainer.inventorySlots.get(i);
                ItemStack craftingStack = craftingSlot.getStack();
                if (craftingStack.getItem() == Items.AIR || (craftingStackItem = craftingStack.getItem()) != item)
                    continue;
                slot = i;
            }
        }
        return slot;
    }
}

   /* public static class Task {
        private final int slot;
        private final boolean update;
        private final boolean quickClick;

        public Task() {
            this.update = true;
            this.slot = -1;
            this.quickClick = false;
        }

        public Task(int slot) {
            this.slot = slot;
            this.quickClick = false;
            this.update = false;
        }

        public Task(int slot, boolean quickClick) {
            this.slot = slot;
            this.quickClick = quickClick;
            this.update = false;
        }

        public void run() {
            if (this.update) {
                mc.playerController.updateController();
            }
            if (this.slot != -1) {
                mc.playerController.windowClick(0, this.slot, 0, this.quickClick ? ClickType.QUICK_MOVE : ClickType.PICKUP, mc.player);
            }
        }
    }
}*/

