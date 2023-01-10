package sh.astrid.locksmith.listeners

import org.bukkit.block.Block
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.player.PlayerInteractEvent
import sh.astrid.locksmith.Locksmith
import sh.astrid.locksmith.lib.*

class ContainerListener : Listener {
    init {
        Locksmith.instance.server.pluginManager.registerEvents(this, Locksmith.instance)
    }

    @EventHandler
    fun onOpen(event: PlayerInteractEvent) {
        if(event.action != Action.RIGHT_CLICK_BLOCK) return

        val location = event.clickedBlock?.location ?: return;
        val clickedBlock: Block = (getChest(location) ?: event.clickedBlock)!!;
        val p = event.player

        // Checks if the clicked block is a valid container
        if(!clickedBlock.isContainer()) return

        val locked = clickedBlock.isLockedContainer();

        // if not locked, don't do anything else
        if(!locked) return;

        // might remove this check to send a "this <pretty name> is locked."
        if(!p.isHoldingValidKey()) {
            event.isCancelled = true
            return
        }

        // now we can check if the key matches
        val lockData = clickedBlock.getLockData()!!
        val heldKeyUUID = p.getKey()?.getKeyUUID()
        val setKeyUUID = lockData.getString("keyId")

        if(heldKeyUUID != setKeyUUID) {
            p.sendActionBar("&g[!] Wrong key")
            event.isCancelled = true
            return
        }
    }
}