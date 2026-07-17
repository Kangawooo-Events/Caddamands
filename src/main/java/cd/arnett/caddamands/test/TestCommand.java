package cd.arnett.caddamands.test;

import cd.arnett.caddamands.cattamands.arguments.Cattarameter;
import cd.arnett.caddamands.cattamands.cattamand.Cattamand;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;
import java.util.Map;

public class TestCommand extends Cattamand {
    @Override
    public int execute(CommandContext<CommandSourceStack> context) throws CommandSyntaxException {
        return 0;
    }

    @Override
    public List<? extends Cattarameter> getArguments() {
        return List.of(
                Cattarameter.of(
                        "name",
                        IntegerArgumentType.integer(),
                        List.of(),
                        (ctx) -> {
                            return 1;
                        }
                )
        );
    }

    @Override
    public String getName() {
        return "";
    }

    @Override
    public String getPermission() {
        return "";
    }

    @Override
    public List<String> getAliases() {
        return List.of();
    }
}
