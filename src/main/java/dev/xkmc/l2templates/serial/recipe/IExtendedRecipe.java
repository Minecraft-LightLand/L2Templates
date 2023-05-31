package dev.xkmc.l2templates.serial.recipe;

import com.google.gson.JsonObject;
import net.minecraft.world.item.crafting.RecipeSerializer;

public interface IExtendedRecipe {

	void addAdditional(JsonObject json);

	RecipeSerializer<?> getType();

}
