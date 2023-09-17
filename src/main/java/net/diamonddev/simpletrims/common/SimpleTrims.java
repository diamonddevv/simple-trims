package net.diamonddev.simpletrims.common;

import net.diamonddev.simpletrims.common.data.SimpleTrimsDataLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleFactory;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleRegistry;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.fabricmc.loader.impl.FabricLoaderImpl;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.world.GameRules;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class SimpleTrims implements ModInitializer {
	public static final String MODID = "simpletrims";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

	public static final SimpleTrimsDataLoader TRIM_DATA = new SimpleTrimsDataLoader();

	private static final String[] GOOFY_MESSAGES = new String[] {
			"the design is very human",
			"don't try to send textures over packets worst mistake of my life",
			"this solves a very specific problem no one probably ever had",
			"what the hell is an encoded palette",
			"niinku viulunkieli !?",
			"now with apoli!",
			"trims but simpler",
			"simple the trim",
			"you're welcome, i fixed the extra space",
			"also try terraria!",
			"so i added some more of these",
			"try adding an emissive trim!",
			"@diamonddevv (discord, twitter) was here",
			"i paid for the whole two megabytes im gonna use the whole two megabytes",
			"can we go much higher (yes, we can)"
	};

	public static GameRules.Key<GameRules.BooleanRule> SHOULD_PROVIDE_TRIM_POWERS;

	// mods
	public static boolean hasBetterTrimTooltips;

	@Override
	public void onInitialize() {
		ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(TRIM_DATA);

		//SHOULD_PROVIDE_TRIM_POWERS = // gamerule
		//		GameRuleRegistry.register("shouldSimpleTrimsProvideApoliPowers", GameRules.Category.PLAYER, GameRuleFactory.createBooleanRule(true));

		hasBetterTrimTooltips = FabricLoader.getInstance().isModLoaded("better-trim-tooltips");
		// i think the compat for this is on bettertrimtooltips side, will pr better trim tooltips



		LOGGER.info("{} (initialized simpletrims)", getGoofyMessage());
	}

	public static Identifier id(String path) {
		return new Identifier(MODID, path);
	}

	public static boolean testRegex(String input, String pattern) {
		Pattern compiledPattern = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		Matcher matcher = compiledPattern.matcher(input);
		return matcher.matches();
	}

	private static String getGoofyMessage() {
		return GOOFY_MESSAGES[new Random().nextInt(GOOFY_MESSAGES.length)];
	}
}