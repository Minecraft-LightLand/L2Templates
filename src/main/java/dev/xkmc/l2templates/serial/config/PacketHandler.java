package dev.xkmc.l2templates.serial.config;

import dev.xkmc.l2serial.network.BasePacketHandler;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class PacketHandler extends BasePacketHandler {

	@SafeVarargs
	public PacketHandler(NetworkRegistryHandle handle, Function<BasePacketHandler, LoadedPacket<?>>... values) {
		super(handle.id(), handle.version(), values);
		handle.modBus().addListener(this::setup);
	}

	public void setup(FMLCommonSetupEvent event) {
		event.enqueueWork(this::registerPackets);
	}

}
