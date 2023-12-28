package de.felix0351.life.events

import de.felix0351.life.api.DatabaseHandler
import de.felix0351.life.api.TabListManager
import de.felix0351.life.utils.text
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class JoinLeaveEvents(
    private val db: DatabaseHandler,
    private val tablist: TabListManager
) : Listener {


    @EventHandler
    fun onJoin(event: PlayerJoinEvent) {
        db.addPlayerIfNotExists(event.player)
        tablist.configureScoreboardForPlayer(event.player) // Give the new player a scoreboard
        tablist.updateScoreboardForAllPlayers(event.player) // add the player to the other players' scoreboards

        event.joinMessage(text("${event.player.displayName} hat das Spiel betreten", color = NamedTextColor.YELLOW))
    }

    @EventHandler
    fun onLeave(event: PlayerQuitEvent) {
        event.quitMessage(text("${event.player.displayName} hat das Spiel verlassen", color = NamedTextColor.YELLOW))
    }


}