package fat.vonware.features.modules.movement;

import fat.vonware.event.events.MoveEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import fat.vonware.util.PositionUtil;
import fat.vonware.util.holeesp.Hole;
import fat.vonware.util.holeesp.HoleUtil;
import fat.vonware.util.EntityUtil;
import fat.vonware.util.PlayerUtil;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


public class Anchor extends Module
{
    private final Setting<Integer> pitch = this.register(new Setting<Integer>("Pitch", 37, 0, 90));
    private final Setting<Boolean> doubles = this.register(new Setting<Boolean>("Doubles", true));
    private final Setting<Boolean> stopMotion = this.register(new Setting<Boolean>("StopMotion", false));
    private final Setting<Boolean> pullDown = this.register(new Setting<Boolean>("PullDown", false));
    private final Setting<Boolean> terrain = this.register(new Setting<Boolean>("Terrain", false));

    protected boolean anchoring;

    public Anchor() {
        super("Anchor", "Pulls you down into holes", Category.MOVEMENT, true, false,false);
         this.anchoring = false;
        }

    public void onEnable() {
        this.anchoring = false;
    }
    @SubscribeEvent
    public void onMove(final MoveEvent event) {
        if (PlayerUtil.isNull()) {
            return;
        }
        if (mc.player.rotationPitch < (int)pitch.getValue()) {
            anchoring = false;
            return;
        }
        final BlockPos playerPos = PositionUtil.getPosition();
        final IBlockState headState = mc.world.getBlockState(playerPos.up());
        boolean head = false;
        if (headState.getBlock() != Blocks.AIR) {
            head = (headState.getBoundingBox((IBlockAccess)mc.world, playerPos).offset(playerPos).maxY > mc.player.posY);
        }
        if (EntityUtil.isInBurrow((EntityPlayer)mc.player) || HoleUtil.isHole(PositionUtil.getPosition()) || head) {
            anchoring = false;
            return;
        }
        for (int height = 5, i = 0; i <= height; ++i) {
            if (mc.world.isAirBlock(playerPos.down(i + 1))) {
                anchoring = false;
            }
            else {
                final Hole hole = HoleUtil.getHole(playerPos.down(i), (boolean)doubles.getValue(), (boolean)terrain.getValue());
                if (hole == null) {
                    anchoring = false;
                    break;
                }
                final Vec3d vec = HoleUtil.getCenter(hole);
                if (stopMotion.getValue()) {
                    mc.player.motionX = 0.0;
                    mc.player.motionZ = 0.0;
                    mc.player.movementInput.moveForward = 0.0f;
                    mc.player.movementInput.moveStrafe = 0.0f;
                }
                if (mc.player.motionY > -0.1 && (boolean)pullDown.getValue()) {
                    mc.player.motionY = -0.1;
                }
                final double xSpeed = vec.x - mc.player.posX;
                final double zSpeed = vec.z - mc.player.posZ;
                event.setX(xSpeed / 2.0);
                event.setZ(zSpeed / 2.0);
                anchoring = true;
            }
        }
    }

    public boolean isAnchoring() {
        return this.isEnabled() && this.anchoring;
    }
}