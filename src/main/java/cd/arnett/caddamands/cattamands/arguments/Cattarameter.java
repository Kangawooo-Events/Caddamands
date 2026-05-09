package cd.arnett.caddamands.cattamands.arguments;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandExceptionType;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import javax.swing.plaf.InsetsUIResource;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class Cattarameter
{

    //region Properties

    /*=================================================================================================
                    -  Properties  -
    =================================================================================================*/

    String name;
    ArgumentType<?> type;
    Map<String, String> literalSuggestions;
    Function<CommandContext<CommandSourceStack>, Map<String, String>> lambdaSuggestions;
    boolean doDefaultSuggestions;
    Command<CommandSourceStack> executes;

    //endregion


    //region Constructors

    /*=================================================================================================
                    -  Constructors  -
    =================================================================================================*/

    @FunctionalInterface
    /**
     * to prevent overload clashes with constructors
     */
    public interface GenericSuggestions {
        Object get(CommandContext<CommandSourceStack> ctx);
    };

    public static Cattarameter of(String name, ArgumentType<?> type)
    {
        return of(name, type, List.of(), true);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, String literalSuggestions)
    {
        return of(name, type, List.of(literalSuggestions));
    }

    public static Cattarameter of(String name, ArgumentType<?> type, List<String> literalSuggestions)
    {
        return of(name, type, convertListToMap(literalSuggestions));
    }

    public static Cattarameter of(String name, ArgumentType<?> type, Map<String, String> literalSuggestions)
    {
        return of(name, type, literalSuggestions, null, false);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, GenericSuggestions lambdaSuggestions)
    {
        return of(name, type, Map.of(), lambdaSuggestions, false);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, boolean doDefaultSuggestions)
    {
        return of(name, type, List.of(), doDefaultSuggestions);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, String literalSuggestions, boolean doDefaultSuggestions)
    {
        return of(name, type, List.of(literalSuggestions), doDefaultSuggestions);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, List<String> literalSuggestions, boolean doDefaultSuggestions)
    {
        return of(name, type, convertListToMap(literalSuggestions), doDefaultSuggestions);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, boolean doDefaultSuggestions)
    {
        return of(name, type, literalSuggestions, null, doDefaultSuggestions);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, GenericSuggestions lambdaSuggestions, boolean doDefaultSuggestions)
    {
        return of(name, type, Map.of(), lambdaSuggestions, doDefaultSuggestions);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, GenericSuggestions lambdaSuggestions, boolean doDefaultSuggestions)
    {
        return new Cattarameter(name, type, literalSuggestions, lambdaSuggestions, doDefaultSuggestions, null);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, Command<CommandSourceStack> executes)
    {
        return of(name, type, List.of(), true, executes);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, String literalSuggestions, Command<CommandSourceStack> executes)
    {
        return of(name, type, List.of(literalSuggestions), executes);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, List<String> literalSuggestions, Command<CommandSourceStack> executes)
    {
        return of(name, type, convertListToMap(literalSuggestions), executes);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, Command<CommandSourceStack> executes)
    {
        return of(name, type, literalSuggestions, null, false, executes);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, GenericSuggestions lambdaSuggestions, Command<CommandSourceStack> executes)
    {
        return of(name, type, Map.of(), lambdaSuggestions, false, executes);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, boolean doDefaultSuggestions, Command<CommandSourceStack> executes)
    {
        return of(name, type, List.of(), doDefaultSuggestions, executes);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, String literalSuggestions, boolean doDefaultSuggestions, Command<CommandSourceStack> executes)
    {
        return of(name, type, List.of(literalSuggestions), doDefaultSuggestions, executes);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, List<String> literalSuggestions, boolean doDefaultSuggestions, Command<CommandSourceStack> executes)
    {
        return of(name, type, convertListToMap(literalSuggestions), doDefaultSuggestions, executes);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, boolean doDefaultSuggestions, Command<CommandSourceStack> executes)
    {
        return of(name, type, literalSuggestions, null, doDefaultSuggestions, executes);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, GenericSuggestions lambdaSuggestions, boolean doDefaultSuggestions, Command<CommandSourceStack> executes)
    {
        return of(name, type, Map.of(), lambdaSuggestions, doDefaultSuggestions, executes);
    }

    public static Cattarameter of(String name, ArgumentType<?> type, Map<String, String> literalSuggestions, GenericSuggestions lambdaSuggestions, boolean doDefaultSuggestions, Command<CommandSourceStack> executes)
    {
        return new Cattarameter(name, type, literalSuggestions, lambdaSuggestions, doDefaultSuggestions, executes);
    }

    protected Cattarameter (String name, ArgumentType<?> type, Map<String, String> literalSuggestions, GenericSuggestions lambdaSuggestions, boolean doDefaultSuggestions, Command<CommandSourceStack> executes)
    {
        this.name = name;
        this.type = type;
        this.literalSuggestions = literalSuggestions;
        this.doDefaultSuggestions = doDefaultSuggestions;
        this.executes = executes;

        //differentiate between map or list of strings
        if(lambdaSuggestions != null)
        {
            this.lambdaSuggestions = ctx -> {
                //grab the input generically (so either a List or a Map)
                Object input = lambdaSuggestions.get(ctx);

                if(input instanceof Map<?,?> map)
                {
                    try {
                        return (Map<String,String>)map;
                    }
                    catch (Exception e)
                    {
                        throw new IllegalArgumentException("Invalid Suggestions Provided for Parameter: " + name + " : " + type);
                    }
                }
                else if(input instanceof List<?> list)
                {
                    return convertListToMap(list);
                }
                else
                {
                    return Map.of(input.toString(), "");
                }
            };
        }
        else
        {
            this.lambdaSuggestions = null;
        }
    }

    //endregion


    //region Properties

    /*=================================================================================================
                    -  Properties  -
    =================================================================================================*/


    /**
     * @return Name of the Argument
     */
    public String getName() {
        return name;
    }

    /**
     * @return ArgumentType of the arugment
     */
    public ArgumentType<?> getType() {
        return type;
    }


    /**
     * @return Optional Executing function for this argument
     */
    public Command<CommandSourceStack> getExecutes()
    {
        return executes;
    }

    //endregion


    //region Suggestions

    /*=================================================================================================
                    -  Suggestions  -
    =================================================================================================*/


    public Map<String, String> getSuggestions(CommandContext<CommandSourceStack> ctx)
    {
        //get map of regular suggestions
        Map<String, String> suggestions = new HashMap<>(literalSuggestions);

        if(lambdaSuggestions != null)
            suggestions.putAll(lambdaSuggestions.apply(ctx));

        return suggestions;
    }

    public boolean doDefaultSuggestions()
    {
        return doDefaultSuggestions;
    }

    static HashMap<String, String> convertListToMap(List<?> suggestion)
    {
        HashMap<String, String> suggestionMap = new HashMap<>();

        for(Object s : suggestion)
        {
            suggestionMap.put(s.toString(), "");
        }

        return suggestionMap;
    }

    //endregion

}
