package dev.xkmc.l2templates.templates.base;

import com.google.common.base.Suppliers;
import com.tterrag.registrate.AbstractRegistrate;
import com.tterrag.registrate.builders.AbstractBuilder;
import com.tterrag.registrate.builders.BuilderCallback;
import com.tterrag.registrate.providers.RegistrateLangProvider;
import com.tterrag.registrate.util.nullness.NonNullSupplier;
import com.tterrag.registrate.util.nullness.NonnullType;
import dev.xkmc.l2serial.serialization.custom_handler.RLClassHandler;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.*;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class L2Registrate extends AbstractRegistrate<L2Registrate> {
	/**
	 * Construct a new Registrate for the given mod ID.
	 *
	 * @param modid The mod ID for which objects will be registered
	 */
	public L2Registrate(String modid) {
		super(modid);
		registerEventListeners(FMLJavaModLoadingContext.get().getModEventBus());
	}

	public <T extends NamedEntry<T>, P extends T> GenericBuilder<T, P> generic(RegistryInstance<T> cls, String id, NonNullSupplier<P> sup) {
		return entry(id, cb -> new GenericBuilder<>(this, id, cb, cls.key(), sup));
	}

	/*
	public FluidBuilder<VirtualFluid, L2Registrate> virtualFluid(String name) {
		return entry(name,
				c -> new VirtualFluidBuilder<>(self(), self(), name, c, new ResourceLocation(getModid(), "fluid/" + name + "_still"),
						new ResourceLocation(getModid(), "fluid/" + name + "_flow"), null, VirtualFluid::new));
	}*/

	public <T extends Recipe<?>> RegistryObject<RecipeType<T>> recipe(DeferredRegister<RecipeType<?>> type, String id) {
		return type.register(id, () -> new RecipeType<>() {
		});
	}

	@SuppressWarnings({"unchecked", "unsafe"})
	public <E extends NamedEntry<E>> RegistryInstance<E> newRegistry(String id, Class<?> cls) {
		ResourceKey<Registry<E>> key = makeRegistry(id, () ->
				new RegistryBuilder<E>().onCreate((r, s) ->
						new RLClassHandler<>((Class<E>) cls, () -> r)));
		return new RegistryInstance<>(Suppliers.memoize(() -> RegistryManager.ACTIVE.getRegistry(key)), key);
	}

	public record RegistryInstance<E extends NamedEntry<E>>(Supplier<IForgeRegistry<E>> supplier,
															ResourceKey<Registry<E>> key) implements Supplier<IForgeRegistry<E>> {

		@Override
		public IForgeRegistry<E> get() {
			return supplier().get();
		}
	}

	public static class GenericBuilder<T extends NamedEntry<T>, P extends T> extends AbstractBuilder<T, P, L2Registrate, GenericBuilder<T, P>> {

		private final NonNullSupplier<P> sup;

		GenericBuilder(L2Registrate parent, String name, BuilderCallback callback, ResourceKey<Registry<T>> registryType, NonNullSupplier<P> sup) {
			super(parent, parent, name, callback, registryType);
			this.sup = sup;
		}

		@Override
		protected @NonnullType @NotNull P createEntry() {
			return sup.get();
		}

		public GenericBuilder<T, P> defaultLang() {
			return lang(NamedEntry::getDescriptionId, RegistrateLangProvider.toEnglishName(this.getName()));
		}

	}

}
