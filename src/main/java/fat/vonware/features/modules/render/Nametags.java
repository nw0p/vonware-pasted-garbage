package fat.vonware.features.modules.render;

import fat.vonware.Vonware;
import fat.vonware.event.events.Render3DEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.modules.misc.PopCounter;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.*;
import net.minecraft.block.*;
import java.awt.*;
import fat.vonware.util.Render2DMethods;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.item.*;
import net.minecraft.client.renderer.*;
import net.minecraft.enchantment.*;
import net.minecraft.init.*;
import net.minecraft.util.*;
import net.minecraft.entity.player.*;
import net.minecraft.util.text.*;
import java.util.*;
import java.util.List;

import net.minecraft.entity.*;
import net.minecraft.world.*;
import net.minecraft.util.math.*;
import net.minecraft.block.state.*;

public class Nametags extends Module
{

    public Setting<Boolean> armor = this.register(new Setting<Boolean>("Armor", false));
    public Setting<Boolean> enchants = this.register(new Setting<Boolean>("Enchants", false));
    public Setting<Boolean> simple = this.register(new Setting<Boolean>("Simple", false));
    public Setting<Boolean> simpleSwords = this.register(new Setting<Boolean>("SimpleSwords", false));
    public Setting<Boolean> superSimple = this.register(new Setting<Boolean>("SuperSimple", false));
    public Setting<Boolean> lowercase = this.register(new Setting<Boolean>("LowerCase", false));
    public Setting<Boolean> durability = this.register(new Setting<Boolean>("Durability", false));
    public Setting<Boolean> itemStack = this.register(new Setting<Boolean>("ItemStack", false));
    public Setting<Boolean> invisibles = this.register(new Setting<Boolean>("Invisibles", false));
    public Setting<Boolean> entityId = this.register(new Setting<Boolean>("Entity-ID", false));
    public Setting<Boolean> gameMode = this.register(new Setting<Boolean>("Gamemode", false));
    public Setting<Boolean> totemPops = this.register(new Setting<Boolean>("Totem-Pops", false));
    public Setting<Boolean> burrow = this.register(new Setting<Boolean>("Burrow", false));
    public Setting<Boolean> syncBorder = this.register(new Setting<Boolean>("SyncBorder", false));
    public Setting<Boolean> holeColor = this.register(new Setting<Boolean>("HoleColor", false));
    public Setting<Boolean> userAlias = this.register(new Setting<Boolean>("UserAlias", false));
    public Setting<Float> scaling = this.register(new Setting<Float>("Scaling", 0.3f, 0.1f,1.0f));
    public Setting<Float> opacity = this.register(new Setting<Float>("Opacity", 0.5f, 0.0f,1.0f));
    public Setting<Float> outlineOpacity = this.register(new Setting<Float>("OutlineOpacity", 0.75f, 0.0f,1.0f));
    private final Setting<Integer> yOffset = this.register(new Setting<Integer>("Y-Offset", 0, 0, 255));

    protected final Setting<PingEnum> ping;
    public enum PingEnum
    {
        NORMAL,
        COLORED,
        NONE;
    }

    protected final Setting<SneakEnum> sneak;
    public enum SneakEnum
    {
        NONE,
        DARK,
        LIGHT;
    }
    private final Setting<Integer> red = this.register(new Setting<Integer>("Red", 255, 0, 255));
    private final Setting<Integer> green = this.register(new Setting<Integer>("Green", 255, 0, 255));
    private final Setting<Integer> blue = this.register(new Setting<Integer>("Blue", 255, 0, 255));
    private final Setting<Integer> friendred = this.register(new Setting<Integer>("F-Red", 0, 0, 255));
    private final Setting<Integer> friendgreen = this.register(new Setting<Integer>("F-Green", 255, 0, 255));
    private final Setting<Integer> friendblue = this.register(new Setting<Integer>("F-Blue", 255, 0, 255));

    private final Setting<Integer> borderred = this.register(new Setting<Integer>("BorderRed", 255, 0, 255, v -> this.syncBorder.getValue()));
    private final Setting<Integer> bordergreen = this.register(new Setting<Integer>("BorderGreen", 255, 0, 255, v -> this.syncBorder.getValue()));
    private final Setting<Integer> borderblue = this.register(new Setting<Integer>("BorderBlue", 255, 0, 255, v -> this.syncBorder.getValue()));

    private final Setting<Integer> burrowred = this.register(new Setting<Integer>("BurrowRed", 140, 0, 255, v -> this.burrow.getValue()));
    private final Setting<Integer> burrowgreen = this.register(new Setting<Integer>("BurrowGreen", 0, 0, 255, v -> this.burrow.getValue()));
    private final Setting<Integer> burrowblue = this.register(new Setting<Integer>("BurrowBlue", 140, 0, 255, v -> this.burrow.getValue()));

    private final Setting<Integer> invisred = this.register(new Setting<Integer>("Invis-Red", 255, 0, 255, v -> this.invisibles.getValue()));
    private final Setting<Integer> invisgreen = this.register(new Setting<Integer>("Invis-Green", 0, 0, 255, v -> this.invisibles.getValue()));
    private final Setting<Integer> invisblue = this.register(new Setting<Integer>("Invis-Blue", 0, 0, 255, v -> this.invisibles.getValue()));
    protected final List<Block> burrowList;

    public Nametags() {
        super("Nametags", "", Category.RENDER, true, false ,false);
        this.ping = this.register(new Setting("Ping", PingEnum.NORMAL));
        this.sneak = this.register(new Setting("Sneak", SneakEnum.NONE));
        this.burrowList = Arrays.asList(Blocks.BEDROCK, Blocks.OBSIDIAN, Blocks.ENDER_CHEST, (Block)Blocks.CHEST, Blocks.TRAPPED_CHEST, (Block)Blocks.BEACON, (Block)Blocks.PISTON, Blocks.REDSTONE_BLOCK, Blocks.ENCHANTING_TABLE, Blocks.ANVIL);
    }
    protected Color getFriendColour() {
        return new Color(friendred.getValue(), friendgreen.getValue(), friendblue.getValue());
    }
    protected Color getNametagColor() {
        return new Color(red.getValue(), green.getValue(), blue.getValue());
    }
    protected Color getInvisColor() {
        return new Color(invisred.getValue(), invisgreen.getValue(), invisblue.getValue());
    }
    protected Color getBurrowColor() {
        return new Color(burrowred.getValue(), burrowgreen.getValue(), burrowblue.getValue());
    }
    protected Color getBorderColor() {
        return new Color(borderred.getValue(), bordergreen.getValue(), borderblue.getValue());
    }

    protected void renderStack(final ItemStack stack, final int x, final int y, final int enchHeight) {
        GlStateManager.pushMatrix();
        GlStateManager.depthMask(true);
        GlStateManager.clear(256);
        RenderHelper.enableStandardItemLighting();
        Nametags.mc.getRenderItem().zLevel = -150.0f;
        GlStateManager.disableAlpha();
        GlStateManager.enableDepth();
        GlStateManager.disableCull();
        final int height = (enchHeight > 4) ? ((enchHeight - 4) * 8 / 2) : 0;
        Nametags.mc.getRenderItem().renderItemAndEffectIntoGUI(stack, x, y + height);
        Nametags.mc.getRenderItem().renderItemOverlays(Nametags.mc.fontRenderer, stack, x, y + height);
        Nametags.mc.getRenderItem().zLevel = 0.0f;
        RenderHelper.disableStandardItemLighting();
        GlStateManager.enableCull();
        GlStateManager.enableAlpha();
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        if (this.enchants.getValue()) {
            this.renderEnchants(stack, x, y - 24);
        }
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
        GlStateManager.popMatrix();
    }

    private void renderEnchants(final ItemStack stack, final int xOffset, int yOffset) {
        final Set<Enchantment> e = EnchantmentHelper.getEnchantments(stack).keySet();
        final List<String> enchantTexts = new ArrayList<String>(e.size());
        for (final Enchantment enchantment : e) {
            if ((boolean)this.simple.getValue() && !enchantment.getName().contains("all") && (!enchantment.getName().contains("knockback") || (boolean)this.simpleSwords.getValue()) && (!enchantment.getName().contains("fire") || (boolean)this.simpleSwords.getValue()) && !enchantment.getName().contains("arrowDamage") && !enchantment.getName().contains("explosion") && (!enchantment.getName().contains("fall") || (boolean)this.superSimple.getValue()) && (!enchantment.getName().contains("durability") || (boolean)this.superSimple.getValue())) {
                if (!enchantment.getName().contains("mending")) {
                    continue;
                }
                if (this.superSimple.getValue()) {
                    continue;
                }
            }
            enchantTexts.add(this.getEnchantText(enchantment, EnchantmentHelper.getEnchantmentLevel(enchantment, stack)));
        }
        for (final String enchantment2 : enchantTexts) {
            if (enchantment2 != null) {
                Vonware.textManager.drawString(((boolean)this.lowercase.getValue()) ? enchantment2.toLowerCase() : TextUtil.capitalize(enchantment2), xOffset * 2.0f, (float)yOffset, -1,true);
                yOffset += 8;
            }
        }
        if (stack.getItem().equals(Items.GOLDEN_APPLE) && stack.hasEffect()) {
            Vonware.textManager.drawString("God", xOffset * 2.0f, (float)yOffset, -3977919, true);
        }
    }

    private String getEnchantText(final Enchantment ench, final int lvl) {
        final ResourceLocation resource = (ResourceLocation)Enchantment.REGISTRY.getNameForObject(ench);
        String name = (resource == null) ? ench.getName() : resource.toString();
        final int lvlOffset = (lvl > 1) ? 12 : 13;
        if (name.length() > lvlOffset) {
            name = name.substring(10, lvlOffset);
        }
        if (lvl > 1) {
            name += lvl;
        }
        return (name.length() < 2) ? name : TextUtil.getFixedName(name);
    }

    protected void renderText(final ItemStack stack, final float y) {
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        final String name = stack.getDisplayName();
        Vonware.textManager.drawString(name, (float)(-Vonware.textManager.getStringWidth(name) >> 1), y, -1, true);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
    }

    protected void renderDurability(final ItemStack stack, final float x, final float y) {
        final int percent = (int)DamageUtil.getDamageInPercent(stack);
        GlStateManager.scale(0.5f, 0.5f, 0.5f);
        GlStateManager.disableDepth();
        Vonware.textManager.drawString(percent + "%", x * 2.0f, y, stack.getItem().getRGBDurabilityForDisplay(stack), true);
        GlStateManager.enableDepth();
        GlStateManager.scale(2.0f, 2.0f, 2.0f);
    }

    protected String getDisplayTag(final EntityPlayer player) {
        String displayName = player.getDisplayName().getFormattedText();
        if (this.userAlias.getValue()) {
            final String playerName = player.getName();
            if (Vonware.friendManager.isFriend(playerName) && !Vonware.friendManager.isAliasSameAsLabel(playerName)) {
                displayName = Vonware.friendManager.getFriend(playerName).getUsername();
            }
            if (displayName.contains(Nametags.mc.player.getName())) {
                displayName = "You";
            }
        }
        final double health = Math.ceil(EntityUtil.getHealth(player));
        String color;
        if (health > 18.0) {
            color = TextFormatting.GREEN.toString();
        }
        else if (health > 16.0) {
            color = TextFormatting.DARK_GREEN.toString();
        }
        else if (health > 12.0) {
            color = TextFormatting.YELLOW.toString();
        }
        else if (health > 8.0) {
            color = TextFormatting.GOLD.toString();
        }
        else if (health > 5.0) {
            color = TextFormatting.RED.toString();
        }
        else {
            color = TextFormatting.DARK_RED.toString();
        }
        String idString = "";
        if (this.entityId.getValue()) {
            idString = idString + " ID: " + player.getEntityId();
        }
        String gameModeStr = "";
        if (this.gameMode.getValue()) {
            gameModeStr = (player.isCreative() ? (gameModeStr + " [C]") : (player.isSpectator() ? (gameModeStr + " [I]") : (gameModeStr + " [S]")));
        }
        String pingStr = "";
        if (this.ping.getValue() != PingEnum.NONE) {
            try {
                final int responseTime = Objects.requireNonNull(Nametags.mc.getConnection()).getPlayerInfo(player.getUniqueID()).getResponseTime();
                switch ((PingEnum)this.ping.getValue()) {
                    case COLORED: {
                        pingStr = pingStr + " " + this.getPingColor(responseTime) + responseTime + "ms";
                        break;
                    }
                    case NORMAL: {
                        pingStr = pingStr + " " + responseTime + "ms";
                        break;
                    }
                }
            }
            catch (Exception ex) {}
        }
        String popStr = "";
        if (this.totemPops.getValue()) {
            final Map<String, Integer> registry = (Map<String, Integer>) PopCounter.TotemPopContainer;
            popStr += (registry.containsKey(player.getName()) ? (this.getPopColor(registry.get(player.getName())) + " -" + registry.get(player.getName())) : "");
        }
        displayName = displayName + idString + gameModeStr + pingStr + color + " " + (int)health + popStr;
        return displayName;
    }

    protected int getNameColor(final EntityPlayer player) {
        if (Vonware.friendManager.isFriend(player)) {
            return getFriendColour().getRGB();
        }
        if (this.burrow.getValue()) {
            final BlockPos pos = PositionUtil.getPosition((Entity)player);
            final IBlockState state = Nametags.mc.world.getBlockState(pos);
            if (this.burrowList.contains(state.getBlock()) && state.getBoundingBox((IBlockAccess)Nametags.mc.world, pos).offset(pos).maxY > player.posY) {
                return getBurrowColor().getRGB();
            }
        }
        if (player.isInvisible()) {
            return getInvisColor().getRGB();
        }
        if (Nametags.mc.getConnection() != null && Nametags.mc.getConnection().getPlayerInfo(player.getUniqueID()) == null) {
            return getInvisColor().getRGB();
        }
        if (player.isSneaking() && this.sneak.getValue() != SneakEnum.NONE) {
            return (this.sneak.getValue() == SneakEnum.LIGHT) ? 16750848 : -6676491;
        }
        return ((Color)getNametagColor()).getRGB();
    }

    protected int getBorderColor(final EntityPlayer player) {
        if (Vonware.friendManager.isFriend(player) && (boolean)this.syncBorder.getValue()) {
            return getFriendColour().getRGB();
        }
        return this.syncBorder.getValue() ? getBorderColor().getRGB() : ((int)(127.0f * (float)this.outlineOpacity.getValue()) << 24);
    }
    @Override
    public void onRender3D(Render3DEvent event) {
        final Entity renderEntity = RenderMethods.getEntity();
        final Frustum frustum = Interpolation.createFrustum(renderEntity);
        final Vec3d interp = Interpolation.interpolateEntity(renderEntity);
        final List<EntityPlayer> playerList = (List<EntityPlayer>)mc.world.playerEntities;
        playerList.sort(Comparator.comparing(player -> mc.player.getDistance(player)));
        for (final EntityPlayer player2 : playerList) {
            final AxisAlignedBB bb = player2.getEntityBoundingBox();
            final Vec3d vec = Interpolation.interpolateEntity((Entity)player2);
            if (frustum.isBoundingBoxInFrustum(bb.expand(0.75, 0.75, 0.75)) && player2 != renderEntity && !EntityUtil.isDead((Entity)player2) && (!player2.isInvisible() || (boolean)invisibles.getValue())) {
                this.renderNameTag(player2, vec.x, vec.y, vec.z, interp);
            }
        }
    }

    private void renderNameTag(final EntityPlayer player, final double x, double y, final double z, final Vec3d mcPlayerInterpolation) {
        final double tempY = y + (player.isSneaking() ? 0.5 : 0.7);
        final double xDist = mcPlayerInterpolation.x - x;
        final double yDist = mcPlayerInterpolation.y - y;
        final double zDist = mcPlayerInterpolation.z - z;
        y = MathHelper.sqrt(xDist * xDist + yDist * yDist + zDist * zDist);
        final String displayTag = getDisplayTag(player);
        final int width = Vonware.textManager.getStringWidth(displayTag) / 2;
        double scale = 0.0018 + MathUtil.fixedNametagScaling((float)scaling.getValue()) * y;
        if (y <= 8.0) {
            scale = 0.0245;
        }
        GlStateManager.pushMatrix();
        RenderHelper.enableStandardItemLighting();
        GlStateManager.enablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, -1500000.0f);
        GlStateManager.disableLighting();
        GlStateManager.translate((float)x, (float)tempY + 1.4f, (float)z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0f, 1.0f, 0.0f);
        final float xRot = (mc.gameSettings.thirdPersonView == 2) ? -1.0f : 1.0f;
        GlStateManager.rotate(mc.getRenderManager().playerViewX, xRot, 0.0f, 0.0f);
        GlStateManager.scale(-scale, -scale, scale);
        GlStateManager.disableDepth();
        GlStateManager.enableBlend();
        GlStateManager.enableBlend();
        if ((float)opacity.getValue() >= 0.2f) {
            final int alpha = (int)((float)outlineOpacity.getValue() * 255.0f);
            final int border = ColorUtil.changeAlpha(new Color(getBorderColor(player)), alpha).getRGB();
            final int green = ColorUtil.changeAlpha(new Color(-15415296), alpha).getRGB();
            final int red = ColorUtil.changeAlpha(new Color(-3669996), alpha).getRGB();
            final int holeColor = EntityUtil.isPlayerSafe(player) ? green : red;
            Render2DMethods.drawNameTagRect((float)(-width - 2), (float)(-(mc.fontRenderer.FONT_HEIGHT + (int)yOffset.getValue())), width + 2.0f, 1.5f, (int)(127.0f * (float)opacity.getValue()) << 24, ((boolean)this.holeColor.getValue()) ? holeColor : border, 1.4f);
        }
        Vonware.textManager.drawString(displayTag, (float)(-width), -8.0f, getNameColor(player), true);
        GlStateManager.disableBlend();
        GlStateManager.pushMatrix();
        final ItemStack heldItemMainhand = player.getHeldItemMainhand();
        final ItemStack heldItemOffhand = player.getHeldItemOffhand();
        int xOffset = 0;
        int enchantOffset = 0;
        int armorSize;
        for (int i = armorSize = 3; i >= 0; i = --armorSize) {
            final ItemStack itemStack;
            if (!(itemStack = (ItemStack)player.inventory.armorInventory.get(armorSize)).isEmpty()) {
                xOffset -= 8;
                final int size;
                if ((boolean)enchants.getValue() && !(boolean)simple.getValue() && (size = EnchantmentHelper.getEnchantments(itemStack).size()) > enchantOffset) {
                    enchantOffset = size;
                }
            }
        }
        if ((!heldItemOffhand.isEmpty() && (boolean)armor.getValue()) || ((boolean)durability.getValue() && heldItemOffhand.isItemStackDamageable())) {
            xOffset -= 8;
            final int size2;
            if ((boolean)enchants.getValue() && !(boolean)simple.getValue() && (size2 = EnchantmentHelper.getEnchantments(heldItemOffhand).size()) > enchantOffset) {
                enchantOffset = size2;
            }
        }
        if (!heldItemMainhand.isEmpty()) {
            final int size3;
            if ((boolean)enchants.getValue() && !(boolean)simple.getValue() && (size3 = EnchantmentHelper.getEnchantments(heldItemMainhand).size()) > enchantOffset) {
                enchantOffset = size3;
            }
            int armorOffset = this.getOffset(enchantOffset);
            if ((boolean)armor.getValue() || ((boolean)durability.getValue() && heldItemMainhand.isItemStackDamageable())) {
                xOffset -= 8;
            }
            if (armor.getValue()) {
                final int oldOffset = armorOffset;
                armorOffset -= 32;
                renderStack(heldItemMainhand, xOffset, oldOffset, enchantOffset);
            }
            if ((boolean)durability.getValue() && heldItemMainhand.isItemStackDamageable()) {
                renderDurability(heldItemMainhand, (float)xOffset, (float)armorOffset);
            }
            if (itemStack.getValue()) {
                renderText(heldItemMainhand, (float)(armorOffset - (durability.getValue() ? 10 : 2)));
            }
            if ((boolean)armor.getValue() || ((boolean)durability.getValue() && heldItemMainhand.isItemStackDamageable())) {
                xOffset += 16;
            }
        }
        int armorSizeI;
        for (int i2 = armorSizeI = 3; i2 >= 0; i2 = --armorSizeI) {
            final ItemStack itemStack2;
            if (!(itemStack2 = (ItemStack)player.inventory.armorInventory.get(armorSizeI)).isEmpty()) {
                int fixedEnchantOffset = this.getOffset(enchantOffset);
                if (armor.getValue()) {
                    final int oldEnchantOffset = fixedEnchantOffset;
                    fixedEnchantOffset -= 32;
                    renderStack(itemStack2, xOffset, oldEnchantOffset, enchantOffset);
                }
                if ((boolean)durability.getValue() && itemStack2.isItemStackDamageable()) {
                    renderDurability(itemStack2, (float)xOffset, (float)fixedEnchantOffset);
                }
                xOffset += 16;
            }
        }
        if (!heldItemOffhand.isEmpty()) {
            int fixedEnchantOffsetI = this.getOffset(enchantOffset);
            if (armor.getValue()) {
                final int oldEnchantOffsetI = fixedEnchantOffsetI;
                fixedEnchantOffsetI -= 32;
                renderStack(heldItemOffhand, xOffset, oldEnchantOffsetI, enchantOffset);
            }
            if ((boolean)durability.getValue() && heldItemOffhand.isItemStackDamageable()) {
                renderDurability(heldItemOffhand, (float)xOffset, (float)fixedEnchantOffsetI);
            }
        }
        GlStateManager.popMatrix();
        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
        GlStateManager.disablePolygonOffset();
        GlStateManager.doPolygonOffset(1.0f, 1500000.0f);
        GlStateManager.popMatrix();
    }

    private int getOffset(final int offset) {
        int fixedOffset = armor.getValue() ? -26 : -27;
        if (offset > 4) {
            fixedOffset -= (offset - 4) * 8;
        }
        return fixedOffset;
    }

    private String getPopColor(final int pops) {
        if (pops == 1) {
            return TextFormatting.GREEN.toString();
        }
        if (pops == 2) {
            return TextFormatting.DARK_GREEN.toString();
        }
        if (pops == 3) {
            return TextFormatting.YELLOW.toString();
        }
        if (pops == 4) {
            return TextFormatting.GOLD.toString();
        }
        if (pops == 5) {
            return TextFormatting.RED.toString();
        }
        return TextFormatting.DARK_RED.toString();
    }

    private String getPingColor(final int ping) {
        if (ping > 200) {
            return TextFormatting.RED.toString();
        }
        if (ping > 100) {
            return TextFormatting.YELLOW.toString();
        }
        return TextFormatting.GREEN.toString();
    }
}
