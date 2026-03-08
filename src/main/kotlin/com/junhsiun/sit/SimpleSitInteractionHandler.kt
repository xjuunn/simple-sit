package com.junhsiun.sit

import net.fabricmc.fabric.api.event.player.UseBlockCallback
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.level.Level
import net.minecraft.world.phys.BlockHitResult

object SimpleSitInteractionHandler : UseBlockCallback {
    override fun interact(player: Player, world: Level, hand: InteractionHand, hitResult: BlockHitResult): InteractionResult {
        val config = SimpleSitConfigManager.current()
        if (!config.enabled || world.isClientSide || hand != InteractionHand.MAIN_HAND) {
            return InteractionResult.PASS
        }
        if (config.requireSneakRightClick && !player.isShiftKeyDown) {
            return InteractionResult.PASS
        }
        if (config.requireEmptyHand && !player.getItemInHand(hand).isEmpty) {
            return InteractionResult.PASS
        }

        val serverPlayer = player as? ServerPlayer ?: return InteractionResult.PASS
        val state = world.getBlockState(hitResult.blockPos)
        if (SeatEntityManager.resolveTargetType(state) == null) {
            return InteractionResult.PASS
        }
        return if (SeatEntityManager.sitOnBlock(serverPlayer, hitResult.blockPos, state)) {
            InteractionResult.SUCCESS
        } else {
            InteractionResult.PASS
        }
    }
}
