package io.github.sdftdusername.saturn.commands;

import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.chat.commands.Command;

public class CommandLoad extends Command {
    @Override
    public void run(Chat chat, String[] args) {
        super.run(chat, args);

        if (!CommandSave.saved) {
            commandError("No position and rotation saved.");
            return;
        }

        player.getEntity().position.set(CommandSave.position);
        player.getEntity().viewDirection.set(CommandSave.rotation);
    }

    @Override
    public String getShortDescription() {
        return "Loads the player position and rotation";
    }
}