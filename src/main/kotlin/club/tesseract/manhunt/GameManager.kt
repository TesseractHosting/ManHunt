package club.tesseract.manhunt

import club.tesseract.manhunt.utils.GameState
import club.tesseract.manhunt.utils.PreyLeftTask
import club.tesseract.manhunt.utils.TaskTimerRunnable
import net.kyori.adventure.text.Component
import net.kyori.adventure.text.format.NamedTextColor
import org.bukkit.Bukkit
import org.bukkit.Location
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.CompassMeta
import org.bukkit.scheduler.BukkitTask
import java.util.*

class GameManager(val plugin: ManHunt) {

    private var gameState: GameState = GameState.WAITING
    var prey: UUID? = null
    private var preyLeft = false
    private var trackingTask: BukkitTask? = null
    private var preyLeftTask: PreyLeftTask? = null
    private val trackingCompass: ItemStack = ItemStack(Material.COMPASS)
    private val spawn: Location

    init {
        val meta = trackingCompass.itemMeta as CompassMeta
        meta.lore(ArrayList<Component>(setOf(Component.text("Compass Of Tracking"))))
        trackingCompass.itemMeta = meta

        spawn = Bukkit.getWorlds()[0].spawnLocation
    }


    fun findPrey(): UUID{
        if(prey == null) {
            prey = Bukkit.getOnlinePlayers().random().uniqueId
            preyLeft = false
        }
        return prey!!
    }

    fun progressGameState(){
        when(gameState){
            GameState.WAITING ->{
                if(Bukkit.getOnlinePlayers().size < 2){
                    Bukkit.broadcast(Component.text("Not enough players to start ManHunt", NamedTextColor.RED))
                    return
                }
                gameState = GameState.COUNTDOWN
                findPrey()
                startTracking()//Count Down
                TaskTimerRunnable.create(plugin, 10, Runnable {
                    progressGameState()
                })
            }
            GameState.COUNTDOWN -> {
                gameState = GameState.HUNTING
                //RELEASE HUNTERS
            }
            GameState.HUNTING ->{
                gameState = GameState.END
                TaskTimerRunnable.create(plugin, 15) {
                    progressGameState()
                }
                //DO WIN LOGIC
            }
            GameState.END ->{
                resetGame()
            }
        }
    }

    fun forceEndGame(reason: String){
        Bukkit.broadcast(Component.text("Game Force Ended: $reason", NamedTextColor.RED))
        resetGame()
    }

    fun resetGame(){
        prey = null
        preyLeft = false
        gameState = GameState.WAITING
        if(preyLeftTask?.isCancelled == false)preyLeftTask?.cancel()
        preyLeftTask = null
        stopTracking()
        //Clear inventories
        Bukkit.getOnlinePlayers().forEach{
            it.inventory.clear()
            it.teleport(spawn)
        }
    }

    fun preyLeft(player: Player){
        if(preyLeftTask != null){
            if(!preyLeftTask!!.isCancelled)return
        }
        preyLeftTask = PreyLeftTask.start(plugin, player.uniqueId)
        preyLeft = true
        Bukkit.broadcast(Component.text("Prey has left the game and has 30 seconds to return before it's considered a forfeit."))
    }

    fun preyReJoined(player: Player){
        if(preyLeftTask == null)return
        if(preyLeftTask!!.isCancelled) {
            preyLeftTask = null
            return
        }
        if(preyLeftTask!!.tryCancel(player.uniqueId)){
            preyLeftTask = null
            Bukkit.broadcast(Component.text("Prey has joined back."))
            preyLeft = false
        }

    }

    fun getGameState(): GameState{
        return gameState
    }

    fun getPrey(): Player {
        return Bukkit.getPlayer(prey!!)!!
    }


    fun startTracking() {
        trackingTask = Bukkit.getScheduler().runTaskTimer(plugin, Runnable {
            val compass = trackingCompassItemStack()
            Bukkit.getOnlinePlayers().forEach {
                if(it.uniqueId == prey)return@forEach
                val inv: Inventory = it.inventory
                if (!inv.contains(Material.COMPASS)) {
                    inv.addItem(compass) // This shouldn't be a issue but inventory may be full...
                }
                if(preyLeft)return@forEach
                it.compassTarget = getPrey().location
            }
        }, 0, 60)
    }

    fun stopTracking() {
        if (trackingTask != null) {
            if (!trackingTask!!.isCancelled) {
                trackingTask!!.cancel()
                trackingTask = null
            }
        }
    }

    fun trackingCompassItemStack(): ItemStack {
        return trackingCompass
    }

    fun getSpawn(): Location{
        return spawn.clone()
    }

}