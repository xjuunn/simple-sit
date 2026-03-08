package com.junhsiun.sit

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component

object SimpleSitCommand : CommandRegistrationCallback {
    override fun register(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registryAccess: CommandBuildContext,
        environment: Commands.CommandSelection
    ) {
        dispatcher.register(
            Commands.literal("sit")
                .requires { SimpleSitConfigManager.current().allowCommandSit }
                .executes { context ->
                    val player = context.source.playerOrException
                    if (!SimpleSitConfigManager.current().enabled) {
                        context.source.sendFailure(Component.literal("Simple Sit 当前已被管理员禁用。"))
                        return@executes 0
                    }
                    if (!SeatEntityManager.sitInPlace(player)) {
                        context.source.sendFailure(Component.literal("当前位置无法坐下，或该位置已被占用。"))
                        return@executes 0
                    }
                    1
                }
        )
    }
}
