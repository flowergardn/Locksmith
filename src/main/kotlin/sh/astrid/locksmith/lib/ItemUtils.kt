package sh.astrid.locksmith.lib

import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.entity.Player
import org.bukkit.event.inventory.InventoryType
import org.bukkit.inventory.ItemStack
import java.sql.ResultSet
import java.util.*

// Method to check if a block is a supported Locksmith container
fun Block.isContainer(): Boolean {
    // We need to add some sort of support for shulkers, but I can't think of a reasonable way to add support for them.
    // Shulkers are easily moved, which would make the lock useless (it's Location based).

    return when (this.type) {
        Material.CHEST -> true
        Material.CHEST_MINECART -> true
        Material.TRAPPED_CHEST -> true
        Material.BARREL -> true
        else -> {
            false
        }
    }
}

// yoinked from a previous project
fun Material.prettify(): String {
    val fixed = this.toString().replace("_".toRegex(), " ")
    return fixed.substring(0, 1).uppercase(Locale.getDefault()) + fixed.substring(1).lowercase(Locale.getDefault())
}