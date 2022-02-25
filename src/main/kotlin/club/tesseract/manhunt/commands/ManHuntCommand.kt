package club.tesseract.manhunt.commands

import club.tesseract.manhunt.GameManager
import club.tesseract.manhunt.ManHunt
import club.tesseract.manhunt.commands.utils.ShadowCommand
import club.tesseract.manhunt.commands.utils.ShadowCommandInfo
import club.tesseract.manhunt.utils.GameState
import org.bukkit.Bukkit
import org.bukkit.command.CommandSender
import org.bukkit.plugin.java.JavaPlugin

@ShadowCommandInfo("manhunt")
class ManHuntCommand(plugin: JavaPlugin) : ShadowCommand(plugin) {

    val gameManager: GameManager = (plugin as ManHunt).getGameManager()

    override fun execute(sender: CommandSender, args: Array<String>) {
        if(!sender.isOp)return sender.sendMessage("No!")
        if(args.isEmpty())return sender.sendMessage("/manhunt [start,forceend,setprey,?]")

        when(args[0]){
            "start" -> {
                if(gameManager.getGameState() == GameState.WAITING){
                    sender.sendMessage("Game has been progressed!")
                    gameManager.progressGameState()
                }
                else sender.sendMessage("Game isn't waiting...")
            }
            "forceend" ->{
                if(args.size < 2) return sender.sendMessage("Tell me why you want to force end the game!!!")
                gameManager.forceEndGame(args.slice(1 until args.size).joinToString(" "))
            }
            "setprey" ->{
                if(args.size < 2){
                    sender.sendMessage("Specify who you want to be a prey")
                    return
                }
                val name = args[1]
                gameManager.prey = Bukkit.getPlayerUniqueId(name)
                sender.sendMessage("Prey has been set to $name")
            }
            else ->{
                sender.sendMessage("Unknown argument: /manhunt [start,forceend,setprey,?]")
            }
        }
    }

}