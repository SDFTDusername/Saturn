package io.github.sdftdusername.saturn.mixins;

import com.badlogic.gdx.utils.ObjectMap;
import finalforeach.cosmicreach.rendering.entities.EntityModelInstance;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
@Mixin(EntityModelInstance.class)
public interface EntityModelInstanceGetBoneMap {
    @Accessor(value = "boneMap")
    ObjectMap<String, Object> getBoneMap();
}
