package club.tesseract.manhunt.listeners

import club.tesseract.manhunt.ManHunt
import club.tesseract.manhunt.listeners.utils.ShadowListener
import club.tesseract.manhunt.utils.GameState
import net.kyori.adventure.text.Component
import org.bukkit.entity.EntityType
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerMoveEvent

class InteractionListener(plugin: ManHunt) : ShadowListener(plugin) {

    val gameManager = plugin.getGameManager()

    fun canInteract(player: Player): Boolean{
        if(gameManager.getGameState() != GameState.HUNTING && gameManager.getGameState() != GameState.COUNTDOWN){
            return false
        }
        if(player.uniqueId != gameManager.prey && gameManager.getGameState() == GameState.COUNTDOWN) return false
        return true
    }

    @EventHandler
    fun onDamage(event: EntityDamageEvent){
        if(event.entityType != EntityType.PLAYER)return
        if(canInteract(event.entity as Player))return
        event.isCancelled = true
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent){
        if(canInteract(event.player))return
        event.isCancelled = true
    }


    @EventHandler
    fun onBreakBlock(event: BlockBreakEvent){
        if(!canInteract(event.player))event.isCancelled = true
    }

    @EventHandler
    fun onPlaceBlock(event: BlockBreakEvent){
        if(!canInteract(event.player))event.isCancelled = true
    }

    @EventHandler
    fun onMovement(event: PlayerMoveEvent){
        if(canInteract(event.player))return
        if(event.hasExplicitlyChangedBlock()){
            val spawn = gameManager.getSpawn()
            val to = event.to
            if(to.x !in spawn.x-5..spawn.x+5 || to.z !in spawn.z-5..spawn.z+5) {
                event.player.teleport(spawn)
                event.player.sendMessage(Component.text("Please wait in spawn till the game start!"))
            }
        }
    }


}