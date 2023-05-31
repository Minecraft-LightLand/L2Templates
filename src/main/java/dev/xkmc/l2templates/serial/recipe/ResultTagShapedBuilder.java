package dev.xkmc.l2templates.serial.recipe;

import com.google.gson.JsonObject;
import dev.xkmc.l2serial.serialization.custom_handler.StackHelper;
import net.minecraft.advancements.AdvancementRewards;
import net.minecraft.advancements.RequirementsStrategy;
import net.minecraft.advancements.critereon.RecipeUnlockedTrigger;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingBookCategory;
import net.minecraft.world.item.crafting.RecipeSerializer;

import java.util.function.Consumer;

public class ResultTagShapedBuilder extends ShapedRecipeBuilder implements IExtendedRecipe {

	private final ItemStack stack;

	public ResultTagShapedBuilder(ItemStack stack) {
		super(RecipeCategory.MISC, stack.getItem(), stack.getCount());
		this.stack = stack;
	}

	public void save(Consumer<FinishedRecipe> pvd, ResourceLocation id) {
		this.ensureValid(id);
		this.advancement.parent(new ResourceLocation("recipes/root"))
				.addCriterion("has_the_recipe", RecipeUnlockedTrigger.unlocked(id))
				.rewards(AdvancementRewards.Builder.recipe(id)).requirements(RequirementsStrategy.OR);
		pvd.accept(new ExtendedRecipeResult(new Result(id, result, count,
				this.group == null ? "" : this.group, CraftingBookCategory.MISC, rows, key, advancement,
				new ResourceLocation(id.getNamespace(), "recipes/" + id.getPath()), false),
				this));
	}

	@Override
	public void addAdditional(JsonObject json) {
		json.add("result", StackHelper.serializeForgeItemStack(stack));
	}

	@Override
	public RecipeSerializer<?> getType() {
		return RecipeSerializer.SHAPED_RECIPE;
	}

}
