package sh.astrid.locksmith.listeners

import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.PrepareItemCraftEvent
import org.bukkit.inventory.CraftingInventory
import org.bukkit.inventory.ItemStack
import sh.astrid.locksmith.Locksmith
import sh.astrid.locksmith.lib.NBTEditor
import sh.astrid.locksmith.lib.SQL
import sh.astrid.locksmith.lib.coloured
import java.util.*


class CraftListener : Listener {
    init {
        Locksmith.instance.server.pluginManager.registerEvents(this, Locksmith.instance)
    }

    @EventHandler
    fun onCraft(event: PrepareItemCraftEvent) {
        val inv: CraftingInventory = event.inventory
        val matrix = inv.matrix

        if(matrix[1] == null || matrix[4] == null) return

        // "acceptable" variable is set to false when an item is in a slot that it's not supposed to be in
        // this makes it, so you can craft a key only if the required items are there, and nothing else

        var acceptable = true

        matrix.forEachIndexed { index, element ->
            if(index != 1
                && index != 4
                && element != null)
                acceptable = false
        }

        if(!acceptable) return

        // not the most... elegant solution
        if(matrix[1]!!.type == Material.IRON_INGOT && matrix[4]!!.type == Material.STICK) {
            println("woah they're crafting a key!!!")

            val keyId = UUID.randomUUID();
            val player = event.view.player as Player

            SQL.execute(
                "INSERT INTO keys(keyId, owner, createdAt) VALUES(?, ?, ?)",
                keyId,
                player.uniqueId,
                System.currentTimeMillis()
            )

            val tempKey = ItemStack(Material.TRIPWIRE_HOOK);
            val meta = tempKey.itemMeta!!
            meta.setDisplayName("&s${player.displayName}'s&p key".coloured())
            tempKey.itemMeta = meta
            val key: ItemStack = NBTEditor.set(tempKey, keyId.toString(), "keyUUID", "item")

            event.inventory.result = key
        }
    }
}