package cd.arnett.caddamands.cattamands.interpretation;

import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.SignedMessageResolver;
import io.papermc.paper.command.brigadier.argument.predicate.ItemStackPredicate;
import io.papermc.paper.command.brigadier.argument.resolvers.ArgumentResolver;
import io.papermc.paper.command.brigadier.argument.resolvers.BlockPositionResolver;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

public class UltResolve {

    /**
     * Resolve the InterperetedCattarameter into an Object of it's class type (e.g. StringArgumentType -> String)
     * @param ctx Command Context
     * @param arg Argument to resolve
     * @return Object of Resolved class
     */
    public static Object resolve(CommandContext<CommandSourceStack> ctx, InterperetedCattarameter arg)
    {
        if(!(arg.getType().getClass().getGenericInterfaces()[0] instanceof ParameterizedType paramType))
            throw new RuntimeException("Invalid type of argument: " + arg.getType().getClass());

        Type innerArg = paramType.getActualTypeArguments()[0];
        BiFunction<CommandContext<CommandSourceStack>, String, Object> lambda;

        //generic
        lambda = (context, name) -> {
            Object result = context.getArgument(name, Object.class);

            //if this is a resolver be sure to resolve it first
            if(result instanceof ArgumentResolver<?> resolver)
            {
                try {
                    return resolver.resolve(context.getSource());
                } catch (CommandSyntaxException e) {
                    throw new RuntimeException(e);
                }
            }

            return result;
        };

        if(lambda == null)
        {
            throw new RuntimeException("Invalid type of argument: " + arg.getType().getClass() + " - " + innerArg);
        }

        Object result = lambda.apply(ctx, arg.getName());

        if(arg.isSingle && result instanceof List<?> list)
            result = list.getFirst();

        return result;
    }
}
