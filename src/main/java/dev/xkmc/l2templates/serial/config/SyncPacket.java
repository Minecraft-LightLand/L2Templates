package dev.xkmc.l2templates.serial.config;

import dev.xkmc.l2serial.network.SerialPacketBase;
import dev.xkmc.l2serial.serialization.SerialClass;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;

import java.util.HashMap;

@SerialClass
public class SyncPacket extends SerialPacketBase {

	@SerialClass.SerialField
	public ResourceLocation id;

	@SerialClass.SerialField
	public HashMap<String, BaseConfig> map = null;

	@Deprecated
	public SyncPacket() {

	}

	SyncPacket(PacketHandlerWithConfig handler, HashMap<String, BaseConfig> map) {
		this.id = handler.CHANNEL_NAME;
		this.map = map;
	}

	@Override
	public void handle(NetworkEvent.Context ctx) {
		if (map != null) {
			var handler = PacketHandlerWithConfig.INTERNAL.get(id);
			handler.listener_before.forEach(Runnable::run);
			handler.configs = map;
			map.forEach((k, v) -> v.id = new ResourceLocation(k));
			handler.listener_after.forEach(Runnable::run);
		}
	}

}
