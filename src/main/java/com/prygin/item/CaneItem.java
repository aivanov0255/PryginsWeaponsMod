package com.prygin.item;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;

public class CaneItem extends Item {
    public CaneItem(Properties properties) {
        super(properties);
    }

    @Override
    public void hurtEnemy(ItemStack itemStack, LivingEntity target, LivingEntity attacker) {
        double xRatio = attacker.getX() - target.getX();
        double zRatio = attacker.getZ() - target.getZ();

        target.addDeltaMovement(new Vec3(0.5*xRatio, 0.1, 0.5*zRatio));

        target.needsSync = true;
    }
}
