package net.diamonddev.simpletrims;

import net.diamonddev.simpletrims.data.SimpleTrimsDataLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SimpleTrims implements ModInitializer {
	public static final String MODID = "simpletrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final SimpleTrimsDataLoader TRIM_DATA = new SimpleTrimsDataLoader();

	private static final String[] MESSAGES = new String[] {
			"the design is very human",
			"don't try to send textures over packets worst mistake of my life",
			"this solves  a very specific problem no one probably ever had",
			"what the hell is an encoded palette",
			"simple the trim"
	};


	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(TRIM_DATA);

		LOGGER.info("{} (initialized)", MESSAGES[new Random().nextInt(MESSAGES.length)]);
	}

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}

	public static boolean testRegex(String input, String pattern) {
		Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = compiledPattern.matcher(input);
		return matcher.matches();
	}
}