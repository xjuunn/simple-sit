package com.junhsiun.sit

import com.google.gson.GsonBuilder
import com.junhsiun.SimpleSit
import java.nio.file.Files
import java.nio.file.Path

object SimpleSitConfigManager {
    private val gson = GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create()
    private val configPath: Path = Path.of("config", "simple-sit.json")

    @Volatile
    private var currentConfig: SimpleSitConfig = SimpleSitConfig()

    fun current(): SimpleSitConfig = currentConfig

    fun load(): SimpleSitConfig {
        if (Files.notExists(configPath.parent)) {
            Files.createDirectories(configPath.parent)
        }

        currentConfig = if (Files.exists(configPath)) {
            Files.newBufferedReader(configPath).use { reader ->
                gson.fromJson(reader, SimpleSitConfig::class.java) ?: SimpleSitConfig()
            }
        } else {
            SimpleSitConfig().also(::save)
        }

        return currentConfig
    }

    fun save(config: SimpleSitConfig = currentConfig) {
        currentConfig = config
        Files.newBufferedWriter(configPath).use { writer ->
            gson.toJson(currentConfig, writer)
        }
    }

    fun reload(): SimpleSitConfig = load()

    fun update(transform: (SimpleSitConfig) -> SimpleSitConfig): SimpleSitConfig {
        val updated = transform(currentConfig)
        save(updated)
        SimpleSit.LOGGER.info("已更新坐下配置文件: {}", configPath)
        return updated
    }
}
