package com.sdftdusername.saturn;

import com.sdftdusername.saturn.entities.SaturnEntity;
import finalforeach.cosmicreach.entities.EntityCreator;

public class EntitiesMod {
    public static void RegisterEntities() {
        SaturnMod.LOGGER.info("Registering entities");
        EntityCreator.registerEntityCreator("base:entity_saturn", SaturnEntity::new);
    }
}
