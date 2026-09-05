package cd.arnett.caddamands.cattamands.arguments;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.ArgumentType;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

    public Cattarameter (String name, ArgumentType<?> type)
    {
        this(name, type, List.of());
    }

    public Cattarameter (String name, ArgumentType<?> type, List<String> literalSuggestions)
    {
        this(name, type, convertListToMap(literalSuggestions));
    }

    public Cattarameter (String name, ArgumentType<?> type, Map<String, String> literalSuggestions)
    {
        this(name, type, literalSuggestions, null, null, true);
    }

    public Cattarameter (String name, ArgumentType<?> type, GenericSuggestions lambdaSuggestions)
    {
        this(name, type, lambdaSuggestions,  null);
    }

    public Cattarameter (String name, ArgumentType<?> type, GenericSuggestions lambdaSuggestions, Command<CommandSourceStack> executes)
    {
        this(name, type, Map.of(), lambdaSuggestions,executes,  true);
    }

    public Cattarameter (String name, ArgumentType<?> type,GenericSuggestions lambdaSuggestions,Command<CommandSourceStack> executes, boolean doDefaultSuggestions)
    {
        this(name, type, Map.of(), lambdaSuggestions, executes, doDefaultSuggestions);
    }

    public Cattarameter (String name, ArgumentType<?> type, Map<String, String> literalSuggestions, GenericSuggestions lambdaSuggestions, Command<CommandSourceStack> executes, boolean doDefaultSuggestions)
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


    /**
     * Option to set this argument to hide the default minecraft suggestions for the arguments type<br>
     * i.e. showing player names when doing a player argument, Defaults to TRUE
     */
    public void doDefaultSuggestions(boolean doDefaultSuggestions)
    {
        this.doDefaultSuggestions = doDefaultSuggestions;
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
