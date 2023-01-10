package sh.astrid.locksmith

import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import java.util.logging.Level
import sh.astrid.locksmith.commands.*;
import sh.astrid.locksmith.lib.SQL
import sh.astrid.locksmith.listeners.BlockListener
import sh.astrid.locksmith.listeners.ContainerListener
import sh.astrid.locksmith.listeners.CraftListener

class Locksmith : JavaPlugin() {

    override fun onEnable() {
        saveDefaultConfig()

        // these are class names, don't yell at me for conventions :c

        // Commands
        AddLock()
        RemoveLock()
        Wipe()
        SetSpare()

        // Events
        ContainerListener()
        BlockListener()
        CraftListener()

        // Register recipes
        RegisterRecipes()

        val version = instance.description.version;
        instance.logger.log(Level.INFO, "Locksmith v$version has loaded successfully.")
    }

    companion object {
        @JvmStatic
        val instance: Locksmith
            get() = getPlugin(Locksmith::class.java)
    }

    override fun onDisable() {
        instance.logger.log(Level.INFO, "Shutting down Locksmith, waiting for SQL to close...")
        SQL.close()
    }

}