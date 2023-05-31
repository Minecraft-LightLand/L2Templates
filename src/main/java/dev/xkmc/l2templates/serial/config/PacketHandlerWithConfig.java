package dev.xkmc.l2templates.serial.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import dev.xkmc.l2serial.network.BasePacketHandler;
import dev.xkmc.l2serial.serialization.codec.JsonCodec;
import dev.xkmc.l2serial.util.Wrappers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.NetworkDirection;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Stream;

@SuppressWarnings("unused")
public class PacketHandlerWithConfig extends PacketHandler {

	private static <T> T[] concat(T first, T[] second) {
		T[] ans = Arrays.copyOf(second, second.length + 1);
		ans[0] = first;
		System.arraycopy(second, 0, ans, 1, second.length);
		return ans;
	}

	static final Map<ResourceLocation, PacketHandlerWithConfig> INTERNAL = new ConcurrentHashMap<>();

	public HashMap<String, BaseConfig> configs = new HashMap<>();

	private final PreparableReloadListener listener;
	private final Map<String, CachedConfig<?>> cache = new HashMap<>();
	final List<Runnable> listener_before = new ArrayList<>();
	final List<Runnable> listener_after = new ArrayList<>();

	@SafeVarargs
	public PacketHandlerWithConfig(NetworkRegistryHandle handle, String config_path, Function<BasePacketHandler, LoadedPacket<?>>... values) {
		super(handle, concat(e -> e.create(SyncPacket.class, NetworkDirection.PLAY_TO_CLIENT), values));
		INTERNAL.put(handle.id(), this);
		listener = new ConfigReloadListener(config_path);
		handle.forgeBus().addListener(this::onDatapackSync);
		handle.forgeBus().addListener(this::addReloadListeners);
	}

	public void onDatapackSync(OnDatapackSyncEvent event) {
		SyncPacket packet = new SyncPacket(this, configs);
		if (event.getPlayer() == null) toAllClient(packet);
		else toClientPlayer(packet, event.getPlayer());

	}

	public void addReloadListeners(AddReloadListenerEvent event) {
		if (listener != null) {
			event.addListener(listener);
		}
	}

	public void addBeforeReloadListener(Runnable runnable) {
		listener_before.add(runnable);
	}

	public void addAfterReloadListener(Runnable runnable) {
		listener_after.add(runnable);
	}

	public <T extends BaseConfig> void addCachedConfig(String id, Function<Stream<Map.Entry<String, BaseConfig>>, T> loader) {
		CachedConfig<T> c = new CachedConfig<>(id, loader);
		cache.put(id, c);
		addAfterReloadListener(() -> c.result = null);
	}

	public <T extends BaseConfig> T getCachedConfig(String id) {
		return Wrappers.cast(cache.get(id).load());
	}

	public Stream<Map.Entry<String, BaseConfig>> getConfigs(String id) {
		return configs.entrySet().stream()
				.filter(e -> new ResourceLocation(e.getKey()).getPath().split("/")[0].equals(id));
	}

	private class CachedConfig<T extends BaseConfig> {

		private final Function<Stream<Map.Entry<String, BaseConfig>>, T> function;
		private final String id;

		private T result;

		CachedConfig(String id, Function<Stream<Map.Entry<String, BaseConfig>>, T> function) {
			this.id = id;
			this.function = function;
		}

		T load() {
			if (result != null) {
				return result;
			}
			result = function.apply(getConfigs(id));
			result.id = new ResourceLocation(CHANNEL_NAME.getNamespace(), id);
			return result;
		}

	}

	private class ConfigReloadListener extends SimpleJsonResourceReloadListener {

		public ConfigReloadListener(String path) {
			super(new Gson(), path);
		}

		@Override
		protected void apply(Map<ResourceLocation, JsonElement> map, ResourceManager manager, ProfilerFiller filler) {
			listener_before.forEach(Runnable::run);
			map.forEach((k, v) -> {
				if (!k.getNamespace().startsWith("_")) {
					if (!ModList.get().isLoaded(k.getNamespace())) {
						return;
					}
				}
				BaseConfig config = JsonCodec.from(v, BaseConfig.class, null);
				if (config != null) {
					config.id = k;
					configs.put(k.toString(), config);
				}
			});
			listener_after.forEach(Runnable::run);
		}
	}

}
