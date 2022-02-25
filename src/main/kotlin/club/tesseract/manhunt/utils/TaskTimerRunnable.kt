package club.tesseract.manhunt.utils

import club.tesseract.manhunt.ManHunt
import org.bukkit.Bukkit
import org.bukkit.scheduler.BukkitRunnable
import kotlin.math.floor

class TaskTimerRunnable(val plugin: ManHunt, val endTime: Int, val runnable: Runnable): BukkitRunnable() {

    companion object{

        var currentTimer: TaskTimerRunnable? = null

        fun create(plugin: ManHunt, endTime: Int,  runnable: Runnable): TaskTimerRunnable{
            val task = TaskTimerRunnable(plugin, endTime, runnable)
            currentTimer = task
            task.runTaskTimer(plugin, 20, 20)
            return task
        }
    }


    private var currentTime = 0

    override fun run() {
        if (endTime <= currentTime) {
            Bukkit.getScheduler().runTask(plugin, runnable)
            currentTimer = null
            cancel()
        }
        currentTime++
    }

    fun getDisplayTest(): String{
        val seconds: Int = endTime - currentTime
        val minutes = floor(seconds / 60.0).toInt()
        val formattedMinutes = String.format("%02d", minutes)
        val formattedSeconds = String.format("%02d", seconds % 60)
        return "$formattedMinutes:$formattedSeconds"
    }
}