package sh.astrid.locksmith.commands

import org.bukkit.block.Block
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import sh.astrid.locksmith.Locksmith
import sh.astrid.locksmith.lib.*

class SetSpare : CommandExecutor {
    init {
        Locksmith.instance.getCommand("setspare")!!.setExecutor(this);
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (sender !is Player) {
            sender.sendMessage("Console cannot execute this command.")
            return false;
        }

        val item = sender.inventory.itemInMainHand

        // Checks if the key is "valid". Only checks metadata.
        if(!item.isValidKey()) {
            sender.sendMessage("&gPlease hold one of your keys to add a spare.".coloured())
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
            sender.sendMessage("&gPlease look at a container to set a spare key.".coloured())
            return true;
        }

        if(!targetBlock.isLockedContainer()) {
            sender.sendMessage("&gYou can only add spare keys to containers that are already locked. (Try /addlock)".coloured())
            return true
        }

        val containerData = targetBlock.getLockData()!!;
        val containerKeyId = containerData.getString("keyId");
        val keyId = keyData.getString("keyId");

        if(containerKeyId == keyId) {
            sender.sendMessage("&gThis key is already linked to this container, silly.".coloured())
            return true;
        }

        val newKey: ItemStack = NBTEditor.set(sender.getKey()!!, containerKeyId, "keyUUID", "item")

        // might be a better way to do this
        sender.inventory.setItemInMainHand(newKey);
        sender.updateInventory();

        sender.sendMessage("&pSuccessfully added a&s spare&p key to this &s${targetBlock.type.prettify()}&p! Be careful with it.".coloured())

        return true;
    }

}