package fat.vonware.manager;

import fat.vonware.features.Feature;
import net.minecraft.network.Packet;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraft.util.math.Vec3d;

import java.util.ArrayList;
import java.util.List;

public class PacketManager
        extends Feature {
    boolean caughtPlayerPosLook;
    SPacketExplosion pExplosion;
    boolean caughtPExplosion;
    private final List<Packet<?>> noEventPackets = new ArrayList();

    public void sendPacketNoEvent(Packet<?> packet) {
        if (packet != null && !PacketManager.nullCheck()) {
            this.noEventPackets.add(packet);
            PacketManager.mc.player.connection.sendPacket(packet);
        }
    }
    public boolean isValid() {
        try {
            if (PacketManager.mc.player != null && this.caughtPExplosion && this.pExplosion.getStrength() == 6.0f && PacketManager.mc.player.getDistance(this.pExplosion.getX(), this.pExplosion.getY(), this.pExplosion.getZ()) <= 8.0 && this.raytraceCheck()) {
                return true;
            }
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }
        return false;
    }
    public boolean raytraceCheck() {
        try {
            return PacketManager.mc.world.rayTraceBlocks(new Vec3d(PacketManager.mc.player.posX, PacketManager.mc.player.posY, PacketManager.mc.player.posZ), new Vec3d(this.pExplosion.getX(), this.pExplosion.getY(), this.pExplosion.getZ()), false, true, false) == null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return false;
        }
    }
    public boolean getCaughtPPS() {
        return this.caughtPlayerPosLook;
    }

    public boolean shouldSendPacket(Packet<?> packet) {
        if (this.noEventPackets.contains(packet)) {
            this.noEventPackets.remove(packet);
            return false;
        }
        return true;
    }
}

