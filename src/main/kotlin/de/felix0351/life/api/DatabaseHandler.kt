package de.felix0351.life.api

import de.felix0351.life.models.toStringCoordinate
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.entity.Player
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction

import java.io.File
import java.lang.Exception
import java.lang.RuntimeException
import java.sql.Connection

class DatabaseHandler(directory: String, path: String) {

    var isConnected = false

    companion object {
        /** Driver address to access the sqlite driver **/
        const val SQLITE_DRIVER = "org.sqlite.JDBC"
        const val STANDARD_TEAM = "Bauer"
    }

    /**
     * To show the membership of a player, a player table is needed
     * @property uuid The unique id of the player
     * @property name The player's display name
     * @property team The team of the player
     */
    object Players : Table() {
        val uuid = uuid("uuid")
        val name = varchar("name", 30)
        val team = varchar("team", 30)
        override val primaryKey = PrimaryKey(uuid)
    }


    /**
     * To save the chest from destroying, the chests must be stored
     * @property location The location of the chest. Will be saved in the form of "world-x-y-z"
     * @property team The team which owns the chest
     */
    object Chests : Table() {
        val location = varchar("location", 30)
        val team = varchar("team", 30)
        override val primaryKey = PrimaryKey(location)
    }

    init {
        val dir = File(directory)
        val db = File(directory + path)

        // Create the sqlite file if it doesn't exist
        if(!dir.exists()) dir.mkdirs()
        if (!db.exists()) db.createNewFile()

        try {
            TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
            Database.connect(url = "jdbc:sqlite:$directory$path", driver = SQLITE_DRIVER)
            transaction {
                // Add a logger and create the tables
                addLogger(StdOutSqlLogger)
                SchemaUtils.create(Players)
                SchemaUtils.create(Chests)
            }

            Bukkit.broadcast(Component.text("Loaded database successfully!"))
            isConnected = true
        } catch (ex: Exception) {
            ex.printStackTrace()
            Bukkit.broadcast(Component.text("Failed to load the sqlite database!"))
        }


    }


    fun addPlayerIfNotExists(player: Player, team: String = STANDARD_TEAM) = transaction {
        Players.insertIgnore {
            it[uuid] = player.uniqueId
            it[name] = player.name
            it[Players.team] = team
        }
    }

    /**
     * Update the assigned team from a player to a new team
     */
    fun updatePlayerTeam(player: Player, team: String) = transaction {
        Players.update(where = { Players.uuid eq player.uniqueId } ) {
            it[Players.team] = team
        }
    }

    fun getPlayers() = transaction {
        Players.selectAll().asIterable().map { it }
    }

    fun getPlayer(player: Player) = transaction {
        Players.select { Players.uuid eq player.uniqueId }.firstOrNull()
    }


    /**
     * Add a new chest to the assigned team of the player
     */
    fun addChest(location: Location, player: Player) = transaction {
        // Find the team of the user
        val entity = Players.select{ Players.uuid eq player.uniqueId }.firstOrNull()
            ?: throw RuntimeException("Player not found")

        Chests.insert {
            it[team] = entity[Players.team]
            it[Chests.location] = location.toStringCoordinate()
        }
    }


    fun removeChest(location: Location) = transaction {
        Chests.deleteWhere { Chests.location eq location.toStringCoordinate() }
    }


    fun isChestUnlocked(location: Location, player: Player) = transaction {
        val result = Chests.select { Chests.location eq location.toStringCoordinate() }.firstOrNull()
            ?: return@transaction true // If there is no chest, at this position => open

        // Find the team of the user
        val player = Players.select { Players.uuid eq player.uniqueId }.firstOrNull()
            ?: throw RuntimeException("Player not found")

        // If the team matches, the user is allowed to open
        return@transaction (result[Chests.team] == player[Players.team])
    }

    fun getAmountChests(player: Player) = transaction {
        val result = Players.select { Players.uuid eq player.uniqueId }.firstOrNull()
            ?: throw RuntimeException("Player not found")

        Chests.select { Chests.team eq result[Players.team] }.count()
    }








}