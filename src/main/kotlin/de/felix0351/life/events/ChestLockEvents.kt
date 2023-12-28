package de.felix0351.life.events

import de.felix0351.life.api.DatabaseHandler
import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.block.BlockPlaceEvent
import org.bukkit.event.entity.EntityExplodeEvent
import org.bukkit.event.player.PlayerInteractEvent

class ChestLockEvents(
    private val db: DatabaseHandler
) : Listener {

    @EventHandler
    fun onBlockPlace(event: BlockPlaceEvent) {
        // If there is no db connection, we can't control it
        if (!db.isConnected) return

        if (event.blockPlaced.type == Material.CHEST) {
            db.addChest(event.blockPlaced.location, event.player)
        } else if ((event.blockPlaced.type == Material.HOPPER)|| (event.blockPlaced.type == Material.HOPPER_MINECART)) {
            // If a user tries to get the items from a chest above, this must also be cancelled
            val location = event.blockPlaced.location.apply { this.y += 1.0 }
            if (location.block.type == Material.CHEST) {
                // Check if the user is allowed to do that
                if (!db.isChestUnlocked(location, event.player)) {
                    event.isCancelled =  true
                }
            }


        }
    }


    @EventHandler
    fun onBlockBreak(event: BlockBreakEvent) {
        // If there is no db connection, we can't control it
        if (!db.isConnected) return

        if (event.block.type == Material.CHEST) {
            // if the chest is locked, the chest shouldn't break
            if (!db.isChestUnlocked(event.block.location, event.player)) {
                event.isCancelled = true
            } else {
                db.removeChest(event.block.location)
            }
        }
    }

    @EventHandler
    fun onPlayerInteract(event: PlayerInteractEvent) {
        // If there is no db connection, we can't control it
        if (!db.isConnected) return

        if (event.action == Action.RIGHT_CLICK_BLOCK) {
            val block = event.clickedBlock ?: return

            if (block.type == Material.CHEST) {
                // If the chest is locked for the user, the event must be cancelled
                if (!db.isChestUnlocked(block.location, event.player)) {
                    event.isCancelled = true
                }
            }
        }
    }

    @EventHandler
    fun onBlockExplode(event: EntityExplodeEvent) {
        // iterate through the whole block list and remove the chests
        val iterator = event.blockList().iterator()
        while (iterator.hasNext()) {
            val item = iterator.next()
            if (item.type == Material.CHEST) {
                iterator.remove()
            }
        }

    }








}