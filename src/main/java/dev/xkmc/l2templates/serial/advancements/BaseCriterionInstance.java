package dev.xkmc.l2templates.serial.advancements;

import com.google.gson.JsonObject;
import dev.xkmc.l2serial.serialization.SerialClass;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.SerializationContext;
import net.minecraft.resources.ResourceLocation;

@SerialClass
public class BaseCriterionInstance<T extends BaseCriterionInstance<T, R>, R extends BaseCriterion<T, R>> extends AbstractCriterionTriggerInstance {

	public BaseCriterionInstance(ResourceLocation id, EntityPredicate.Composite player) {
		super(id, player);
	}

	@Override
	public JsonObject serializeToJson(SerializationContext pConditions) {
		JsonObject obj = super.serializeToJson(pConditions);
		JsonCodec.toJsonObject(this, obj);
		return obj;
	}
}
