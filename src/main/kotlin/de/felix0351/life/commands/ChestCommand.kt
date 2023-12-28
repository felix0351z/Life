package de.felix0351.life.commands

import de.felix0351.life.api.DatabaseHandler
import de.felix0351.life.utils.text
import de.felix0351.life.utils.textArg
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

/**
*
* /chests status <Player> Zeige wie viele Truhen der momentane Spieler mit seinem Team besitzt
* /chests remove - Entferne die Truhe, auf welche der Spieler momentan zeigt
*/

private const val PERMISSION = "life.chests"
class ChestCommand(
    private val db: DatabaseHandler
) : CommandExecutor {

    override fun onCommand(cs: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if (cs !is Player || !cs.hasPermission(PERMISSION)) {
            return false
        }

        if (args.isEmpty()) {
            cs.sendMessage(text("Bitte benutze /chests status <Player> oder /chests remove"))
            return true
        }

        // chests status <Player>
        if (args.size == 2 && args[0] == "status") {
            val player = Bukkit.getPlayer((args[1]))
            if (player == null) {
                cs.sendMessage(textArg("Der Spieler ; konnte nicht gefunden werden", args[1]))
                return true
            }

            val amountChests = db.getAmountChests(player)
            cs.sendMessage(textArg("Der Spieler ; besitzt mit seinem Team momentan ; Truhen", args[1], amountChests))
            return true
        }

        if (args[0] == "remove") {
            // Remove the pointing chest

            val block = cs.getTargetBlock(null, 4) // max distance = 4
            if (block.type != Material.CHEST) {
                cs.sendMessage(text("Um eine Truhe aus dem System zu entfernen, musst du auf eine Truhe zeigen!"))
                return true
            }

            val success = db.removeChest(block.location)
            if (success == 1) {
                cs.sendMessage(textArg("Die Truhe an der Position ; ; ; wurde aus dem System entfernt",
                    block.location.blockX,
                    block.location.blockY,
                    block.location.blockZ))
            } else {
                cs.sendMessage(text("Die Truhe wurde nicht im System gefunden!"))
            }


        }

        return true
    }

}