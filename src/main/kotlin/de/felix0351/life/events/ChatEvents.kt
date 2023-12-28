package de.felix0351.life.events

import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.player.AsyncPlayerChatEvent

class ChatEvents : Listener {

    @EventHandler
    fun onChat(event: AsyncPlayerChatEvent) {
        event.format = "§7 ${event.player.displayName}: " + event.message
    }


}