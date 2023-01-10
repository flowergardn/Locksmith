package sh.astrid.locksmith.commands

import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.astrid.locksmith.Locksmith
import sh.astrid.locksmith.lib.*

class RemoveLock : CommandExecutor {
    init {
        Locksmith.instance.getCommand("removelock")!!.setExecutor(this);
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Console cannot execute this command.")
            return false;
        }

        val targetBlock: Block? = sender.getTargetBlockExact(3)

        // checks if the block is valid
        if(targetBlock == null || !targetBlock.isContainer()) {
            sender.sendMessage("&gPlease look at a container to remove a lock.".coloured())
            return true;
        }

        if(!targetBlock.isLockedContainer()) {
            sender.sendMessage("&gYou can only remove locks from locked containers, silly.".coloured())
            return true;
        }

        val containerData = targetBlock.getLockData();

        // temp
        val ownerUUID = containerData!!.getString("owner");

        if(ownerUUID != sender.uniqueId.toString()) {
            sender.sendMessage("&gYou don't seem to own this container..".coloured())
            return true
        }

        val location = serializeLocation(targetBlock.grabLocation());

        SQL.execute("DELETE FROM locked_containers WHERE location = ?", location)

        sender.sendActionBar("&pSuccessfully removed the lock from this &s${targetBlock.type.prettify()}!")

        return true
    }
}