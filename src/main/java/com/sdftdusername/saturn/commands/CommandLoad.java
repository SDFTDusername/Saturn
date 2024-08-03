package com.sdftdusername.saturn.commands;

import com.sdftdusername.saturn.mixins.CommandGetFields;
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

        ((CommandGetFields)this).getPlayer().getEntity().position.set(CommandSave.position);
        ((CommandGetFields)this).getPlayer().getEntity().viewDirection.set(CommandSave.rotation);
    }

    @Override
    public String getDescription() {
        return "Loads the player position and rotation";
    }
}