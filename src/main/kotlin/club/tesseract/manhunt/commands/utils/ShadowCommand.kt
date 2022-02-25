package club.tesseract.manhunt.commands.utils

import club.tesseract.manhunt.ManHunt
import org.bukkit.command.Command
import org.bukkit.command.CommandSender
import org.bukkit.command.TabExecutor
import org.bukkit.entity.Player
import org.bukkit.plugin.java.JavaPlugin

abstract class ShadowCommand(plugin: JavaPlugin) : TabExecutor {

    protected val plugin: ManHunt
    private val commandInfo: ShadowCommandInfo
    var aliases: ArrayList<String> = ArrayList()

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<String>): Boolean {
        val isPlayer = sender is Player
        if (commandInfo.permission.isNotEmpty()) {
            if (!sender.hasPermission(commandInfo.permission)) {
                sender.sendMessage(commandInfo.permissionErr)
                return true
            }
        }
        if (commandInfo.isPlayerOnly) {
            if (!isPlayer) {
                sender.sendMessage(commandInfo.isPlayerOnlyErr)
                return true
            }
            execute(sender as Player, args)
            return true
        }
        execute(sender, args)
        return true
    }

    override fun onTabComplete(
        sender: CommandSender,
        command: Command,
        alias: String,
        args: Array<String>
    ): List<String> {
        val isPlayer = sender is Player
        if (commandInfo.permission.isNotEmpty()) {
            if (!sender.hasPermission(commandInfo.permission)) {
                return ArrayList()
            }
        }
        return if (commandInfo.isPlayerOnly) {
            if (!isPlayer) {
                ArrayList()
            } else tabComplete(sender as Player, args)
        } else tabComplete(sender, args)
    }

    open fun execute(player: Player, args: Array<String>) {}
    open fun execute(sender: CommandSender, args: Array<String>) {}
    open fun tabComplete(sender: CommandSender, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }

    open fun tabComplete(player: Player, args: Array<String>): ArrayList<String> {
        return ArrayList()
    }

    fun getCommandInfo(): ShadowCommandInfo {
        return commandInfo
    }

    val usage: String
        get() = getCommandInfo().usage.replace("<command>", getCommandInfo().name)

    init {
        if(plugin !is ManHunt) throw Exception("Wrong Plugin provided")
        this.plugin = plugin
        commandInfo = javaClass.getDeclaredAnnotation(ShadowCommandInfo::class.java)
        val alias: ShadowCommandAlias? = javaClass.getDeclaredAnnotation(ShadowCommandAlias::class.java)
        aliases = if(alias == null){
            ArrayList()
        }else{
            ArrayList(alias.alias.toList())
        }
    }
}
