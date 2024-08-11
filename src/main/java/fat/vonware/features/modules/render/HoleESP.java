package fat.vonware.features.modules.render;

import fat.vonware.event.events.Render3DEvent;
import fat.vonware.features.command.Command;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.*;
import java.util.*;
import java.awt.*;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import fat.vonware.util.holeesp.Hole;
import fat.vonware.util.holeesp.HoleUtil;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.entity.Entity;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.*;

public class HoleESP extends Module {

    public enum Outline {
        NORMAL,
        CROSS
    }

    protected final Setting<Integer> range;
    protected final Setting<Boolean> twoByOne;
    protected final Setting<Integer> alpha;
    protected final Setting<Float> height;
    protected final Setting<Boolean> fadeAlpha;
    public Setting<Boolean> gradient;
    public Setting<Boolean> gradientEdges;
    protected final Setting<Outline> outlineMode;
    protected final Setting<Integer> wireAlpha;
    protected final Setting<Float> lineWidth;
    public Setting<Boolean> voids;
    protected final Setting<Integer> voidRange;
    public Setting<Boolean> traps;
    public Setting<Boolean> terrain;

    public Setting<Integer> obbyRed = this.register(new Setting("Obby Red", 160, 0, 255));
    public Setting<Integer> obbyGreen = this.register(new Setting("Obby Green", 0, 0, 255));
    public Setting<Integer> obbyBlue = this.register(new Setting("Obby Blue", 0, 0, 255));

    public Setting<Integer> mixedRed = this.register(new Setting("Mixed Red", 160, 0, 255));
    public Setting<Integer> mixedGreen = this.register(new Setting("Mixed Green", 0, 0, 255));
    public Setting<Integer> mixedBlue = this.register(new Setting("Mixed Blue", 0, 0, 255));

    public Setting<Integer> bedrockRed = this.register(new Setting("Bedrock Red", 0, 0, 255));
    public Setting<Integer> bedrockGreen = this.register(new Setting("Bedrock Green", 160, 0, 255));
    public Setting<Integer> bedrockBlue = this.register(new Setting("Bedrock Blue", 0, 0, 255));

    public Setting<Integer> voidRed = this.register(new Setting("Void Red", 198, 0, 255, v -> this.voids.getValue()));
    public Setting<Integer> voidGreen = this.register(new Setting("Void Green", 142, 0, 255, v -> this.voids.getValue()));
    public Setting<Integer> voidBlue = this.register(new Setting("Void Blue", 247, 0, 255, v -> this.voids.getValue()));

    public Setting<Integer> trapRed = this.register(new Setting("Trap Red", 0, 0, 255, v -> this.traps.getValue()));
    public Setting<Integer> trapGreen = this.register(new Setting("Trap Green", 0, 0, 255, v -> this.traps.getValue()));
    public Setting<Integer> trapBlue = this.register(new Setting("Trap Blue", 0, 0, 255, v -> this.traps.getValue()));

    public Setting<Integer> terrainRed = this.register(new Setting("Terrain Red", 0, 0, 255, v -> this.terrain.getValue()));
    public Setting<Integer> terrainGreen = this.register(new Setting("Terrain Green", 0, 0, 255, v -> this.terrain.getValue()));
    public Setting<Integer> terrainBlue = this.register(new Setting("Terrain Blue", 0, 0, 255, v -> this.terrain.getValue()));
    protected List<Hole> holes;
    protected List<BlockPos> voidHoles;
    public ExecutorService executorService;

    public HoleESP() {
        super("HoleESP", "Highlights safe holes", Category.RENDER, true, false, false);
        this.range = this.register(new Setting<>("Range", 10, 1, 50));
        this.twoByOne = this.register(new Setting("Doubles", false));
        this.alpha = this.register(new Setting<>("Box Alpha", 21, 0, 255));
        this.height = this.register(new Setting<>("Height", 0.05f, -1.0f, 1.0f));
        this.fadeAlpha = this.register(new Setting("Fade Alpha", false));
        this.gradient = this.register(new Setting("Gradient", true));
        this.gradientEdges = this.register(new Setting("Gradient Edges", false, v -> this.gradient.getValue()));
        this.outlineMode = this.register(new Setting("Outline Mode", Outline.NORMAL));
        this.wireAlpha = this.register(new Setting<>("Line Alpha", 125, 0, 255));
        this.lineWidth = this.register(new Setting<>("Line Width", 1.0f, 0.1f, 4.0f));
        this.voids = this.register(new Setting("Void Holes", false));
        this.voidRange = this.register(new Setting<>("Void Range", 16, 1, 50));
        this.traps = this.register(new Setting("Trapped Holes", false));
        this.terrain = this.register(new Setting("Terrain Holes", false));
        this.holes = new ArrayList<Hole>();
        this.voidHoles = new ArrayList<BlockPos>();
        this.executorService = Executors.newCachedThreadPool();
    }

    protected Color getBedrockColor() {
        return new Color(bedrockRed.getValue(), bedrockGreen.getValue(), bedrockBlue.getValue());
    }

    protected Color getObbyColor() {
        return new Color(obbyRed.getValue(), obbyGreen.getValue(), obbyBlue.getValue());
    }

    protected Color getVoidColor() {
        return new Color(voidRed.getValue(), voidGreen.getValue(), voidBlue.getValue());
    }

    protected Color getMixedColor() {
        return new Color(mixedRed.getValue(), mixedGreen.getValue(), mixedBlue.getValue());
    }

    protected Color getTrapColor() {
        return new Color(trapRed.getValue(), trapGreen.getValue(), trapBlue.getValue());
    }

    protected Color getTerrainColor() {
        return new Color(terrainRed.getValue(), terrainGreen.getValue(), terrainBlue.getValue());
    }

    @Override
    public void onUpdate() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        executorService.submit(() -> {
            this.holes = HoleUtil.getHoles(mc.player, (float)(int)this.range.getValue(), (boolean)this.twoByOne.getValue(), false, (boolean)this.traps.getValue(), (boolean)this.terrain.getValue());
            if (this.voids.getValue()) {
                this.voidHoles = this.getVoidHoles();
            }
        });
    }

    @Override
    public void onRender3D(final Render3DEvent event) {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (!this.holes.isEmpty()) {
            for (Hole hole : this.holes) {
                if (hole == null) {
                    continue;
                }
                if (!this.isInFrustum(hole.getFirst())) {
                    continue;
                }
                Color color = new Color(-1);
                switch (hole.getSafety()) {
                    case OBBY: {
                        color = this.getObbyColor();
                        break;
                    }
                    case MIXED: {
                        color = this.getMixedColor();
                        break;
                    }
                    case BEDROCK: {
                        color = this.getBedrockColor();
                        break;
                    }
                    case TRAPPED: {
                        color = this.getTrapColor();
                        break;
                    }
                    case TERRAIN: {
                        color = this.getTerrainColor();
                        break;
                    }
                }
                AxisAlignedBB bb = Interpolation.interpolatePos(hole.getFirst(), HoleUtil.isTrapHole(hole.getFirst()) ? 2.0f : ((float)this.height.getValue()));
                if (hole.getSecond() != null) {
                    bb = new AxisAlignedBB(hole.getFirst().getX() - mc.getRenderManager().viewerPosX, hole.getFirst().getY() - mc.getRenderManager().viewerPosY, hole.getFirst().getZ() - mc.getRenderManager().viewerPosZ, hole.getSecond().getX() + 1 - mc.getRenderManager().viewerPosX, hole.getSecond().getY() + (float)this.height.getValue() - mc.getRenderManager().viewerPosY, hole.getSecond().getZ() + 1 - mc.getRenderManager().viewerPosZ);
                }
                int alpha = (int)this.alpha.getValue();
                if (this.fadeAlpha.getValue()) {
                    final double distance = mc.player.getDistanceSq((double)(hole.getFirst().getX() + 1), (double)hole.getFirst().getY(), (double)(hole.getFirst().getZ() + 1));
                    final double tempAlpha = (MathUtil.square((float)(int)this.range.getValue()) - distance) / MathUtil.square((float)(int)this.range.getValue());
                    if (tempAlpha > 0.0 && tempAlpha < 1.0) {
                        alpha = MathUtil.clamp((int)(tempAlpha * 255.0), 0, 255);
                    }
                }
                if (alpha < 0) {
                    continue;
                }
                final int finalAlpha = this.fadeAlpha.getValue() ? ((int)(alpha * 0.1)) : alpha;
                int lineAlpha = ((boolean)this.fadeAlpha.getValue()) ? (finalAlpha * 4) : ((int)this.wireAlpha.getValue());
                if (gradient.getValue()) {
                    RenderUtil.prepare();
                    RenderUtil.renderBB(7, bb, ColorUtil.changeAlpha(color, finalAlpha), ColorUtil.changeAlpha(color, 0));
                    AxisAlignedBB outlineBB = gradientEdges.getValue() ? bb : new AxisAlignedBB(bb.minX, bb.minY, bb.minZ, bb.maxX, bb.minY + 0.01f, bb.maxZ);
                    GlStateManager.glLineWidth(lineWidth.getValue());
                    RenderUtil.renderBB(3, outlineBB, ColorUtil.changeAlpha(color, lineAlpha), ColorUtil.changeAlpha(color, 0));
                    RenderUtil.release();
                } else {
                    RenderMethods.startRender();
                    RenderMethods.drawBox(bb, ColorUtil.changeAlpha(color, finalAlpha));
                    this.drawOutline(bb, (float)this.lineWidth.getValue(), ColorUtil.changeAlpha(color, lineAlpha));
                    RenderMethods.endRender();
                }
            }
        }
        if (!this.voidHoles.isEmpty() && (boolean)this.voids.getValue()) {
            for (final BlockPos pos : this.voidHoles) {
                if (!this.isInFrustum(pos)) {
                    continue;
                }
                if (mc.player.getPositionVector().y > (int)this.voidRange.getValue()) {
                    continue;
                }
                final AxisAlignedBB bb2 = Interpolation.interpolatePos(pos, (float)this.height.getValue());
                RenderMethods.startRender();
                RenderMethods.drawBox(bb2, ColorUtil.changeAlpha(this.getVoidColor(), (int)this.alpha.getValue()));
                RenderMethods.drawOutline(bb2, (float)this.lineWidth.getValue(), ColorUtil.changeAlpha(this.getVoidColor(), (int)this.wireAlpha.getValue()));
                RenderMethods.endRender();
            }
        }
    }

    public Frustum frustum = new Frustum();

    private boolean isInFrustum(final BlockPos pos) {
        Entity entity = mc.getRenderViewEntity();
        if (entity != null) {
            frustum.setPosition(entity.posX, entity.posY, entity.posZ);
            return frustum.isBoundingBoxInFrustum(new AxisAlignedBB(pos));
        }
        return false;
    }

    protected List<BlockPos> getVoidHoles() {
        final BlockPos playerPos = PositionUtil.getPosition(RenderMethods.getEntity());
        return BlockUtil.getCircle(playerPos.add(0, -playerPos.getY(), 0), (float)(int)this.voidRange.getValue()).stream().filter(this::isVoid).collect(Collectors.toList());
    }

    private boolean isVoid(final BlockPos pos) {
        return (HoleESP.mc.world.getBlockState(pos).getBlock() == Blocks.AIR || HoleESP.mc.world.getBlockState(pos).getBlock() != Blocks.BEDROCK) && pos.getY() < 1 && pos.getY() >= 0;
    }

    protected void drawOutline(final AxisAlignedBB bb, final float with, final Color color) {
        switch (this.outlineMode.getValue()) {
            case NORMAL: {
                RenderMethods.drawOutline(bb, with, color);
                break;
            }
            case CROSS: {
                RenderMethods.drawCross(bb, with, color);
                if (this.height.getValue() != 0.0) {
                    RenderMethods.drawOutline(bb, with, color);
                    break;
                }
                break;
            }
        }
    }
}