package dev.tuhkanens.liteskins.libs

import com.alessiodp.libby.Library
import com.alessiodp.libby.VelocityLibraryManager
import com.alessiodp.libby.relocation.Relocation
import dev.tuhkanens.liteskins.Main

class LibraryLoader {

    private val plugin: Main = Main.instance
    private lateinit var libraryManager: VelocityLibraryManager<Main>

    fun loadLibraries() {
        libraryManager = VelocityLibraryManager(
            plugin,
            plugin.logger,
            plugin.directory,
            plugin.proxy.pluginManager
        )

        libraryManager.addMavenCentral()

        libraryManager.loadLibraries(
            Library.builder()
                .groupId("org{}jetbrains{}exposed")
                .artifactId("exposed-core")
                .version("0.53.0")
                .relocate(Relocation("org{}jetbrains{}exposed", "dev{}tuhkanens{}liteskins{}libs{}exposed"))
                .build(),

            Library.builder()
                .groupId("org{}jetbrains{}exposed")
                .artifactId("exposed-dao")
                .version("0.53.0")
                .relocate(Relocation("org{}jetbrains{}exposed", "dev{}tuhkanens{}liteskins{}libs{}exposed"))
                .build(),

            Library.builder()
                .groupId("org{}jetbrains{}exposed")
                .artifactId("exposed-jdbc")
                .version("0.53.0")
                .relocate(Relocation("org{}jetbrains{}exposed", "dev{}tuhkanens{}liteskins{}libs{}exposed"))
                .build(),

            Library.builder()
                .groupId("org{}xerial")
                .artifactId("sqlite-jdbc")
                .version("3.49.1.0")
                .relocate(Relocation("org{}xerial", "dev{}tuhkanens{}liteskins{}libs{}sqlite"))
                .build(),

            Library.builder()
                .groupId("com{}mysql")
                .artifactId("mysql-connector-j")
                .version("8.3.0")
                .relocate(Relocation("com{}mysql", "dev{}tuhkanens{}liteskins{}libs{}mysql"))
                .build(),

            Library.builder()
                .groupId("com{}zaxxer")
                .artifactId("HikariCP")
                .version("5.1.0")
                .relocate(Relocation("com{}zaxxer{}hikari", "dev{}tuhkanens{}liteskins{}libs{}hikari"))
                .build(),

            Library.builder()
                .groupId("org{}spongepowered")
                .artifactId("configurate-core")
                .version("4.2.0")
                .relocate(Relocation("org{}spongepowered{}configurate", "dev{}tuhkanens{}liteskins{}libs{}configurate"))
                .build(),

            Library.builder()
                .groupId("org{}spongepowered")
                .artifactId("configurate-yaml")
                .version("4.2.0")
                .relocate(Relocation("org{}spongepowered{}configurate", "dev{}tuhkanens{}liteskins{}libs{}configurate"))
                .build(),

            Library.builder()
                .groupId("com{}google{}code{}gson")
                .artifactId("gson")
                .version("2.13.2")
                .relocate(Relocation("com{}google{}gson", "dev{}tuhkanens{}liteskins{}libs{}gson"))
                .build()
        )
    }

}