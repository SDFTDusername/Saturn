package io.github.sdftdusername.saturn.commands;

import com.badlogic.gdx.math.Vector3;
import finalforeach.cosmicreach.chat.Chat;
import finalforeach.cosmicreach.chat.commands.Command;

public class CommandSave extends Command {
    public static boolean saved = false;
    public static Vector3 position;
    public static Vector3 rotation;

    @Override
    public void run(Chat chat, String[] args) {
        super.run(chat, args);

        position = new Vector3(player.getPosition());
        rotation = new Vector3(player.getEntity().viewDirection);
        saved = true;

        chat.sendMessage(world, player, null, "Saved position and rotation!");
    }

    @Override
    public String getShortDescription() {
        return "Save the player position and rotation";
    }
}