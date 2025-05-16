package net.tomweiland.better_repairing;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BetterRepairing implements ModInitializer {
	public static final String MOD_ID = "better_repairing";
	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {
		
	}

	public static Identifier getId(String name) {
		return Identifier.of(MOD_ID, name);
	}
}