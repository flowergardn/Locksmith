package sh.astrid.locksmith.listeners

import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.block.BlockFace
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import sh.astrid.locksmith.Locksmith
import sh.astrid.locksmith.lib.*


class BlockListener : Listener {
    init {
        Locksmith.instance.server.pluginManager.registerEvents(this, Locksmith.instance)
    }

    private fun check(loc: Location): Boolean {
        val faces = setOf(BlockFace.NORTH, BlockFace.EAST, BlockFace.SOUTH, BlockFace.WEST)
        for (face in faces) {
            if (loc.block.getRelative(face).isLockedContainer()) return true
        }
        return false
    }


    @EventHandler
    fun onPlace(event: BlockPlaceEvent) {
        // Prevents placing of keys
        if(event.player.isHoldingValidKey())
            event.isCancelled = true

        // Prevents placing of chests near a lock chest

        if(event.block.type != Material.CHEST) return

        val aroundChest = check(event.block.location)

        if(aroundChest) {
            event.player.sendActionBar("&g[!] Cannot place chest here")
            event.isCancelled = true
        }
    }

    // Removes locked container entry on break
    // Takes into account who owns the lock
    @EventHandler
    fun onBreak(event: BlockBreakEvent) {
        val clickedBlock = event.block

        // Checks if the clicked block is a valid container
        if(!clickedBlock.isContainer()) return

        val locked = clickedBlock.isLockedContainer();

        // if not locked, don't do anything else
        if(!locked) return;

        // get lock data
        val containerData = clickedBlock.getLockData()!!;

        // get the person who created the lock
        val lockCreator = containerData.getString("owner")
        // get the person attempting to break it
        val playerUUID = event.player.uniqueId.toString()

        // if uuids don't match, cancel event
        if(lockCreator != playerUUID) {
            event.player.sendActionBar("&g[!] ${clickedBlock.type.prettify()} is locked")
            event.isCancelled = true
            return
        }

        // get the location from the container data itself, doing this allows us to not serialize the location again
        val location = containerData.getString("location")

        // remove the entry from the database
        SQL.execute("DELETE FROM locked_containers WHERE location = ?", location)
    }
}