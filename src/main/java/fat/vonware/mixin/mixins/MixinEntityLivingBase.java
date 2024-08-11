package fat.vonware.mixin.mixins;


import fat.vonware.Vonware;
import fat.vonware.features.modules.render.SwingSpeed;
import net.minecraft.entity.EntityLivingBase;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityLivingBase.class)
public class MixinEntityLivingBase {
    @Inject(method = "getArmSwingAnimationEnd",at = @At(value = "HEAD"), cancellable = true)
    public void getArmSwingAnimationEndHook(CallbackInfoReturnable<Integer> cir) {
        int stuff = Vonware.moduleManager.getModuleByClass(SwingSpeed.class).isEnabled() ? SwingSpeed.changeSwing.getValue() : 6;
        cir.setReturnValue(stuff);
    }
}