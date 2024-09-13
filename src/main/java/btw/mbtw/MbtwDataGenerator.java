package btw.mbtw;

import btw.mbtw.data.client.MbtwModelGenerator;
import btw.mbtw.data.server.MbtwRecipeGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;

public class MbtwDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(MbtwModelGenerator::new);
		pack.addProvider(MbtwRecipeGenerator::new);
	}
}
