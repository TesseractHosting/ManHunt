package club.tesseract.manhunt.listeners

import club.tesseract.manhunt.ManHunt
import club.tesseract.manhunt.listeners.utils.ShadowListener
import club.tesseract.manhunt.utils.GameState
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerMoveEvent

class MovementListener(plugin: ManHunt) : ShadowListener(plugin) {

    val gameManager = plugin.getGameManager()

    @EventHandler
    fun onMovement(event: PlayerMoveEvent){
        if(gameManager.getGameState() != GameState.HUNTING)return
        if(gameManager.prey == event.player.uniqueId){
            plugin.getPapiManager().cacheDistance.clear()
            plugin.getPapiManager().cacheLocation[event.player.world.environment] = event.player.location
        }else{
            plugin.getPapiManager().cacheDistance.remove(event.player)
        }
    }
}