package dev.tuhkanens.liteskins.manager

import dev.tuhkanens.liteskins.Main
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.minimessage.MiniMessage
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver
import org.spongepowered.configurate.ConfigurationNode
import org.spongepowered.configurate.yaml.YamlConfigurationLoader
import java.io.File

object MessagesManager {

    private val plugin: Main = Main.instance
    private val miniMessage: MiniMessage = Main.miniMessage

    private lateinit var root: ConfigurationNode
    private lateinit var yaml: YamlConfigurationLoader
    private lateinit var file: File

    private const val FILE = "messages.yml"

    fun init() {

        file = File("${plugin.directory}/$FILE")

        if (!file.exists()) {
            plugin.javaClass.getResourceAsStream("/$FILE")?.use { input ->
                file.outputStream().use { output ->
                    input.copyTo(output)
                }
            }
        }

        this.loadYaml()

    }

    fun getString(path: String): String {
        val keys = path.split(".").toTypedArray()
        val string = root.node(*keys).getString(path) ?: path
        return string
    }

    fun getStringList(path: String): List<String> {
        val keys = path.split(".").toTypedArray()
        val lines = root.node(*keys).getList(String::class.java) ?: emptyList()
        return lines
    }

    fun getStringLore(path: String): String {
        val string = this.getStringList(path).joinToString("\n")
        return string
    }

    fun getComponent(path: String, vararg resolvers: TagResolver): Component {
        val string = this.getString(path)
        return miniMessage.deserialize(string, *resolvers)
    }

    fun getComponentList(path: String, vararg resolvers: TagResolver): List<Component> {
        val lines = this.getStringList(path)
        return lines.map { miniMessage.deserialize(it, *resolvers) }
    }

    fun getComponentLore(path: String, vararg resolvers: TagResolver): Component {
        val string = this.getStringList(path).joinToString("\n")
        return miniMessage.deserialize(string, *resolvers)
    }

    fun get() = root

    fun reload() = this.loadYaml()

    fun loadYaml() {
        yaml = YamlConfigurationLoader.builder()
            .path(file.toPath())
            .build()
        root = yaml.load()
    }

}