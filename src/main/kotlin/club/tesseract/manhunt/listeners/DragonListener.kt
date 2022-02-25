package club.tesseract.manhunt.listeners

import club.tesseract.manhunt.ManHunt
import club.tesseract.manhunt.listeners.utils.ShadowListener
import club.tesseract.manhunt.utils.GameState
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.entity.EntityDeathEvent

class DragonListener(plugin: ManHunt) : ShadowListener(plugin) {


    @EventHandler
    fun dragonDeathEvent(event: EntityDeathEvent){
        if(event.entityType != EntityType.ENDER_DRAGON)return
        if(plugin.getGameManager().getGameState() != GameState.HUNTING)return
        plugin.getGameManager().progressGameState()
    }

}