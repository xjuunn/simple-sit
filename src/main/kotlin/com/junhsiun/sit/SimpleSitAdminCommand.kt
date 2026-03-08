package com.junhsiun.sit

import com.mojang.brigadier.CommandDispatcher
import com.mojang.brigadier.arguments.BoolArgumentType
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandBuildContext
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.network.chat.Component
import net.minecraft.server.permissions.Permissions

object SimpleSitAdminCommand : CommandRegistrationCallback {
    override fun register(
        dispatcher: CommandDispatcher<CommandSourceStack>,
        registryAccess: CommandBuildContext,
        environment: Commands.CommandSelection
    ) {
        val root = Commands.literal("sitadmin")
            .requires { it.permissions().hasPermission(Permissions.COMMANDS_ADMIN) }
            .then(Commands.literal("reload").executes { context ->
                SimpleSitConfigManager.reload()
                context.source.sendSuccess({ Component.literal("Simple Sit 配置已重新加载。") }, false)
                1
            })

        listOf(
            "enabled",
            "requireSneakRightClick",
            "requireEmptyHand",
            "allowStairs",
            "allowCarpets",
            "allowSlabs",
            "allowBeds",
            "allowCommandSit"
        ).forEach { name ->
            root.then(
                Commands.literal("set")
                    .then(Commands.literal(name)
                        .then(Commands.argument("value", BoolArgumentType.bool())
                            .executes { context ->
                                val value = BoolArgumentType.getBool(context, "value")
                                val updated = when (name) {
                                    "enabled" -> SimpleSitConfigManager.update { it.copy(enabled = value) }
                                    "requireSneakRightClick" -> SimpleSitConfigManager.update { it.copy(requireSneakRightClick = value) }
                                    "requireEmptyHand" -> SimpleSitConfigManager.update { it.copy(requireEmptyHand = value) }
                                    "allowStairs" -> SimpleSitConfigManager.update { it.copy(allowStairs = value) }
                                    "allowCarpets" -> SimpleSitConfigManager.update { it.copy(allowCarpets = value) }
                                    "allowSlabs" -> SimpleSitConfigManager.update { it.copy(allowSlabs = value) }
                                    "allowBeds" -> SimpleSitConfigManager.update { it.copy(allowBeds = value) }
                                    "allowCommandSit" -> SimpleSitConfigManager.update { it.copy(allowCommandSit = value) }
                                    else -> SimpleSitConfigManager.current()
                                }
                                context.source.sendSuccess({ Component.literal("已更新 $name = $value") }, true)
                                if (!updated.enabled) {
                                    context.source.sendSuccess({ Component.literal("提示：模组功能已整体关闭。") }, false)
                                }
                                1
                            }))
            )
        }

        dispatcher.register(root)
    }
}
