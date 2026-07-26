package com.prygin.mixin;

import com.prygin.access.HumanoidRenderStateExtension;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(HumanoidRenderState.class)
public abstract class HumanoidRenderStateMixin implements HumanoidRenderStateExtension {

    @Unique
    private ItemStack guns$mainHandStack = ItemStack.EMPTY;

    @Unique
    private ItemStack guns$offHandStack = ItemStack.EMPTY;

    @Override
    public ItemStack guns$getMainHandStack() {
        return this.guns$mainHandStack;
    }

    @Override
    public void guns$setMainHandStack(ItemStack stack) {
        this.guns$mainHandStack = stack;
    }

    @Override
    public ItemStack guns$getOffHandStack() {
        return this.guns$offHandStack;
    }

    @Override
    public void guns$setOffHandStack(ItemStack stack) {
        this.guns$offHandStack = stack;
    }
}