package sh.astrid.locksmith.commands

import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import sh.astrid.locksmith.Locksmith
import sh.astrid.locksmith.lib.*
import java.sql.ResultSet
import java.sql.SQLException

class AddLock : CommandExecutor {
    init {
        Locksmith.instance.getCommand("addlock")!!.setExecutor(this);
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Console cannot execute this command.")
            return false;
        }

        val item = sender.inventory.itemInMainHand

        // Checks if the key is "valid". Only checks metadata.
        if(!item.isValidKey()) {
            sender.sendMessage("&gPlease hold one of your keys to setup the lock.".coloured())
            return true;
        }

        // Grab data from the key, owner, uuid, createdAt, etc.
        val keyData = item.getKeyData();

        // If this is null, then it's either an invalid key or has no data in the database.
        if(keyData == null) {
            sender.sendMessage("&gThis does not appear to be a valid key!".coloured())
            return true;
        }

        val targetBlock: Block? = sender.getTargetBlockExact(3)

        // checks if the block is valid
        if(targetBlock == null || !targetBlock.isContainer()) {
            sender.sendMessage("&gPlease look at a container to add a lock.".coloured())
            return true;
        }

        // If the container is currently locked, only accept new keys from the owner of the locked container
        // in the future maybe add some sort of confirmation?
        if(targetBlock.isLockedContainer()) {
            val containerData = targetBlock.getLockData();

            // temp
            val ownerUUID = containerData!!.getString("owner");
            if(sender.uniqueId.toString() != ownerUUID) {
                sender.sendMessage("&gThis container is already locked, and you don't seem to own this container..".coloured())
                return true
            }
        }

        // turn location into an easy to save string
        val location = serializeLocation(targetBlock.grabLocation());

       try {
           SQL.execute(
               "INSERT INTO locked_containers(location, owner, keyId) VALUES(?, ?, ?)",
               location,
               sender.uniqueId,
               keyData.getString("keyId")
           )
       } catch(e: SQLException) {

           // Sends a more descriptive error for pre-existing locks.
           if(e.message != null) {
               if(e.message!!.contains("SQLITE_CONSTRAINT_PRIMARYKEY")) {
                    sender.sendMessage("&gThis ${targetBlock.type.prettify()} already has a lock on it.".coloured())
                    return false;
               } else println(e)
           }

           sender.sendMessage("&gDatabase seems to be busy, try again later :c".coloured())
           return false;
       }

        sender.sendActionBar("&pAdded a lock to &s${targetBlock.type.prettify()}!")

        return false;
    }

}