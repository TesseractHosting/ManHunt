package club.tesseract.manhunt.utils

import club.tesseract.manhunt.ManHunt
import net.kyori.adventure.text.Component
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import java.util.*

class PreyLeftTask(val plugin: ManHunt, val prey: UUID): BukkitRunnable() {

    companion object{
        fun start(plugin: ManHunt, prey: UUID): PreyLeftTask{
            val task = PreyLeftTask(plugin, prey)
            task.runTaskTimer(plugin, 20, 20)
            return task
        }
    }

    var countDown = 30 // 30 seconds before they are forced end game

    override fun run() {
        if(countDown <= 0){
            cancel()
            plugin.getGameManager().forceEndGame("Prey left during game, and did not return")
            return
        }
        if(countDown <= 5){
            Bukkit.broadcast(Component.text("Prey forfeit in $countDown seconds"))
        }
        countDown -= 1
    }


    fun getTimeLeft(): Int{
        return countDown
    }

    fun tryCancel(uuid: UUID): Boolean{
        if(prey == uuid){
            cancel()
            return true
        }else{
            return false
        }
    }
}