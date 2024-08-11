package fat.vonware.features.modules.movement;

import fat.vonware.Vonware;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import fat.vonware.features.command.Command;
import fat.vonware.event.events.PacketEvent;
import fat.vonware.util.EntityUtil;
import fat.vonware.util.Timer;
import fat.vonware.util.HoleUtilSafety;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec2f;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Comparator;

public class Holesnap
        extends Module {
    Timer timer = new Timer();;
    public HoleUtilSafety.Hole holes;
    public Setting<Mode> mode = this.register(new Setting<Mode>("SnapMode", Mode.Motion));
    private final Setting<Float> range2 = this.register(new Setting<Float>("Motion Range", Float.valueOf(4.0f), Float.valueOf(0.1f), Float.valueOf(10.0f), f -> this.mode.getValue() == Mode.Motion));
    public Setting<Float> timerfactor = this.register(new Setting<Float>("Timer", Float.valueOf(1.0f), Float.valueOf(1.0f), Float.valueOf(5.0f), f -> this.mode.getValue() == Mode.Motion));
    private Setting<Boolean> motionstop = this.register(new Setting<Boolean>("StopMotion", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.Motion));
    private final Setting<Float> range = this.register(new Setting<Float>("Instant Range", Float.valueOf(0.5f), Float.valueOf(0.1f), Float.valueOf(5.0f), f -> this.mode.getValue() == Mode.Instant));
    private final Setting<Boolean> InstantSpeedCheck = this.register(new Setting<Boolean>("Disable InstantSpeed", Boolean.valueOf(true), v -> this.mode.getValue() == Mode.Motion));
    private int ticks = 0;

    public Holesnap() {
        super("HoleSnap", "Teleport to Hole", Module.Category.MOVEMENT, true, false, false);
    }

    @Override
    public void onTick() {
        BlockPos blockPos2;
        if (this.mode.getValue() == Mode.Instant) {
            blockPos2 = Vonware.holeManager.calcHoles().stream().min(Comparator.comparing(blockPos -> Holesnap.mc.player.getDistance((double)blockPos.getX(), (double)blockPos.getY(), (double)blockPos.getZ()))).orElse(null);
            if (blockPos2 != null) {
                if (Holesnap.mc.player.getDistance((double)blockPos2.getX(), (double)blockPos2.getY(), (double)blockPos2.getZ()) < (double)this.range.getValue().floatValue() + 1.5) {
                    Holesnap.mc.player.setPosition((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
                    Holesnap.mc.player.setPosition((double)blockPos2.getX() + 0.5, (double)blockPos2.getY(), (double)blockPos2.getZ() + 0.5);
                    Command.sendMessage("Accepting Teleport");
                } else {
                    Command.sendMessage("Out of range. disabling HoleSnap");
                }
            } else {
                Command.sendMessage("Unable to find hole, disabling HoleSnap");
            }
            this.disable();
        }
        if (this.mode.getValue() == Mode.Motion) {
            if (Holesnap.fullNullCheck()) {
                return;
            }
            if (EntityUtil.isInLiquid()) {
                this.disable();
                return;
            }

            Vonware.timerManager.set(timerfactor.getValue());
            this.holes = Holesnap.getTargetHoleVec3D(this.range2.getValue().floatValue());
            if (this.holes == null) {
                Command.sendMessage("Unable to find hole, disabling HoleSnap");
                this.disable();
                return;
            }
            if (this.timer.passedMs(500L)) {
                this.disable();
                return;
            }
            if (HoleUtilSafety.isObbyHole(Holesnap.getPlayerPos()) || HoleUtilSafety.isBedrockHoles(Holesnap.getPlayerPos())) {
                this.disable();
                return;
            }
            if (Holesnap.mc.world.getBlockState(this.holes.pos1).getBlock() != Blocks.AIR) {
                this.disable();
                return;
            }
            blockPos2 = this.holes.pos1;
            Vec3d vec3d = Holesnap.mc.player.getPositionVector();
            Vec3d vec3d2 = new Vec3d((double)blockPos2.getX() + 0.5, Holesnap.mc.player.posY, (double)blockPos2.getZ() + 0.5);
            double d = Math.toRadians(Holesnap.getRotationTo((Vec3d)vec3d, (Vec3d)vec3d2).x);
            double d2 = vec3d.distanceTo(vec3d2);
            double d3 = Holesnap.mc.player.onGround ? -Math.min(0.2805, d2 / 2.0) : -EntityUtil.getMaxSpeed() + 0.02;
            Holesnap.mc.player.motionX = -Math.sin(d) * d3;
            Holesnap.mc.player.motionZ = Math.cos(d) * d3;
        }
    }

    @Override
    public void onDisable() {
        this.timer.reset();
        this.holes = null;
        Holesnap.mc.timer.tickLength = 50.0f;
    }

    @Override
    public void onEnable() {
        if (this.mode.getValue() == Mode.Motion && this.motionstop.getValue().booleanValue()) {
            Holesnap.mc.player.motionX = 0.0;
            Holesnap.mc.player.motionZ = 0.0;
        }
        if (this.InstantSpeedCheck.getValue().booleanValue() && InstantSpeed.getInstance().isOn()) {
            InstantSpeed.getInstance().disable();
            Command.sendMessage("<HoleSnap> Disabling InstantSpeed");
        }
        if (Holesnap.fullNullCheck()) {
            return;
        }
        this.timer.reset();
        this.holes = null;
    }

    public static HoleUtilSafety.Hole getTargetHoleVec3D(double d) {
        return HoleUtilSafety.getHoles(d, Holesnap.getPlayerPos(), false).stream().filter(hole -> Holesnap.mc.player.getPositionVector().distanceTo(new Vec3d((double)hole.pos1.getX() + 0.5, Holesnap.mc.player.posY, (double)hole.pos1.getZ() + 0.5)) <= d).min(Comparator.comparingDouble(hole -> Holesnap.mc.player.getPositionVector().distanceTo(new Vec3d((double)hole.pos1.getX() + 0.5, Holesnap.mc.player.posY, (double)hole.pos1.getZ() + 0.5)))).orElse(null);
    }

    public static BlockPos getPlayerPos() {
        double d = Holesnap.mc.player.posY - Math.floor(Holesnap.mc.player.posY);
        return new BlockPos(Holesnap.mc.player.posX, d > 0.8 ? Math.floor(Holesnap.mc.player.posY) + 1.0 : Math.floor(Holesnap.mc.player.posY), Holesnap.mc.player.posZ);
    }

    public static Vec2f getRotationTo(Vec3d vec3d, Vec3d vec3d2) {
        return Holesnap.getRotationFromVec(vec3d.subtract(vec3d2));
    }

    public static Vec2f getRotationFromVec(Vec3d vec3d) {
        double d = Math.hypot(vec3d.x, vec3d.z);
        float f = (float) Holesnap.normalizeAngle(Math.toDegrees(Math.atan2(vec3d.z, vec3d.x)) - 90.0);
        float f2 = (float) Holesnap.normalizeAngle(Math.toDegrees(-Math.atan2(vec3d.y, d)));
        return new Vec2f(f, f2);
    }

    public static double normalizeAngle(Double d) {
        double d2 = 0.0;
        double d3 = d;
        d3 %= 360.0;
        if (d2 >= 180.0) {
            d3 -= 360.0;
        }
        if (d3 < -180.0) {
            d3 += 360.0;
        }
        return d3;
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive receive) {
        if (this.isDisabled()) {
            return;
        }
        if (receive.getPacket() instanceof SPacketPlayerPosLook) {
            this.disable();
            return;
        }
    }

    public static enum Mode {
        Instant,
        Motion;
    }
}