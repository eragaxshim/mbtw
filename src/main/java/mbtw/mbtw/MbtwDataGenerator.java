package mbtw.mbtw;

import mbtw.mbtw.data.client.MbtwModelGenerator;
import mbtw.mbtw.data.server.MbtwBlockTagGenerator;
import mbtw.mbtw.data.server.MbtwItemTagGenerator;
import mbtw.mbtw.data.server.MbtwRecipeGenerator;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;

public class MbtwDataGenerator implements DataGeneratorEntrypoint {
	@Override
	public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
		FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
		pack.addProvider(MbtwModelGenerator::new);
		pack.addProvider(MbtwRecipeGenerator::new);
		FabricTagProvider.BlockTagProvider blockTagProvider = pack.addProvider(MbtwBlockTagGenerator::new);
		pack.addProvider((o, e) -> new MbtwItemTagGenerator(o, e, blockTagProvider));
	}
}
