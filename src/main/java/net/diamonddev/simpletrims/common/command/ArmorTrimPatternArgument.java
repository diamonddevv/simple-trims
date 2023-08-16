package net.diamonddev.simpletrims.common.command;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.DynamicCommandExceptionType;
import net.diamonddev.iliterallyonlyneedonelibgeneticsthingandtheresnowayimjijjingitforthatsohereitis.StringArrayListArgType;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.ArrayList;

public class ArmorTrimPatternArgument extends StringArrayListArgType {

    private static final DynamicCommandExceptionType INVALID_EXCEPTION =
            new DynamicCommandExceptionType((id) -> Text.literal("Trim Pattern " + id + " was not found in register!"));

    private ArmorTrimPatternArgument() {}
    public static ArmorTrimPatternArgument pattern() {return new ArmorTrimPatternArgument();}

    public static Identifier get(CommandContext<ServerCommandSource> context, String arg) throws CommandSyntaxException {
        String name = context.getArgument(arg, String.class);
        Identifier id = null;
        for (String str : CmdInit.PATTERN.keySet()) {
            if (str.matches(name)) {
                id = new Identifier(str);
            }
        }

        if (id == null) {
            throw INVALID_EXCEPTION.create(name);
        } else return id;
    }

    @Override
    public ArrayList<String> getArray() {
        return new ArrayList<>(CmdInit.PATTERN.keySet());
    }
}
