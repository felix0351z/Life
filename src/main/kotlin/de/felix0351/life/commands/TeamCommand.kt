package de.felix0351.life.commands


import de.felix0351.life.api.DatabaseHandler
import de.felix0351.life.api.TabListManager
import de.felix0351.life.utils.text
import de.felix0351.life.utils.textArg
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender


/**
* Ändere das Team von einem Spieler.
* Standardmäßig wird ein Spieler dem Team [DatabaseHandler.STANDARD_TEAM] zugeordnet
* /team <Spieler>                  | Zeigt das aktuelle Team des Spielers an
* /team <Spieler> <neues Team>     | Weißt dem Spieler ein neues Team zu
 *
 * Permission: life.teams
*/
private const val PERMISSION = "life.teams"

class TeamCommand(
    private val db: DatabaseHandler,
    private val tablist: TabListManager
) : CommandExecutor {

    override fun onCommand(cs: CommandSender, cmd: Command, label: String, args: Array<out String>): Boolean {
        if(!cs.hasPermission(PERMISSION)) return true // No Permission

        if (args.isEmpty()) { // No Player selected
            cs.sendMessage(text("Bitte gib einen Spieler an"))
            return true
        }

        val target = Bukkit.getPlayer(args[0])
        if (target == null) { // No correct Player selected
            cs.sendMessage(textArg("Es wurde kein Spieler mit dem Namen ; gefunden!", args[0]))
            return true
        }

        when(args.size) {
            // Return current Team
            1 -> {
                val result = db.getPlayer(target)
                if (result == null) {
                    cs.sendMessage(textArg("Es wurde kein Spieler mit dem Namen ; in der Datenbank gefunden", args[0]))
                    return true
                }
                val name = result[DatabaseHandler.Players.team]
                cs.sendMessage(textArg("Der Spieler ; gehört zu dem Team ;", args[0], name))
            }

            // Change team
            2 -> {
                val newName = args[1]
                db.updatePlayerTeam(target, newName)
                tablist.updateScoreboardForAllPlayers(target) // Update the player in every scoreboard
                cs.sendMessage(textArg("Der Spieler ; gehört jetzt zu dem Team ;", args[0], newName))
            }
        }

        return true
    }


}