package fat.vonware.features.modules.render;

import fat.vonware.event.events.ClientEvent;
import fat.vonware.event.events.Render3DEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.modules.client.ClickGui;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.ColorUtil;
import fat.vonware.util.RenderUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class BurrowESP
        extends Module {
    private static BurrowESP INSTANCE = new BurrowESP();
    public Setting<Integer> range = this.register(new Setting<Integer>("Range", 20, 5, 200));
    public Setting<Boolean> self = this.register(new Setting<Boolean>("Self", true));
    public Setting<Boolean> text = this.register(new Setting<Boolean>("Text", true));
    public Setting<String> textString = this.register(new Setting<>("TextString", "BURROW", v -> this.text.getValue()));
    public Setting<Boolean> rainbow = this.register(new Setting<Boolean>("Rainbow", false));
    public Setting<Integer> red = this.register(new Setting<>("Red", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue() == false));
    public Setting<Integer> green = this.register(new Setting<>("Green", Integer.valueOf(255), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue() == false));
    public Setting<Integer> blue = this.register(new Setting<>("Blue", Integer.valueOf(0), Integer.valueOf(0), Integer.valueOf(255), v -> this.rainbow.getValue() == false));
    public Setting<Integer> alpha = this.register(new Setting<Integer>("Alpha", 0, 0, 255));
    public Setting<Integer> outlineAlpha = this.register(new Setting<Integer>("OL-Alpha", 0, 0, 255));
    public Setting<Boolean> colorSync = this.register(new Setting<Boolean>("Color Sync", false));
    private final List<BlockPos> posList = new ArrayList<BlockPos>();
    private RenderUtil renderUtil = new RenderUtil();

    public BurrowESP() {
        super("BurrowESP", "See who is in a burrow.", Category.RENDER, false, false, false);
        this.setInstance();
    }

    public static BurrowESP getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new BurrowESP();
        }
        return INSTANCE;
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onTick() {
        this.posList.clear();
        for (EntityPlayer player : BurrowESP.mc.world.playerEntities) {
            BlockPos blockPos = new BlockPos(Math.floor(player.posX), Math.floor(player.posY + 0.2), Math.floor(player.posZ));
            if (BurrowESP.mc.world.getBlockState(blockPos).getBlock() != Blocks.ENDER_CHEST && BurrowESP.mc.world.getBlockState(blockPos).getBlock() != Blocks.OBSIDIAN || !(blockPos.distanceSq(BurrowESP.mc.player.posX, BurrowESP.mc.player.posY, BurrowESP.mc.player.posZ) <= (double)this.range.getValue().intValue()) || blockPos.distanceSq(BurrowESP.mc.player.posX, BurrowESP.mc.player.posY, BurrowESP.mc.player.posZ) <= 1.5 && !this.self.getValue().booleanValue()) continue;
            this.posList.add(blockPos);
        }
    }

    @Override
    public void onRender3D(Render3DEvent event) {
        for (BlockPos blockPos : this.posList) {
            String s = this.textString.getValue().toUpperCase();
            if (this.text.getValue().booleanValue()) {
                this.renderUtil.drawText(blockPos, s, this.rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.outlineAlpha.getValue()));
            }
            RenderUtil.drawBoxESP(blockPos, this.colorSync.getValue() != false ? new Color(ClickGui.getInstance().red.getValue(), ClickGui.getInstance().green.getValue(), ClickGui.getInstance().blue.getValue(), ClickGui.getInstance().alpha.getValue()) : (this.rainbow.getValue() != false ? ColorUtil.rainbow(ClickGui.getInstance().rainbowHue.getValue()) : new Color(this.red.getValue(), this.green.getValue(), this.blue.getValue(), this.outlineAlpha.getValue())), 1.5f, true, true, this.alpha.getValue());
        }
    }
}