package btw.mbtw;

import btw.mbtw.gui.screen.ingame.BrickOvenScreen;
import net.fabricmc.api.ClientModInitializer;
import net.minecraft.client.gui.screen.ingame.HandledScreens;

public class MbtwClient implements ClientModInitializer {
	@Override
	public void onInitializeClient() {
		HandledScreens.register(Mbtw.BRICK_OVEN_SCREEN_HANDLER, BrickOvenScreen::new);
	}
}