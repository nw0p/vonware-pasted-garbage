package fat.vonware.features.modules.render;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import fat.vonware.event.events.PacketEvent;
import fat.vonware.features.modules.Module;
import fat.vonware.features.setting.Setting;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.BossInfoClient;
import net.minecraft.client.gui.GuiBossOverlay;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.passive.EntityBat;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.server.SPacketTimeUpdate;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.BossInfo;
import net.minecraft.world.GameType;
import net.minecraft.world.World;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.event.entity.PlaySoundAtEntityEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

public class NoRender
        extends Module {


    public Setting<Boolean> items = this.register(new Setting<Boolean>("Items", Boolean.valueOf(false), "Removes items on the ground."));
    public Setting<Fog> fog = this.register(new Setting<Fog>("Fog", Fog.NONE, "Removes Fog."));
    public Setting<Boolean> noWeather = this.register(new Setting<Boolean>("Weather", Boolean.valueOf(false), "AntiWeather"));
   public Setting<NoArmor> noArmor = this.register(new Setting<NoArmor>("NoArmor", NoArmor.NONE, "Doesnt Render Armor on players."));
    public Setting<Boolean> glint = this.register(new Setting<Object>("Glint", Boolean.valueOf(false), v -> this.noArmor.getValue() != NoArmor.NONE));

    public Setting<Boolean> barriers = this.register(new Setting<Boolean>("Barriers", Boolean.valueOf(false), "Barriers"));
    public Setting<Boolean> blocks = this.register(new Setting<Boolean>("Blocks", Boolean.valueOf(false), "Blocks"));
    private static NoRender INSTANCE = new NoRender();

    public NoRender() {
        super("NoRender", "Allows you to stop rendering stuff", Module.Category.RENDER, true, false, false);
        this.setInstance();
    }

    private void setInstance() {
        INSTANCE = this;
    }

    @Override
    public void onUpdate() {
        if (this.items.getValue().booleanValue()) {
            NoRender.mc.world.loadedEntityList.stream().filter(EntityItem.class::isInstance).map(EntityItem.class::cast).forEach(Entity::setDead);
        }
        if (this.noWeather.getValue().booleanValue() && NoRender.mc.world.isRaining()) {
            NoRender.mc.world.setRainStrength(0.0f);
        }
    }

    public void doVoidFogParticles(int posX, int posY, int posZ) {
        int i = 32;
        Random random = new Random();
        ItemStack itemstack = NoRender.mc.player.getHeldItemMainhand();
        boolean flag = this.barriers.getValue() == false || NoRender.mc.playerController.getCurrentGameType() == GameType.CREATIVE && !itemstack.isEmpty() && itemstack.getItem() == Item.getItemFromBlock((Block)Blocks.BARRIER);
        BlockPos.MutableBlockPos blockpos$mutableblockpos = new BlockPos.MutableBlockPos();
        for (int j = 0; j < 667; ++j) {
            this.showBarrierParticles(posX, posY, posZ, 16, random, flag, blockpos$mutableblockpos);
            this.showBarrierParticles(posX, posY, posZ, 32, random, flag, blockpos$mutableblockpos);
        }
    }

    public void showBarrierParticles(int x, int y, int z, int offset, Random random, boolean holdingBarrier, BlockPos.MutableBlockPos pos) {
        int i = x + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);
        int j = y + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);
        int k = z + NoRender.mc.world.rand.nextInt(offset) - NoRender.mc.world.rand.nextInt(offset);
        pos.setPos(i, j, k);
        IBlockState iblockstate = NoRender.mc.world.getBlockState((BlockPos)pos);
        iblockstate.getBlock().randomDisplayTick(iblockstate, (World)NoRender.mc.world, (BlockPos)pos, random);
        if (!holdingBarrier && iblockstate.getBlock() == Blocks.BARRIER) {
            NoRender.mc.world.spawnParticle(EnumParticleTypes.BARRIER, (double)((float)i + 0.5f), (double)((float)j + 0.5f), (double)((float)k + 0.5f), 0.0, 0.0, 0.0, new int[0]);
        }
    }



    public static NoRender getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new NoRender();
        }
        return INSTANCE;
    }

    public static class Pair<T, S> {
        private T key;
        private S value;

        public Pair(T key, S value) {
            this.key = key;
            this.value = value;
        }

        public T getKey() {
            return this.key;
        }

        public S getValue() {
            return this.value;
        }

        public void setKey(T key) {
            this.key = key;
        }

        public void setValue(S value) {
            this.value = value;
        }
    }

    public static enum NoArmor {
        NONE,
        ALL,
        HELMET;

    }

    public static enum Fog {
        NONE,
        AIR,
        NOFOG;

    }
}

