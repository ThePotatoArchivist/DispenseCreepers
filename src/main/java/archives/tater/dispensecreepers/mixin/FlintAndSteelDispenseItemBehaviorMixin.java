package archives.tater.dispensecreepers.mixin;

import com.llamalad7.mixinextras.expression.Definition;
import com.llamalad7.mixinextras.expression.Expression;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Coerce;

import net.minecraft.core.dispenser.BlockSource;
import net.minecraft.core.dispenser.OptionalDispenseItemBehavior;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.EntitySelector;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.DispenserBlock;
import net.minecraft.world.phys.AABB;

@Mixin(targets = "net.minecraft.core.dispenser.DispenseItemBehavior$5")
public class FlintAndSteelDispenseItemBehaviorMixin {
    @SuppressWarnings("InvalidInjectorMethodSignature") // gets mad at the coerce for some reason
    @Definition(id = "setSuccess", method = "Lnet/minecraft/core/dispenser/DispenseItemBehavior$5;setSuccess(Z)V")
    @Expression("?.setSuccess(false)")
    @WrapOperation(
            method = "execute",
            at = @At("MIXINEXTRAS:EXPRESSION")
    )
    private void tryIgniteCreeper(@Coerce OptionalDispenseItemBehavior instance, boolean success, Operation<Void> original, final BlockSource source, final ItemStack dispensed) {
        if (success) return;

        for (var creeper : source.level().getEntitiesOfClass(Creeper.class, new AABB(source.pos().relative(source.state().getValue(DispenserBlock.FACING))), EntitySelector.NO_SPECTATORS)) {
            if (creeper.isIgnited()) continue;
            source.level().playSound(null, creeper.getX(), creeper.getY(), creeper.getZ(), SoundEvents.FLINTANDSTEEL_USE, creeper.getSoundSource(), 1.0F, creeper.getRandom().nextFloat() * 0.4F + 0.8F);
            creeper.ignite();
            original.call(instance, true);
            return;
        }

        original.call(instance, false);
    }

}
