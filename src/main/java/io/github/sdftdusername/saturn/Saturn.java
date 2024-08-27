package io.github.sdftdusername.saturn;

import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer;
import org.quiltmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Saturn implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Saturn");

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Saturn Mod Initialized!");
		// CommandsMod.RegisterCommands();
		EntitiesMod.RegisterEntities();
	}
}

