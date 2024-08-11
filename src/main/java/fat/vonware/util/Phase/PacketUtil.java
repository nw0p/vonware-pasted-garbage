package fat.vonware.util.Phase;

import fat.vonware.util.MovementUtil;
import fat.vonware.util.Util;
import net.minecraft.network.*;
import net.minecraft.client.network.*;

import net.minecraft.network.play.client.*;


public class PacketUtil implements Util {
    public static void send(final Packet<?> packet) {
        final NetHandlerPlayClient connection = PacketUtil.mc.getConnection();
        if (connection != null) {
            connection.sendPacket((Packet) packet);
        }
    }

    public static void rotate(final float[] rotations) {
        rotate(rotations, PacketUtil.mc.player.onGround);
    }

    public static void rotate(final float[] rotations, final boolean ground) {
        final boolean isMoving = MovementUtil.isMoving();
        final CPacketPlayer packet = (CPacketPlayer) (isMoving ? new CPacketPlayer.PositionRotation(PacketUtil.mc.player.posX, PacketUtil.mc.player.posY, PacketUtil.mc.player.posZ, rotations[0], rotations[1], ground) : new CPacketPlayer.Rotation(rotations[0], rotations[1], ground));
        send((Packet<?>) packet);
    }
}




