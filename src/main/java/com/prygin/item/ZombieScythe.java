package com.prygin.item;

import com.prygin.entity.ModEntityTypes;
import com.prygin.entity.scythe_zombie.ScytheZombie;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ZombieScythe extends Item {
    List<ScytheZombie> zombies;

    public static final int COOLDOWN_TICKS = 600;

    public ZombieScythe(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        player.getCooldowns().addCooldown(stack, COOLDOWN_TICKS);

        this.zombies = summonScytheZombieCircle(player, level, player.blockPosition(), 5, 10);

        return InteractionResult.SUCCESS;
    }

    public List<ScytheZombie> summonScytheZombieCircle(Player player, Level level, BlockPos centerPos, double radius, int zombieCount) {
        if (level.isClientSide()) {
            return null;
        }

        ServerLevel serverLevel = (ServerLevel) level;

        Scoreboard scoreboard = Objects.requireNonNull(level.getServer()).getScoreboard();

        List<ScytheZombie> zombies = new ArrayList<>();

        for (int i = 0; i < zombieCount; i++) {
            double angle = (2 * Math.PI / zombieCount) * i;

            double dirX = Math.cos(angle);
            double dirZ = Math.sin(angle);

            double clampedRadius = radius;
            double stepSize = 0.25;

            for (double dist = stepSize; dist <= radius; dist += stepSize) {
                double checkX = centerPos.getX() + 0.5 + dirX * dist;
                double checkZ = centerPos.getZ() + 0.5 + dirZ * dist;

                BlockPos checkPos = new BlockPos((int) Math.floor(checkX), centerPos.getY(), (int) Math.floor(checkZ));
                BlockPos checkPosAbove = checkPos.above();

                boolean blocked = !level.getBlockState(checkPos).getCollisionShape(level, checkPos).isEmpty()
                        || !level.getBlockState(checkPosAbove).getCollisionShape(level, checkPosAbove).isEmpty();

                if (blocked) {
                    clampedRadius = Math.max(0, dist - stepSize);
                    break;
                }
            }

            double spawnX = centerPos.getX() + 0.5 + dirX * clampedRadius;
            double spawnZ = centerPos.getZ() + 0.5 + dirZ * clampedRadius;

            BlockPos groundPos = findGroundLevel(level, new BlockPos((int) Math.floor(spawnX), centerPos.getY(), (int) Math.floor(spawnZ)));
            double spawnY = groundPos.getY() + 1;

            ScytheZombie zombie = ModEntityTypes.SCYTHE_ZOMBIE.create(serverLevel, (e) -> {
            }, new BlockPos((int) Math.floor(spawnX), (int) spawnY, (int) Math.floor(spawnZ)), EntitySpawnReason.SPAWNER, true, false);

            assert zombie != null;

            zombie.setPos(spawnX, spawnY, spawnZ);

            level.addFreshEntity(zombie);

            zombie.setOwnerUUID(player.getUUID());

            if (scoreboard.getPlayerTeam(player.getName() + ":ScytheZombieScythe") != null) {
                PlayerTeam team = scoreboard.getPlayerTeam(player.getName() + ":ScytheZombieScythe");

                scoreboard.addPlayerToTeam(zombie.getScoreboardName(), team);
            } else {
                PlayerTeam team = scoreboard.addPlayerTeam(player.getName() + ":ScytheZombieScythe");
                team.setDisplayName(Component.empty());
                scoreboard.addPlayerToTeam(player.getScoreboardName(), team);
                scoreboard.addPlayerToTeam(zombie.getScoreboardName(), team);
            }

            zombies.add(zombie);
        }

        return zombies;
    }

    private BlockPos findGroundLevel(Level level, BlockPos startPos) {
        int searchRange = 10;

        BlockPos pos = startPos;

        int upChecks = 0;
        while (!level.getBlockState(pos).getCollisionShape(level, pos).isEmpty() && upChecks < searchRange) {
            pos = pos.above();
            upChecks++;
        }

        int downChecks = 0;
        while (level.getBlockState(pos.below()).getCollisionShape(level, pos.below()).isEmpty() && downChecks < searchRange) {
            pos = pos.below();
            downChecks++;
        }

        return pos.below();
    }
}
