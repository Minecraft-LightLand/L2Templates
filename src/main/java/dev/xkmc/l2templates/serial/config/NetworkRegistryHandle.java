package dev.xkmc.l2templates.serial.config;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.eventbus.api.IEventBus;

public record NetworkRegistryHandle(ResourceLocation id, int version, IEventBus modBus, IEventBus forgeBus) {

}
