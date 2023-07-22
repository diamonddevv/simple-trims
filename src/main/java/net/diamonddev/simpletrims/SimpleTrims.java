package net.diamonddev.simpletrims;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimpleTrims implements ModInitializer {
	public static final String MODID = "simpletrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	private static final SimpleTrimDefinitionLoader TRIM_MAT_DEF_LOADER = new SimpleTrimDefinitionLoader();


	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(TRIM_MAT_DEF_LOADER);

		LOGGER.info("the design is very human (initialized)");
	}

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}
}