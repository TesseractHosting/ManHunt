package club.tesseract.manhunt.listeners

import club.tesseract.manhunt.ManHunt
import club.tesseract.manhunt.listeners.utils.ShadowListener
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.block.CreatureSpawner
import org.bukkit.entity.EntityType
import org.bukkit.event.EventHandler
import org.bukkit.event.block.BlockBreakEvent

class AntiBlockBreaking(plugin: ManHunt) : ShadowListener(plugin) {


    @EventHandler
    fun onBlazeSpawnerBreak(event: BlockBreakEvent){
        if (event.block.type != Material.SPAWNER) return
        val state = event.block.state as CreatureSpawner
        if (state.spawnedType != EntityType.BLAZE) return
        event.isCancelled = true
        event.player.sendMessage(ChatColor.RED.toString() + "Blaze spawners cannot be broken.")
    }

}