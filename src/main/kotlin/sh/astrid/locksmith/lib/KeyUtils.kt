package sh.astrid.locksmith.lib

import org.bukkit.entity.Item
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import java.sql.ResultSet

fun ItemStack.isValidKey(): Boolean {
    return NBTEditor.contains(this, "keyUUID", "item")
}

fun ItemStack.getKeyData(): ResultSet? {
    if (this.type.isAir || !this.isValidKey()) return null;
    val keyUUID = NBTEditor.getString(this, "keyUUID", "item")
    return SQL.query("SELECT * FROM keys WHERE keyId = ?", keyUUID);
}

fun Player.isHoldingValidKey(): Boolean {
    val item = this.inventory.itemInMainHand
    return item.isValidKey()
}

fun Player.getKey(): ItemStack? {
    return if(this.isHoldingValidKey()) {
        this.inventory.itemInMainHand
    } else null
}

fun ItemStack.getKeyUUID(): String? {
    if (!this.isValidKey()) return null;
    return NBTEditor.getString(this, "keyUUID", "item")
}