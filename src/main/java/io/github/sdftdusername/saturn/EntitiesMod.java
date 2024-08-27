package io.github.sdftdusername.saturn;

import finalforeach.cosmicreach.entities.EntityCreator;
import io.github.sdftdusername.saturn.entities.SaturnEntity;

public class EntitiesMod {
    public static void RegisterEntities() {
        Saturn.LOGGER.info("Registering entities");
        EntityCreator.registerEntityCreator(SaturnEntity.ENTITY_TYPE_ID, SaturnEntity::new);
    }
}
