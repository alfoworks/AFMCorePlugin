package ru.allformine.afmcp.net.api

import com.google.gson.JsonArray
import com.google.gson.JsonObject
import ninja.leaping.configurate.ConfigurationNode
import org.spongepowered.api.command.CommandSource
import org.spongepowered.api.entity.living.player.Player
import org.spongepowered.api.scheduler.Task
import ru.allformine.afmcp.AFMCorePlugin
import ru.allformine.afmcp.net.http.Requests

object Webhook {
    private val configNode: ConfigurationNode = AFMCorePlugin.getConfig().getNode("webhook")
    private val server_id = configNode.getNode("server_id").string
    private val token = configNode.getNode("token").string
    private const val apiUrl = "https://hooks.alfo.ws/servers/"

    private fun sendApiRequest(jsonObject: JsonObject, type: String, group: String, extra: Array<out String>) {

    }

    private fun arrayToJson(array: Array<out String>): JsonArray { // TODO: Дикий костыль
        val jsonArray = JsonArray()

        for (s in array) {
            jsonArray.add(s)
        }

        return jsonArray
    }

    fun sendSecureAlert(type: TypeSecureAlert, player: Player, vararg extra: String) {
        val jsonObject = JsonObject()
        jsonObject.addProperty("username", player.name)

        sendApiRequest(jsonObject, type.name, "secalert", extra)
    }

    @JvmStatic
    fun sendServerMessage(type: TypeServerMessage, vararg extra: String) {
        sendApiRequest(JsonObject(), type.name, "server", extra)
    }

    fun sendPlayerMessage(type: TypePlayerMessage, player: CommandSource, vararg extra: String) {
        val typeName = type.name
        val jsonObject = JsonObject()
        jsonObject.addProperty("username", player.name)

        // Оптимизация (отправка запроса на каждый чих не будем проебывать тики)
        Task.builder()
                .execute(Runnable {
                    sendApiRequest(jsonObject, typeName, "player", extra)
                })
                .async()
                .submit(AFMCorePlugin.instance)
    }

    enum class TypeServerMessage {
        SERVER_STARTED,
        SERVER_STOPPED,
        SERVER_RESTARTING,
        SERVER_WIPE,
        SERVER_MAINTENANCE
    }

    enum class TypePlayerMessage {
        CHAT_MESSAGE,
        LVL2_CHAT_MESSAGE,
        COMMAND,
        JOINED_SERVER,
        LEFT_SERVER,
        STAFF_JOINED_SERVER,
        STAFF_LEFT_SERVER,
        JOINED_FIRST_TIME,
        EARNED_ADVANCEMENT,
        DIED,
        BOUGHT_VIP
    }

    enum class TypeSecureAlert {
        PACKETHACK_USAGE_DETECTED
    }
}
