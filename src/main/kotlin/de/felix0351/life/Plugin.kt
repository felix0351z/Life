package de.felix0351.life

import de.felix0351.life.events.ChatEvents
import de.felix0351.life.events.ChestLockEvents
import de.felix0351.life.events.JoinLeaveEvents
import de.felix0351.life.api.DatabaseHandler
import de.felix0351.life.api.TabListManager
import de.felix0351.life.commands.ChestCommand
import de.felix0351.life.commands.TeamCommand
import de.felix0351.life.events.BookshelfInterfaceEvents
import org.bukkit.plugin.java.JavaPlugin

//TODO: SitEvents
//TODO: Skin System

class Plugin : JavaPlugin() {

    companion object {
        const val FOLDER = "plugins/life/"
        const val DB_FILE = "life.db"
    }

    private lateinit var databaseHandler: DatabaseHandler
    private lateinit var tablistManager: TabListManager


    override fun onEnable() {
        databaseHandler = DatabaseHandler(directory = FOLDER, path = DB_FILE)
        tablistManager = TabListManager(databaseHandler)

        getCommand("team")!!.setExecutor(TeamCommand(databaseHandler, tablistManager))
        getCommand("chest")!!.setExecutor(ChestCommand(databaseHandler))


        server.pluginManager.registerEvents(JoinLeaveEvents(databaseHandler, tablistManager), this)
        server.pluginManager.registerEvents(ChestLockEvents(databaseHandler), this)
        server.pluginManager.registerEvents(ChatEvents(), this)
        server.pluginManager.registerEvents(BookshelfInterfaceEvents(), this)
    }

    override fun onDisable() {
        super.onDisable()
    }





}