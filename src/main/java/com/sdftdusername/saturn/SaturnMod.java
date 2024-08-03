package com.sdftdusername.saturn;

import dev.crmodders.cosmicquilt.api.entrypoint.ModInitializer;
import org.quiltmc.loader.api.ModContainer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SaturnMod implements ModInitializer {
	public static final Logger LOGGER = LoggerFactory.getLogger("Gep");

	@Override
	public void onInitialize(ModContainer mod) {
		LOGGER.info("Gep Initialized!");
		CommandsMod.RegisterCommands();
		EntitiesMod.RegisterEntities();
		MobSpawnerMod.RegisterMobs();
	}
}

