package net.diamonddev.simpletrims.common.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.minecraft.command.argument.EntityArgumentType;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.tag.ItemTags;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

import static net.minecraft.server.command.CommandManager.argument;
import static net.minecraft.server.command.CommandManager.literal;

public class TrimCommand {
    public static final DynamicCommandExceptionType IDK_BRO_IT_FAILED = new DynamicCommandExceptionType(obj ->
            new LiteralMessage("Failed to apply trim? pattern -> " + ((String[])obj)[0] + ", material -> " + ((String[])obj)[1]));

    public static void register(CommandDispatcher<ServerCommandSource> dispatcher, @Nullable DynamicRegistryManager DRM) {
        dispatcher.register(
                literal("trim").requires(src -> src.hasPermissionLevel(4))
                        .then(argument("players", EntityArgumentType.players())
                                .then(argument("pattern", ArmorTrimPatternArgument.pattern())
                                        .then(argument("material", ArmorTrimMaterialArgument.material())
                                                .executes(ctx -> executeTrimHandheldArmor(ctx, DRM))
                                        )
                                )
                        )
        );
    }

    private static int executeTrimHandheldArmor(CommandContext<ServerCommandSource> context, @Nullable DynamicRegistryManager DRM) throws CommandSyntaxException {
        Collection<ServerPlayerEntity> spes = EntityArgumentType.getPlayers(context, "players");
        var pattern = CmdInit.PATTERN.get(ArmorTrimPatternArgument.get(context, "pattern").toString());
        var material = CmdInit.MATERIAL.get(ArmorTrimMaterialArgument.get(context, "material").toString());

        if (pattern.isPresent() && material.isPresent()) {
            spes.forEach(player -> {
                ItemStack mainstack = player.getStackInHand(Hand.MAIN_HAND).copy();
                if (mainstack.isIn(ItemTags.TRIMMABLE_ARMOR)) {
                    ArmorTrim trim = new ArmorTrim(material.get(), pattern.get());
                    if (ArmorTrim.apply(DRM, mainstack, trim)) {
                        player.setStackInHand(Hand.MAIN_HAND, mainstack);
                    };
                }
            });
            return 1;
        }

        throw IDK_BRO_IT_FAILED.create(new String[] {
            ArmorTrimPatternArgument.get(context, "pattern").toString(),
            ArmorTrimMaterialArgument.get(context, "material").toString()
        });
    }
}
