package club.tesseract.manhunt.listeners

import club.tesseract.manhunt.GameManager
import club.tesseract.manhunt.ManHunt
import club.tesseract.manhunt.listeners.utils.ShadowListener
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDamageByEntityEvent
import org.bukkit.event.entity.PlayerDeathEvent
import org.bukkit.event.player.PlayerJoinEvent
import org.bukkit.event.player.PlayerQuitEvent

class PreyListener(plugin: ManHunt) : ShadowListener(plugin) {

    private val gameManager: GameManager = plugin.getGameManager()

    @EventHandler
    fun onLeave(event: PlayerQuitEvent){
        if(gameManager.prey == event.player.uniqueId){
            gameManager.preyLeft(event.player)
        }
    }

    @EventHandler
    fun onJoin(event: PlayerJoinEvent){
        if(gameManager.prey == event.player.uniqueId){
            gameManager.preyReJoined(event.player)
        }
    }

    @EventHandler
    fun onDamage(event: EntityDamageByEntityEvent){
        if(event.damager.type != EntityType.PLAYER || event.entity.type != EntityType.PLAYER)return
        if(event.damager.uniqueId == gameManager.prey || event.entity.uniqueId == gameManager.prey)return
        event.isCancelled = true
    }

    @EventHandler
    fun onDeath(event: PlayerDeathEvent){
        if(event.player.uniqueId != gameManager.prey)return
    }



}