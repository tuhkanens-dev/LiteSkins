package dev.tuhkanens.liteskins.manager

import dev.tuhkanens.liteskins.Main
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File

object ConfigManager {

    private val plugin: Main = Main.instance

    private lateinit var root: ConfigurationNode
    private lateinit var yaml: YamlConfigurationLoader
    private lateinit var file: File

    private const val FILE = "config.yml"

    fun init() {

        file = File("${plugin.directory}/$FILE")

        if (!file.exists()) {
            file.parentFile.mkdirs()
            plugin.javaClass.getResourceAsStream("/$FILE")?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        this.loadYaml()

    }

    fun get() = root

    fun reload() = this.loadYaml()

    private fun loadYaml() {
        yaml = YamlConfigurationLoader.builder()
            .path(file.toPath())
            .build()
        root = yaml.load()
    }

}