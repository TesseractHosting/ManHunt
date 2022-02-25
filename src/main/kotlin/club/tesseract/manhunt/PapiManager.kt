package club.tesseract.manhunt

import club.tesseract.manhunt.utils.GameState
import club.tesseract.manhunt.utils.TaskTimerRunnable
import me.clip.placeholderapi.expansion.PlaceholderExpansion
import org.bukkit.*
import org.bukkit.entity.Player
import kotlin.math.roundToInt

class PapiManager(val plugin: ManHunt): PlaceholderExpansion() {
    val cacheLocation = HashMap<World.Environment, Location>()
    val cacheDistance = HashMap<Player, Int>()


    override fun canRegister(): Boolean {
        return true
    }

    override fun getIdentifier(): String {
        return "hunter"
    }

    override fun getAuthor(): String {
        return "TropicalShadow"
    }

    override fun getVersion(): String {
        return "1.0.0"
    }
    override fun onRequest(player: OfflinePlayer, identifier: String): String? {
        val gameManager: GameManager = plugin.getGameManager()
        val hunter = player as Player
        // %hunter_hunting%
        when (identifier) {
            "hunting" -> {
                if (gameManager.getGameState() == GameState.WAITING) {
                    return "The game hasn't started"
                } else if (gameManager.getGameState() == GameState.END) {
                    return "The game has ended. Resets in " + (TaskTimerRunnable.currentTimer?.getDisplayTest()?: "2 million") + " seconds"
                } else if(gameManager.getGameState() == GameState.COUNTDOWN){
                    return "Starts in " + (TaskTimerRunnable.currentTimer?.getDisplayTest()?: "")
                }
                if (gameManager.prey == hunter.uniqueId) return "Run! Kill the dragon to win"
                val pray: Player = gameManager.getPrey()
                return if (hunter.world !== pray.world) {
                    if (cacheLocation.containsKey(hunter.world.environment)) {
                        val loc = cacheLocation[hunter.world.environment]
                        pray.name + " lost prey at " + loc!!.x.toInt() + ", " + loc.z.toInt()
                    } else {
                        pray.name + " lost before we could track them"
                    }
                } else {
                    if(!cacheDistance.containsKey(hunter))
                        cacheDistance[hunter] = hunter.location.distance(pray.location).roundToInt()
                    pray.name + " is " + cacheDistance[hunter]  + " blocks away"

                }
            }
            "timer" -> {
                return TaskTimerRunnable.currentTimer?.getDisplayTest()?: ""
            }
            "type" -> {
                return if(gameManager.getGameState() != GameState.HUNTING && gameManager.getGameState() != GameState.COUNTDOWN) "Unknown"
                else if(gameManager.prey == hunter.uniqueId) "Prey"
                else "Hunter"
            }
            "dimension" -> {
                return when(hunter.world.environment){
                    World.Environment.NORMAL -> "Overworld"
                    World.Environment.NETHER -> "Nether"
                    World.Environment.THE_END -> "The End"
                    World.Environment.CUSTOM -> "Where the fuck are you?"
                }
            }
            else -> return null
        }
    }
}