package fat.vonware.features.modules.movement;

import fat.vonware.event.events.CollisionEvent;
import fat.vonware.event.events.MoveEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.util.MovementUtil;
import fat.vonware.util.EntityUtil;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.Phase.PacketUtil;
import fat.vonware.util.Timer;
import net.minecraft.network.play.client.*;
import net.minecraft.network.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Phase extends Module
{
    public Setting<Float> speed = this.register(new Setting<Float>("Speed", 3.0f, 1.0f,10.0f));
    public Setting<Float> delay = this.register(new Setting<Float>("Delay", 3.0f, 0.0f,5.0f));
    public Setting<Float> shiftDelay = this.register(new Setting<Float>("ShiftDelay", 4.0f, 0.0f,5.0f));
    public Setting<Boolean> shiftDown = this.register(new Setting<Boolean>("ShiftDown", true));
    public Setting<Boolean> antiVoid = this.register(new Setting<Boolean>("AntiVoid", true));
    public Setting<Boolean> collisions = this.register(new Setting<Boolean>("Collisions", true));
    private final Setting<PacketType> type;
    protected final Timer timer;
    protected final Timer shiftTimer;

    public Phase() {
        super("Phase","", Category.MOVEMENT, true, false ,false);
        this.type = this.register(new Setting("Phase Mode", PacketType.NORMAL));
        this.timer = new Timer();
        this.shiftTimer = new Timer();
    }
    public enum PacketType
    {
        NORMAL(1337.0),
        AUTISM(777.0),
        ZERO(0.0),
        BLACK(-666.0);

        private final double yPos;

        private PacketType(final double yPos) {
            this.yPos = yPos;
        }

        public double getYPos() {
            return this.yPos;
        }
    }
    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (mc.player.collidedHorizontally && EntityUtil.isPhasing() && timer.passed((long)((float)delay.getValue() * 100.0f)) && !mc.player.isOnLadder()) {
            final double[] movement = MovementUtil.directionSpeed((double)((float)speed.getValue() / 100.0f));
            final double xSpeed = mc.player.posX + movement[0];
            final double zSpeed = mc.player.posZ + movement[1];
            mc.player.setPosition(xSpeed, mc.player.posY, zSpeed);
            sendPackets(xSpeed, mc.player.posY, zSpeed);
            mc.player.motionX = 0.0;
            mc.player.motionY = 0.0;
            event.setX(mc.player.motionZ = 0.0);
            event.setY(0.0);
            event.setZ(0.0);
            timer.reset();
        }
    }
    @SubscribeEvent
    public void onCollision(final CollisionEvent event) {
        if (!(boolean)collisions.getValue() && MovementUtil.isMoving() && EntityUtil.isPhasing() && event.getEntity() == mc.player) {
            event.setBB((AxisAlignedBB)null);
        }
    }
    @SubscribeEvent
    public void Move(final MoveEvent event) {
        if (!(boolean)collisions.getValue()) {
            mc.player.motionY = 0.0;
        }
        if (mc.gameSettings.keyBindSneak.isKeyDown() && EntityUtil.isPhasing() && (boolean)shiftDown.getValue() && mc.player.collidedVertically && shiftTimer.passed((long)((float)shiftDelay.getValue() * 100.0f))) {
            if ((boolean)antiVoid.getValue() && mc.player.posY == 1.0) {
                return;
            }
            final double offset = mc.player.posY - 0.003;
            mc.player.setPosition(mc.player.posX, offset, mc.player.posZ);
            sendPackets(mc.player.posX, offset, mc.player.posZ);
            mc.player.motionX = 0.0;
            mc.player.motionY = 0.0;
            event.setX(mc.player.motionZ = 0.0);
            event.setY(0.0);
            event.setZ(0.0);
            shiftTimer.reset();
        }
    }

    protected void sendPackets(final double x, final double y, final double z) {
        PacketUtil.send((Packet)new CPacketPlayer.Position(x, y, z, true));
        if (!Phase.mc.isSingleplayer()) {
            PacketUtil.send((Packet)new CPacketPlayer.Position(Phase.mc.player.posX, ((PacketType)this.type.getValue()).getYPos(), Phase.mc.player.posZ, true));
        }
    }
}
