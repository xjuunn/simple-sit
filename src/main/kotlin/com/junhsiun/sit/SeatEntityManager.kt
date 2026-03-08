package com.junhsiun.sit

import net.minecraft.core.BlockPos
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerLevel
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.decoration.ArmorStand
import net.minecraft.world.level.block.BedBlock
import net.minecraft.world.level.block.CarpetBlock
import net.minecraft.world.level.block.SlabBlock
import net.minecraft.world.level.block.StairBlock
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.phys.AABB
import net.minecraft.world.phys.Vec3
import java.util.UUID

object SeatEntityManager {
    private const val SEAT_TAG = "simple_sit_seat"
    private val seatedPlayers = HashMap<UUID, SeatSession>()

    data class SeatSession(
        val level: ServerLevel,
        val seatId: UUID,
        val anchor: Vec3
    )

    fun sitOnBlock(player: ServerPlayer, pos: BlockPos, state: BlockState): Boolean {
        val targetType = resolveTargetType(state) ?: return false
        val seatPos = seatPositionForBlock(pos, targetType)
        val rotation = facingForTarget(player, state, targetType)
        return sit(player, seatPos, targetType, rotation)
    }

    fun sitInPlace(player: ServerPlayer): Boolean {
        val seatPos = Vec3(player.x, player.y - 0.35, player.z)
        return sit(player, seatPos, SitTargetType.COMMAND, player.yRot)
    }

    fun resolveTargetType(state: BlockState): SitTargetType? {
        val config = SimpleSitConfigManager.current()
        return when {
            config.allowCarpets && state.block is CarpetBlock -> SitTargetType.CARPET
            config.allowStairs && state.block is StairBlock -> SitTargetType.STAIRS
            config.allowSlabs && state.block is SlabBlock -> SitTargetType.SLAB
            config.allowBeds && state.block is BedBlock -> SitTargetType.BED
            else -> null
        }
    }

    fun tickServer(server: MinecraftServer) {
        val iterator = seatedPlayers.entries.iterator()
        while (iterator.hasNext()) {
            val (playerId, session) = iterator.next()
            val player = server.playerList.getPlayer(playerId)
            if (player == null || !player.isAlive) {
                cleanupSeat(session)
                iterator.remove()
                continue
            }

            val vehicle = player.vehicle
            if (vehicle !is ArmorStand || !isSeatEntity(vehicle) || vehicle.uuid != session.seatId) {
                cleanupSeat(session)
                iterator.remove()
                continue
            }

            val input = player.lastClientInput
            val moveIntent = player.lastClientMoveIntent
            val wantsToStand = input.jump() || moveIntent.x != 0.0 || moveIntent.z != 0.0
            if (player.isShiftKeyDown || wantsToStand || player.distanceToSqr(session.anchor) > 1.25) {
                player.stopRiding()
                cleanupSeat(session)
                iterator.remove()
                continue
            }

            vehicle.setPos(session.anchor)
            vehicle.deltaMovement = Vec3.ZERO
        }
    }

    fun cleanupServer(server: MinecraftServer) {
        seatedPlayers.values.forEach(::cleanupSeat)
        seatedPlayers.clear()
        server.allLevels.forEach { level ->
            level.getEntitiesOfClass(ArmorStand::class.java, level.worldBorder.getCollisionShape().bounds())
                .filter(::isSeatEntity)
                .forEach(ArmorStand::discard)
        }
    }

    private fun sit(player: ServerPlayer, seatPos: Vec3, targetType: SitTargetType, rotation: Float): Boolean {
        if (!SimpleSitConfigManager.current().enabled) {
            return false
        }
        if (player.isPassenger) {
            return false
        }
        if (isSeatOccupied(player.level(), seatPos)) {
            return false
        }

        val seat = ArmorStand(player.level(), seatPos.x, seatPos.y, seatPos.z)
        seat.isInvisible = true
        seat.isInvulnerable = true
        seat.isNoGravity = true
        seat.isSilent = true
        seat.tags.add(SEAT_TAG)
        seat.setNoBasePlate(true)
        seat.yRot = rotation
        player.level().addFreshEntity(seat)

        if (!player.startRiding(seat, false, false)) {
            seat.discard()
            return false
        }

        if (targetType == SitTargetType.STAIRS || targetType == SitTargetType.BED) {
            player.yRot = seat.yRot
            player.yHeadRot = seat.yRot
        }

        seatedPlayers[player.uuid] = SeatSession(player.level(), seat.uuid, seatPos)
        return true
    }

    private fun isSeatOccupied(level: ServerLevel, seatPos: Vec3): Boolean {
        return level.getEntitiesOfClass(ArmorStand::class.java, AABB.ofSize(seatPos, 0.3, 0.3, 0.3))
            .any { isSeatEntity(it) && it.passengers.isNotEmpty() }
    }

    private fun cleanupSeat(session: SeatSession) {
        val entity = session.level.getEntity(session.seatId)
        if (entity is ArmorStand && isSeatEntity(entity)) {
            entity.discard()
        }
    }

    private fun isSeatEntity(entity: ArmorStand): Boolean = entity.tags.contains(SEAT_TAG)

    private fun seatPositionForBlock(pos: BlockPos, targetType: SitTargetType): Vec3 {
        val center = Vec3.atCenterOf(pos)
        return when (targetType) {
            SitTargetType.CARPET -> center.add(0.0, -0.55, 0.0)
            SitTargetType.STAIRS -> center.add(0.0, -0.1, 0.0)
            SitTargetType.SLAB -> center.add(0.0, -0.3, 0.0)
            SitTargetType.BED -> center.add(0.0, -0.15, 0.0)
            SitTargetType.COMMAND -> center.add(0.0, -0.35, 0.0)
        }
    }

    private fun facingForTarget(player: ServerPlayer, state: BlockState, targetType: SitTargetType): Float {
        return when (targetType) {
            SitTargetType.STAIRS -> if (state.block is StairBlock) state.getValue(StairBlock.FACING).toYRot() else player.yRot
            SitTargetType.BED -> if (state.block is BedBlock) state.getValue(BedBlock.FACING).toYRot() else player.yRot
            else -> player.yRot
        }
    }
}
