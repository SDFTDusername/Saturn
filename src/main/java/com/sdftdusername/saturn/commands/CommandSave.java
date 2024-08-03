package com.sdftdusername.saturn.commands;

import com.badlogic.gdx.math.Vector3;
import com.sdftdusername.saturn.mixins.CommandGetFields;
import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.chat.commands.Command;

public class CommandSave extends Command {
    public static boolean saved = false;
    public static Vector3 position;
    public static Vector3 rotation;

    @Override
    public void run(Chat chat, String[] args) {
        super.run(chat, args);

        position = new Vector3(((CommandGetFields)this).getPlayer().getPosition());
        rotation = new Vector3(((CommandGetFields)this).getPlayer().getEntity().viewDirection);
        saved = true;

        chat.sendMessage(((CommandGetFields)this).getWorld(), ((CommandGetFields)this).getPlayer(), null, "Saved position and rotation!");
    }

    @Override
    public String getDescription() {
        return "Save the player position and rotation";
    }
}