package com.junhsiun

import com.junhsiun.sit.SeatEntityManager
import com.junhsiun.sit.SimpleSitAdminCommand
import com.junhsiun.sit.SimpleSitCommand
import com.junhsiun.sit.SimpleSitConfigManager
import com.junhsiun.sit.SimpleSitInteractionHandler
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents
import net.fabricmc.fabric.api.event.player.UseBlockCallback
import org.slf4j.LoggerFactory

object SimpleSit : ModInitializer {
    const val MOD_ID = "simple-sit"
    val LOGGER = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        SimpleSitConfigManager.load()
        UseBlockCallback.EVENT.register(SimpleSitInteractionHandler)
        CommandRegistrationCallback.EVENT.register(SimpleSitCommand)
        CommandRegistrationCallback.EVENT.register(SimpleSitAdminCommand)
        ServerTickEvents.END_SERVER_TICK.register(SeatEntityManager::tickServer)
        ServerLifecycleEvents.SERVER_STOPPING.register(SeatEntityManager::cleanupServer)
    }
}
