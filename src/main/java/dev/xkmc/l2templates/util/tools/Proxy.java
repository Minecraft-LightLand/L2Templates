package dev.xkmc.l2templates.util.tools;

import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.server.ServerLifecycleHooks;

import javax.annotation.Nullable;
import java.util.Optional;

public class Proxy {

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static LocalPlayer getClientPlayer() {
		return Minecraft.getInstance().player;
	}

	@Nullable
	public static Player getPlayer() {
		return DistExecutor.unsafeRunForDist(() -> Proxy::getClientPlayer, () -> () -> null);
	}

	@Nullable
	public static Level getWorld() {
		return DistExecutor.unsafeRunForDist(() -> Proxy::getClientWorld, () -> () -> Proxy.getServer().map(MinecraftServer::overworld).orElse(null));
	}

	@Nullable
	@OnlyIn(Dist.CLIENT)
	public static ClientLevel getClientWorld() {
		return Minecraft.getInstance().level;
	}

	public static Optional<MinecraftServer> getServer() {
		return Optional.ofNullable(ServerLifecycleHooks.getCurrentServer());
	}

}
