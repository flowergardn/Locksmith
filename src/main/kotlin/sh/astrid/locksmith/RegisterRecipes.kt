package sh.astrid.locksmith

import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.ShapedRecipe
import sh.astrid.locksmith.lib.coloured
import java.lang.Exception

class RegisterRecipes {
    init {
        // best code fr
        // recipes can only be registered on a server once, so this catches that
        try {
            Bukkit.addRecipe(keyRecipe())
        } catch (_: Exception) { }
    }

    private fun keyRecipe(): ShapedRecipe {
        // this is a placeholder, will be replaced in the PreProcessCraft event
        val key = ItemStack(Material.TRIPWIRE_HOOK)
        val meta = key.itemMeta!!
        meta.setDisplayName("&pKey".coloured())
        key.itemMeta = meta

        val recipe = ShapedRecipe(NamespacedKey(Locksmith.instance, "key"), key)
        recipe.shape(" X ", " Y ", "   ")
        recipe.setIngredient('X', Material.IRON_INGOT)
        recipe.setIngredient('Y', Material.STICK)

        println("Successfully created key recipe")

        return recipe
    }
}