package cd.arnett.caddamands.cattamands.interpretation;

import cd.arnett.caddamands.cattamands.arguments.Cattarameter;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class InterperetedCattarameter extends Cattarameter
{
    //used specifically for telling if the user wants List<Player> or just Player because ArgumentType.player(s) both use the same class
    public boolean isSingle = true;

    protected InterperetedCattarameter(String name, ArgumentType<?> type) {
        this(name, type, Map.of());
    }

    protected InterperetedCattarameter(String name, ArgumentType<?> type, Map<String, String> literalSuggestions) {
        this(name, type, literalSuggestions, null, null);
    }

    protected InterperetedCattarameter(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, GenericSuggestions lambdaSuggestions) {
        this(name, type, literalSuggestions, lambdaSuggestions, false);
    }

    protected InterperetedCattarameter(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, boolean doDefaultSuggestions) {
        this(name, type, literalSuggestions, null, doDefaultSuggestions);
    }

    protected InterperetedCattarameter(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, GenericSuggestions lambdaSuggestions, boolean doDefaultSuggestions) {
        super(name, type, literalSuggestions, lambdaSuggestions, doDefaultSuggestions, null);
    }

    protected InterperetedCattarameter(String name, ArgumentType<?> type, Command<CommandSourceStack> executes) {
        this(name, type, Map.of(), executes);
    }

    protected InterperetedCattarameter(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, Command<CommandSourceStack> executes) {
        this(name, type, literalSuggestions, null, executes);
    }

    protected InterperetedCattarameter(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, GenericSuggestions lambdaSuggestions, Command<CommandSourceStack> executes) {
        this(name, type, literalSuggestions, lambdaSuggestions, false, executes);
    }

    protected InterperetedCattarameter(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, boolean doDefaultSuggestions, Command<CommandSourceStack> executes) {
        this(name, type, literalSuggestions, null, doDefaultSuggestions, executes);
    }

    protected InterperetedCattarameter(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, GenericSuggestions lambdaSuggestions, boolean doDefaultSuggestions, Command<CommandSourceStack> executes) {
        super(name, type, literalSuggestions, lambdaSuggestions, doDefaultSuggestions, executes);
    }

    public InterperetedCattarameter setSingle(boolean single)
    {
        isSingle = single;
        return this;
    }
}
