package io.github.sdftdusername.saturn.mixins;

import finalforeach.cosmicreach.entities.ItemEntity;
import finalforeach.cosmicreach.items.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ItemEntity.class)
public interface ItemEntityGetItemStack {
    @Accessor(value = "itemStack")
    ItemStack getItemStack();
}
