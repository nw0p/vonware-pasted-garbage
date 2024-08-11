package fat.vonware.features.modules.client;

import com.mojang.realmsclient.gui.ChatFormatting;
import fat.vonware.Vonware;
import fat.vonware.event.events.Render2DEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.modules.misc.NameHider;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.*;
import fat.vonware.util.Timer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import scala.Char;

import javax.swing.*;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

public class HUD extends Module {
    private final Setting<Boolean> watermark = this.register(new Setting("Watermark", false));
    private final Setting<Boolean> grayNess = register(new Setting("Gray", Boolean.valueOf(true)));
    private static final ItemStack totem = new ItemStack(Items.TOTEM_OF_UNDYING);
    private final Setting<Boolean> armor = register(new Setting("Armor", Boolean.valueOf(false), "ArmorHUD"));
    private final Setting<Boolean> totems = register(new Setting("Totems", Boolean.valueOf(false), "TotemHUD"));

    private final Setting<Boolean> renderingUp = register(new Setting("RenderingUp", Boolean.valueOf(false), "Orientation of the HUD-Elements."));
    private final Setting<Boolean> speed = register(new Setting("Speed", Boolean.valueOf(false), "Your Speed"));
    private final Setting<Boolean> potions = register(new Setting("Potions", Boolean.valueOf(false), "Your Speed"));
    private final Setting<Boolean> ping = register(new Setting("Ping", Boolean.valueOf(false), "Your response time to the server."));
    private final Setting<Boolean> tps = register(new Setting("TPS", Boolean.valueOf(false), "Ticks per second of the server."));
    private final Setting<Boolean> fps = register(new Setting("FPS", Boolean.valueOf(false), "Your frames per second."));
    private final Setting<Boolean> arrayList = register(new Setting("ActiveModules", Boolean.valueOf(false), "Lists the active modules."));
    private final Setting<Boolean> coords = register(new Setting("Coords", Boolean.valueOf(false), "Your current coordinates"));
    private final Setting<Boolean> direction = register(new Setting("Direction", Boolean.valueOf(false), "The Direction you are facing."));
    private final Setting<Greeter> greeter = this.register(new Setting("Greeter", Greeter.TIME));
    private final Setting<String> spoofGreeter = this.register(new Setting("GreeterName", "Meow welcome :3", v -> this.greeter.getValue() == Greeter.CUSTOM));
    public Setting<Boolean> textRadar = this.register(new Setting<Boolean>("TextRadar", Boolean.valueOf(false), "A TextRadar"));
    public Setting<Integer> textRadarUpdates = this.register(new Setting<>("TRUpdates", 500, 0, 1000));
    public Setting<Boolean> selfTextRadar = this.register(new Setting<Boolean>("selfTextRadar", Boolean.valueOf(false), "A TextRadar"));
    public Setting<Integer> waterMarkY = this.register(new Setting("WaterMarkY", 13, 0, 20));
    public Setting<RenderingMode> renderingMode = register(new Setting("Ordering", RenderingMode.ABC));
    public Setting<Integer> lagTime = register(new Setting("LagTime", Integer.valueOf(1000), Integer.valueOf(0), Integer.valueOf(2000)));
    private int color;
    private final Timer timer = new Timer();
    private Map<String, Integer> players = new HashMap<>();

    public HUD() {
        super("HUD", "meow", Category.CLIENT, false, false, false);
    }

    public static HUD INSTANCE = new HUD();

    @Override
    public void onUpdate() {
        if (this.timer.passedMs(HUD.INSTANCE.textRadarUpdates.getValue())) {
            this.players = getTextRadarMap();
            this.timer.reset();
        }
    }
    public static HUD getInstance() {
        if (INSTANCE == null)
            INSTANCE = new HUD();
        return INSTANCE;
    }


    public void onRender2D(Render2DEvent event) {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        this.color = ColorUtil.toRGBA(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue());
        if (this.watermark.getValue()) {
            String string = "Vonware" + " " + Vonware.MODVER;
            if (((Boolean) (ClickGui.getInstance().rainbow.getValue()).booleanValue())) {
                if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                    renderer.drawString(string, 2.0F, 2, ColorUtil.rainbow(((Integer) (ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
                } else {
                    int[] arrayOfInt = {1};
                    char[] stringToCharArray = string.toCharArray();
                    float f = 0.0F;
                    for (char c : stringToCharArray) {
                        renderer.drawString(String.valueOf(c), 2.0F + f, 2 + waterMarkY.getValue(), ColorUtil.rainbow(arrayOfInt[0] * ((Integer) (ClickGui.getInstance()).rainbowHue.getValue()).intValue()).getRGB(), true);
                        f += renderer.getStringWidth(String.valueOf(c));
                        arrayOfInt[0] = arrayOfInt[0] + 1;
                    }
                }
            } else {
                renderer.drawString(string, 2.0F, 2 + waterMarkY.getValue(), color, true);
            }
        }
        if (greeter.getValue() != Greeter.NONE) {
            this.renderGreeter();
        }
        if (this.textRadar.getValue()) {
            this.drawTextRadar(0);
        }
        int[] counter1 = {1};
        int j = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat && !this.renderingUp.getValue().booleanValue()) ? 14 : 0;
        if (this.arrayList.getValue().booleanValue())
            if (this.renderingUp.getValue().booleanValue()) {
                if (this.renderingMode.getValue() == RenderingMode.ABC) {
                    for (int k = 0; k < Vonware.moduleManager.sortedModulesABC.size(); k++) {
                        String str = Vonware.moduleManager.sortedModulesABC.get(k);
                        this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (2 + j * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                        j++;
                        counter1[0] = counter1[0] + 1;
                    }
                } else {
                    for (int k = 0; k < Vonware.moduleManager.sortedModules.size(); k++) {
                        Module module = Vonware.moduleManager.sortedModules.get(k);
                        String str = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
                        this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (2 + j * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                        j++;
                        counter1[0] = counter1[0] + 1;
                    }
                }
            } else if (this.renderingMode.getValue() == RenderingMode.ABC) {
                for (int k = 0; k < Vonware.moduleManager.sortedModulesABC.size(); k++) {
                    String str = Vonware.moduleManager.sortedModulesABC.get(k);
                    j += 10;
                    this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (height - j), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                for (int k = 0; k < Vonware.moduleManager.sortedModules.size(); k++) {
                    Module module = Vonware.moduleManager.sortedModules.get(k);
                    String str = module.getDisplayName() + ChatFormatting.GRAY + ((module.getDisplayInfo() != null) ? (" [" + ChatFormatting.WHITE + module.getDisplayInfo() + ChatFormatting.GRAY + "]") : "");
                    j += 10;
                    this.renderer.drawString(str, (width - 2 - this.renderer.getStringWidth(str)), (height - j), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        String grayString = this.grayNess.getValue().booleanValue() ? String.valueOf(ChatFormatting.GRAY) : "";
        int i = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat && this.renderingUp.getValue().booleanValue()) ? 13 : (this.renderingUp.getValue().booleanValue() ? -2 : 0);
        if (this.renderingUp.getValue().booleanValue()) {
            if (this.potions.getValue().booleanValue()) {
                List<PotionEffect> effects = new ArrayList<>((Minecraft.getMinecraft()).player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str = Vonware.potionManager.getColoredPotionString(potionEffect);
                    i += 10;
                    this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                String str = grayString + "Speed " + ChatFormatting.WHITE + Vonware.speedManager.getSpeedKpH() + " km/h";
                i += 10;
                this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }

            if (this.tps.getValue().booleanValue()) {
                String str = grayString + "TPS " + ChatFormatting.WHITE + Vonware.serverManager.getTPS();
                i += 10;
                this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            String str1 = grayString + "Ping " + ChatFormatting.WHITE + Vonware.serverManager.getPing();
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    i += 10;
                    this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    i += 10;
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    i += 10;
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.ping.getValue().booleanValue()) {
                    i += 10;
                    this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (height - 2 - i), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            if (this.potions.getValue().booleanValue()) {
                List<PotionEffect> effects = new ArrayList<>((Minecraft.getMinecraft()).player.getActivePotionEffects());
                for (PotionEffect potionEffect : effects) {
                    String str = Vonware.potionManager.getColoredPotionString(potionEffect);
                    this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), potionEffect.getPotion().getLiquidColor(), true);
                }
            }
            if (this.speed.getValue().booleanValue()) {
                String str = grayString + "Speed " + ChatFormatting.WHITE + Vonware.speedManager.getSpeedKpH() + " km/h";
                this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            if (this.tps.getValue().booleanValue()) {
                String str = grayString + "TPS " + ChatFormatting.WHITE + Vonware.serverManager.getTPS();
                this.renderer.drawString(str, (width - this.renderer.getStringWidth(str) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                counter1[0] = counter1[0] + 1;
            }
            String fpsText = grayString + "FPS " + ChatFormatting.WHITE + Minecraft.debugFPS;
            String str1 = grayString + "Ping " + ChatFormatting.WHITE + Vonware.serverManager.getPing();
            if (this.renderer.getStringWidth(str1) > this.renderer.getStringWidth(fpsText)) {
                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            } else {
                if (this.fps.getValue().booleanValue()) {
                    this.renderer.drawString(fpsText, (width - this.renderer.getStringWidth(fpsText) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }

                if (this.ping.getValue().booleanValue()) {
                    this.renderer.drawString(str1, (width - this.renderer.getStringWidth(str1) - 2), (2 + i++ * 10), (ClickGui.getInstance()).rainbow.getValue().booleanValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB()) : this.color, true);
                    counter1[0] = counter1[0] + 1;
                }
            }
        }
        boolean inHell = mc.world.getBiome(mc.player.getPosition()).getBiomeName().equals("Hell");
        int posX = (int) mc.player.posX;
        int posY = (int) mc.player.posY;
        int posZ = (int) mc.player.posZ;
        float nether = !inHell ? 0.125F : 8.0F;
        int hposX = (int) (mc.player.posX * nether);
        int hposZ = (int) (mc.player.posZ * nether);
        i = (mc.currentScreen instanceof net.minecraft.client.gui.GuiChat) ? 14 : 0;
        String coordinates = ChatFormatting.WHITE + "XYZ " + ChatFormatting.RESET + (inHell ? (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]" + ChatFormatting.RESET) : (posX + ", " + posY + ", " + posZ + ChatFormatting.WHITE + " [" + ChatFormatting.RESET + hposX + ", " + hposZ + ChatFormatting.WHITE + "]"));
        String direction = this.direction.getValue().booleanValue() ? Vonware.rotationManager.getDirection4D(false) : "";
        String coords = this.coords.getValue().booleanValue() ? coordinates : "";
        i += 10;
        if ((ClickGui.getInstance()).rainbow.getValue().booleanValue()) {
            String rainbowCoords = this.coords.getValue().booleanValue() ? ("XYZ " + (inHell ? (posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]") : (posX + ", " + posY + ", " + posZ + " [" + hposX + ", " + hposZ + "]"))) : "";
            if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                this.renderer.drawString(direction, 2.0F, (height - i - 11), ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                this.renderer.drawString(rainbowCoords, 2.0F, (height - i), ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
            } else {
                int[] counter2 = {1};
                char[] stringToCharArray = direction.toCharArray();
                float s = 0.0F;
                for (char c : stringToCharArray) {
                    this.renderer.drawString(String.valueOf(c), 2.0F + s, (height - i - 11), ColorUtil.rainbow(counter2[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                    s += this.renderer.getStringWidth(String.valueOf(c));
                    counter2[0] = counter2[0] + 1;
                }
                int[] counter3 = {1};
                char[] stringToCharArray2 = rainbowCoords.toCharArray();
                float u = 0.0F;
                for (char c : stringToCharArray2) {
                    this.renderer.drawString(String.valueOf(c), 2.0F + u, (height - i), ColorUtil.rainbow(counter3[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                    u += this.renderer.getStringWidth(String.valueOf(c));
                    counter3[0] = counter3[0] + 1;
                }
            }
        } else {
            this.renderer.drawString(direction, 2.0F, (height - i - 11), this.color, true);
            this.renderer.drawString(coords, 2.0F, (height - i), this.color, true);
        }
        if (this.totems.getValue().booleanValue())
            renderTotemHUD();
        if (armor.getValue()) drawArmorHUD();
    }
    private void drawArmorHUD() {
        int width = Vonware.textManager.scaledWidth;
        int height = Vonware.textManager.scaledHeight;
        GlStateManager.enableTexture2D();
        int i = width / 2;
        int iteration = 0;
        int y = height - 55 - (mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure() ? 10 : 0);
        for (ItemStack is : mc.player.inventory.armorInventory) {
            iteration++;
            if (is.isEmpty()) continue;
            int x = i - 90 + (9 - iteration) * 20 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(is, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, is, x, y, "");
            RenderUtil.itemRender.zLevel = 0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            String s = is.getCount() > 1 ? is.getCount() + "" : "";
            Vonware.textManager.drawStringWithShadow(s, x + 19 - 2 - Vonware.textManager.getStringWidth(s), y + 9, 0xffffff);

            if (true) {
                int dmg = 0;
                int itemDurability = is.getMaxDamage() - is.getItemDamage();
                float green = (( float ) is.getMaxDamage() - ( float ) is.getItemDamage()) / ( float ) is.getMaxDamage();
                float red = 1 - green;
                if (true) {
                    dmg = 100 - ( int ) (red * 100);
                } else {
                    dmg = itemDurability;
                }
                Vonware.textManager.drawStringWithShadow(dmg + "", x + 8 - Vonware.textManager.getStringWidth(dmg + "") / 2, y - 11, ColorUtil.toRGBA(( int ) (red * 255), ( int ) (green * 255), 0));
            }
        }
        GlStateManager.enableDepth();
        GlStateManager.disableLighting();
    }
    public void renderTotemHUD() {
        int width = this.renderer.scaledWidth;
        int height = this.renderer.scaledHeight;
        int totems = mc.player.inventory.mainInventory.stream().filter(itemStack -> (itemStack.getItem() == Items.TOTEM_OF_UNDYING)).mapToInt(ItemStack::getCount).sum();
        if (mc.player.getHeldItemOffhand().getItem() == Items.TOTEM_OF_UNDYING)
            totems += mc.player.getHeldItemOffhand().getCount();
        if (totems > 0) {
            GlStateManager.enableTexture2D();
            int i = width / 2;
            int iteration = 0;
            int y = height - 55 - ((mc.player.isInWater() && mc.playerController.gameIsSurvivalOrAdventure()) ? 10 : 0);
            int x = i - 189 + 180 + 2;
            GlStateManager.enableDepth();
            RenderUtil.itemRender.zLevel = 200.0F;
            RenderUtil.itemRender.renderItemAndEffectIntoGUI(totem, x, y);
            RenderUtil.itemRender.renderItemOverlayIntoGUI(mc.fontRenderer, totem, x, y, "");
            RenderUtil.itemRender.zLevel = 0.0F;
            GlStateManager.enableTexture2D();
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            this.renderer.drawStringWithShadow(totems + "", (x + 19 - 2 - this.renderer.getStringWidth(totems + "")), (y + 9), 16777215);
            GlStateManager.enableDepth();
            GlStateManager.disableLighting();
        }
    }


    public Map<String, Integer> getTextRadarPlayers() {
        return EntityUtil.getTextRadarPlayers();
    }

    public void renderGreeter() {
        int width = this.renderer.scaledWidth;
        String text = "";
        switch (this.greeter.getValue()) {
            case TIME: {
                text = text + MathUtil.getTimeOfDay() + ""  + getSpoofedName();
                break;
            }
            case CHRISTMAS: {
                text = text + "MERRY CHRISTMAS, "  + getSpoofedName();
                break;
            }
            case LONG: {
                text = text + "Welcome to Vonware, "  + getSpoofedName();
                break;
            }
            case II: {
                text = text + "II Welcomes you, "  + getSpoofedName();
                break;
            }
            case CUSTOM: {
                text = text + this.spoofGreeter.getValue();
                break;
            }
            default: {
                text = text + "Welcome, "  + getSpoofedName()  + " :^)";
            }
        }
        if ((ClickGui.getInstance()).rainbow.getValue().booleanValue()) {
            if ((ClickGui.getInstance()).rainbowModeHud.getValue() == ClickGui.rainbowMode.Static) {
                renderer.drawString(text, width / 2.0F - renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
            } else {
                int[] counter1 = {1};
                char[] stringToCharArray = text.toCharArray();
                float i = 0.0F;
                for (char c : stringToCharArray) {
                    renderer.drawString(String.valueOf(c), width / 2.0F - renderer.getStringWidth(text) / 2.0F + 2.0F + i, 2.0F, ColorUtil.rainbow(counter1[0] * (ClickGui.getInstance()).rainbowHue.getValue().intValue()).getRGB(), true);
                    i += renderer.getStringWidth(String.valueOf(c));
                    counter1[0] = counter1[0] + 1;
                }
            }
        } else {
            renderer.drawString(text, width / 2.0F - renderer.getStringWidth(text) / 2.0F + 2.0F, 2.0F, color, true);
        }
    }

    public void drawTextRadar(final int yOffset) {
        if (!this.players.isEmpty()) {
            int y = this.renderer.getFontHeight() + 7 + yOffset;
            for (final Map.Entry<String, Integer> player : this.players.entrySet()) {
                final String text = player.getKey() + " ";
                final int textheight = this.renderer.getFontHeight() + 1;
                this.renderer.drawString(text, 2.0f, (float) y, (ClickGui.getInstance().rainbow.getValue() ? (((ClickGui.getInstance()).rainbowModeA.getValue() == ClickGui.rainbowModeArray.Up) ? ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB() : ColorUtil.rainbow((ClickGui.getInstance()).rainbowHue.getValue()).getRGB()) : this.color), true);
                y += textheight;
            }
        }
    }
    private Map<String, Integer> getTextRadarMap() {
        Map<String, Integer> retval = new HashMap<>();

        DecimalFormat dfDistance = new DecimalFormat("#.#");
        dfDistance.setRoundingMode(RoundingMode.CEILING);
        StringBuilder distanceSB = new StringBuilder();

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player.isInvisible() || player.getName().equals(mc.player.getName())) continue;

            int distanceInt = (int) mc.player.getDistance(player);
            String distance = dfDistance.format(distanceInt);

            if (distanceInt >= 25) {
                distanceSB.append(ChatFormatting.GREEN);

            } else if (distanceInt > 10) {
                distanceSB.append(ChatFormatting.YELLOW);

            } else {
                distanceSB.append(ChatFormatting.RED);
            }
            distanceSB.append(distance);

            retval.put(

                    (Vonware.friendManager.isFriend(player) ? ChatFormatting.AQUA : ChatFormatting.RESET)
                            + player.getName()
                            + " "
                            + ChatFormatting.GRAY
                            + "["
                            + ChatFormatting.RESET
                            + distanceSB
                            + "m"
                            + ChatFormatting.GRAY
                            + "] "
                            + ChatFormatting.GREEN,
                    (int) mc.player.getDistance(player));

            distanceSB.setLength(0);
        }

        if (!retval.isEmpty()) {
            retval = MathUtil.sortByValue(retval, false);
        }

        return retval;
    }

    public String getSpoofedName() {
        NameHider nameHiderModule = Vonware.moduleManager.getModuleByClass(NameHider.class);
        return nameHiderModule.isEnabled() ? nameHiderModule.newName.getValue() : mc.player.getName();
    }


    public static enum Greeter {
        NONE,
        NAME,
        TIME,
        CHRISTMAS,
        LONG,
        II,
        CUSTOM;

    }
    

    public enum RenderingMode {
        Length, ABC
    }
}

