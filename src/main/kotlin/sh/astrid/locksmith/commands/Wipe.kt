package sh.astrid.locksmith.commands

import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.ConsoleCommandSender
import org.bukkit.entity.Player
import sh.astrid.locksmith.Locksmith
import sh.astrid.locksmith.lib.SQL
import sh.astrid.locksmith.lib.coloured

class Wipe : CommandExecutor {
    init {
        Locksmith.instance.getCommand("wipe")!!.setExecutor(this);
    }

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
       val canExecute = (sender is ConsoleCommandSender || sender is Player && sender.isOp)

        if(!canExecute) {
            var name = "Unknown"
            if(sender is Player) name = sender.displayName
            println("$name tried to use /wipe")
            return false
        }

        SQL.execute("DELETE FROM locked_containers")
        SQL.execute("DELETE FROM keys")

        sender.sendMessage("&pSuccessfully &swiped&p all data".coloured())

        return true;
    }
}