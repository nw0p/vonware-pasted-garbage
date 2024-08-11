package fat.vonware.mixin.mixins.accessor;

import net.minecraft.client.renderer.EntityRenderer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(value = {EntityRenderer.class})
public interface IEntityRenderer {

    @Accessor("rendererUpdateCount")
    int getRendererUpdateCount();


    @Accessor("rainXCoords")
    float[] getRainXCoords();

    @Accessor("rainYCoords")
    float[] getRainYCoords();
    @Invoker(value="setupCameraTransform")
    public void invokeSetupCameraTransform(float var1, int var2);
}
