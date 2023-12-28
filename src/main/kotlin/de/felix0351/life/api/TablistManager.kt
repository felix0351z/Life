package de.felix0351.life.api

import de.felix0351.life.utils.plus
import de.felix0351.life.utils.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.entity.Player

class TabListManager(
    private val db: DatabaseHandler
) {


    /**
     * Configures the scoreboard for a single player.
     * All necessary teams and players will be registered here for the player
     *
     */
    fun configureScoreboardForPlayer(player: Player) {
        db.getPlayers().forEach { row ->
            // Get the display name and the team name from the db
            val userName = row[DatabaseHandler.Players.name]
            val teamName = row[DatabaseHandler.Players.team]

            // Register all teams for the players and set the new teams
            val team = player.scoreboard.getTeam(teamName) ?: player.scoreboard.registerNewTeam(teamName)

            // Set the team prefix and the player color
            team.prefix(text(teamName, color = NamedTextColor.GRAY) + text(" | ", color = NamedTextColor.DARK_GRAY))
            team.color(NamedTextColor.GRAY) // Set the text color of the displayName

            team.addEntry(userName)
        }



    }

    /**
     * If the team of a player has changed,
     * the player entry must be changed in all scoreboards on the server
     */
    fun updateScoreboardForAllPlayers(target: Player) {
        val playerInfo = db.getPlayer(target) ?: throw RuntimeException("Player not found!")
        val teamName = playerInfo[DatabaseHandler.Players.team]

        Bukkit.getOnlinePlayers().forEach { player ->
            // Register or get the team from the target
            val team = player.scoreboard.getTeam(teamName) ?: player.scoreboard.registerNewTeam(teamName)

            // Set the team prefix and the player color for the target
            team.prefix(text(teamName, color = NamedTextColor.GRAY) + text(" | ", color = NamedTextColor.DARK_GRAY))
            team.color(NamedTextColor.GRAY) // Set the text color of the displayName

            // Add the target to the current scoreboard of the player
            team.addEntry(target.displayName)
        }
    }







}