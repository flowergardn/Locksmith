package sh.astrid.locksmith.lib

import net.md_5.bungee.api.ChatColor
import net.md_5.bungee.api.ChatMessageType
import net.md_5.bungee.api.chat.TextComponent
import org.bukkit.entity.Player
import java.util.regex.Matcher
import java.util.regex.Pattern

val hexPattern: Pattern = Pattern.compile("&(#[a-fA-F\\d]{6})");

enum class ChatColors(val hex: String) {
    PRIMARY("&#ffd4e3"),
    SECONDARY("&#ffb5cf"),
    ERROR("&#ff6e6e");
}

fun String.coloured(): String {
    var coloured = this
        .replace("&p", ChatColors.PRIMARY.hex)
        .replace("&s", ChatColors.SECONDARY.hex)
        .replace("&g", ChatColors.ERROR.hex)
    var match: Matcher = hexPattern.matcher(coloured)

    while (match.find()) {
        val color: String = coloured.substring(match.start(), match.end())
        coloured = coloured.replace(color, ChatColor.of(color.substring(1)).toString())
        match = hexPattern.matcher(coloured)
    }

    return ChatColor.translateAlternateColorCodes('&', coloured)
}

fun Player.sendActionBar(msg: String) {
    player!!.spigot().sendMessage(
        ChatMessageType.ACTION_BAR,
        *TextComponent.fromLegacyText(msg.coloured())
    )
}