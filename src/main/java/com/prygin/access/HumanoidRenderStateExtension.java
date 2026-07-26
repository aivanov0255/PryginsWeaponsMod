package com.prygin.access;

import net.minecraft.world.item.ItemStack;

public interface HumanoidRenderStateExtension {
    ItemStack guns$getMainHandStack();

    void guns$setMainHandStack(ItemStack stack);

    ItemStack guns$getOffHandStack();

    void guns$setOffHandStack(ItemStack stack);
}