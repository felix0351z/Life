package de.felix0351.life.events


import de.felix0351.life.utils.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.Sound
import org.bukkit.block.ChiseledBookshelf
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.inventory.InventoryAction
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryCloseEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.ChiseledBookshelfInventory
import org.bukkit.inventory.ItemStack

class BookshelfInterfaceEvents : Listener {

    // Save all open bookshelf's in a map
    private val bookshelfStates = mutableMapOf<Player, ChiseledBookshelfInventory>()

    @EventHandler
    fun onPlayerInteract(e: PlayerInteractEvent) {

        // Only trigger when player does right-click and is not shifting
        if (e.action == Action.RIGHT_CLICK_BLOCK && !e.player.isSneaking) {
            val block = e.clickedBlock ?: return

            // Make sure it's a bookshelf
            if (block.type == Material.CHISELED_BOOKSHELF) {
                e.isCancelled = true

                // Create the inventory
                val shelf = block.state as ChiseledBookshelf
                val inventory = Bukkit.createInventory(e.player, 9, text("Chiseled Bookshelf", color = NamedTextColor.DARK_GRAY))

                // Add normal shelf as placeholders for 6 to 8
                for (i in 6..8) {
                    inventory.setItem(i, ItemStack(Material.BOOKSHELF))
                }
                // Add all other books from the shelf to the inventory
                shelf.inventory.contents.forEachIndexed { index, itemStack ->
                    inventory.setItem(index, itemStack)
                }

                // Open the shelf inventory for the player and save the open state
                e.player.openInventory(inventory)
                bookshelfStates[e.player] = shelf.inventory
                // Play shelf sound
                e.player.playSound(e.player.location, Sound.BLOCK_CHISELED_BOOKSHELF_INSERT_ENCHANTED, 1f, 1f)
            }

        }
    }

    @EventHandler
    fun onInventoryClick(e: InventoryClickEvent) {
        if (e.view.title() == text("Chiseled Bookshelf", color = NamedTextColor.DARK_GRAY)) {
            // Bookshelves are used as placeholder, so no click should be possible on it
            if (e.rawSlot < 9 && e.currentItem?.type == Material.BOOKSHELF) {
                e.isCancelled = true
            }

            // If something is moved to another inventory, make sure the item is a book
            if (e.action == InventoryAction.MOVE_TO_OTHER_INVENTORY) {
                val currentType = e.currentItem?.type

                if (currentType != Material.BOOK &&
                    currentType != Material.ENCHANTED_BOOK &&
                    currentType != Material.WRITABLE_BOOK &&
                    currentType != Material.WRITTEN_BOOK) {
                    e.isCancelled = true
                }
            }

            // If an item is moved in the shelf, make sure it's a book
            if (e.cursor.type != Material.AIR && e.rawSlot < 6) {

                if (e.cursor.type != Material.BOOK &&
                    e.cursor.type != Material.ENCHANTED_BOOK &&
                    e.cursor.type != Material.WRITABLE_BOOK &&
                    e.cursor.type != Material.WRITTEN_BOOK) {
                    e.isCancelled = true
                }
            }

        }
    }

    @EventHandler
    fun onInventoryClose(e: InventoryCloseEvent) {
        if (e.view.title() == text("Chiseled Bookshelf", color = NamedTextColor.DARK_GRAY)) {
            val shelfInventory = bookshelfStates[e.player]

            // Player belongs to that inventory
            if ((shelfInventory != null) && (e.inventory.holder == e.player)) {

                // Save the items to the shelf
                e.inventory.storageContents.forEachIndexed { index, itemStack ->
                    if (index > 5) return@forEachIndexed // 6 to 8 are placeholders, ignore it

                    // Save items
                    if (itemStack == null) shelfInventory.setItem(index, ItemStack(Material.AIR))
                    else shelfInventory.setItem(index, itemStack)
                }
                // remove the bookshelf state
                bookshelfStates.remove(e.player)
            }

        }


    }

}