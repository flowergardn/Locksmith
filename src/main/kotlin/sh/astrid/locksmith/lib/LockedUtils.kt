package sh.astrid.locksmith.lib

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.Block
import org.bukkit.block.Chest
import org.bukkit.block.DoubleChest
import java.sql.ResultSet

fun getChest(loc: Location): Block? {
    val block = loc.block
    if (block.type != Material.CHEST) return null

    val state = block.state as Chest
    val chest = state.inventory.holder

    return if (chest is DoubleChest) {
        return chest.leftSide?.inventory?.location?.block ?: block
    } else block
}

fun Block.grabLocation(): Location {
    val location: Location = this.location
    return getChest(location)?.location ?: location
}

fun Block.isLockedContainer(): Boolean {
    val location: Location = this.grabLocation()
    val executed = SQL.query("SELECT * FROM locked_containers WHERE location = ?", serializeLocation(location));
    return executed.next()
}

fun Block.getLockData(): ResultSet? {
    if(!this.isLockedContainer()) return null;
    val location: Location = this.grabLocation()
    val executed = SQL.query("SELECT * FROM locked_containers WHERE location = ?", serializeLocation(location));
    return executed;
}