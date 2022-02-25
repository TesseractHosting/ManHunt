package club.tesseract.manhunt

import club.tesseract.manhunt.commands.utils.DynamicCommand
import club.tesseract.manhunt.commands.utils.ShadowCommand
import club.tesseract.manhunt.listeners.utils.ShadowListener
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin
import org.reflections.Reflections

class ManHunt: JavaPlugin() {

    private lateinit var gameManager: GameManager
    private lateinit var papiManager: PapiManager

    override fun onEnable() {
        gameManager = GameManager(this)
        papiManager = PapiManager(this)

        if(Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            papiManager.register();
        }

        registerListeners()
        registerCommands()

        logger.info("Plugin Enabled")
    }


    override fun onDisable() {
        gameManager.resetGame()
        logger.info("Plugin Disabled")
    }


    /* Register Commands & Listeners */
    private fun registerCommands() {
        val packageName = javaClass.getPackage().name
        for (clazz in Reflections("$packageName.commands").getSubTypesOf(
            ShadowCommand::class.java
        )) {
            try {
                val cmd: ShadowCommand = clazz.getDeclaredConstructor(JavaPlugin::class.java).newInstance(this)
                val cmdName: String = cmd.getCommandInfo().name
                if (cmdName.isEmpty()) continue
                var command = getCommand(cmdName)
                if (command == null) {
                    logger.info("Injecting Command: $cmdName")
                    if (!addCommandToCommandMap(cmd)) {
                        logger.info("Failed to add $cmdName to command map")
                        continue
                    }
                    command = getCommand(cmdName)
                    if (command == null) {
                        logger.info("Command $cmdName failed to inject")
                        continue
                    }
                }
                logger.info("Registering command $cmdName")
                command.setExecutor(cmd)
                command.tabCompleter = cmd
                if (cmd.getCommandInfo().permission.isEmpty()) command.permission = null else command.permission =
                    cmd.getCommandInfo().permission
                command.description = cmd.getCommandInfo().description
                command.usage = cmd.getCommandInfo().usage
                command.aliases = cmd.aliases
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    private fun registerListeners(){
        val packageName = javaClass.`package`.name
        for (clazz in Reflections("$packageName.listeners").getSubTypesOf(
            ShadowListener::class.java
        )){
            try{
                val listener: ShadowListener = clazz.getDeclaredConstructor(this::class.java).newInstance(this)
                logger.info("Registering ${listener.javaClass.name.split(".").last()}")
                Bukkit.getPluginManager().registerEvents(listener, this)
                logger.info("Registered ${listener.javaClass.name.split(".").last()}")
            }catch (e: Exception){
                e.printStackTrace()
            }
        }
    }
    private fun addCommandToCommandMap(command: ShadowCommand): Boolean {
        return Bukkit.getCommandMap()
            .register(command.getCommandInfo().name, name.lowercase(), DynamicCommand(command))
    }

    fun getGameManager(): GameManager{
        return gameManager
    }

    fun getPapiManager(): PapiManager{
        return papiManager
    }

}