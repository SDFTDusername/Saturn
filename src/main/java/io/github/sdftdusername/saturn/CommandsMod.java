package io.github.sdftdusername.saturn;

import finalforeach.cosmicreach.chat.commands.Command;
import io.github.sdftdusername.saturn.commands.CommandLoad;
import io.github.sdftdusername.saturn.commands.CommandSave;

public class CommandsMod {
    public static void RegisterCommands() {
        Saturn.LOGGER.info("Registering commands");
        Command.registerCommand(CommandSave::new, "save");
        Command.registerCommand(CommandLoad::new, "load");
    }
}
