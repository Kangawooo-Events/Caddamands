package cd.arnett.caddamands.cattamands.interpretation;

import cd.arnett.caddamands.cattamands.cattamand.Cattamand;
import cd.arnett.caddamands.cattamands.cattamand.LiteralCattamand;
import cd.arnett.caddamands.cattamands.interpretation.annotations.Catterpret;
import cd.arnett.caddamands.cattamands.interpretation.annotations.Paramatter;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.predicate.ItemStackPredicate;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import net.kyori.adventure.key.Key;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.Style;
import org.bukkit.*;
import org.bukkit.block.BlockState;
import org.bukkit.block.structure.Mirror;
import org.bukkit.block.structure.StructureRotation;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scoreboard.Criteria;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Stream;

public class Catterpreter {

    public interface S0 extends Serializable { void run(); }
    public interface S1<A> extends Serializable { void run(A a); }
    public interface S2<A, B> extends Serializable { void run(A a, B b); }
    public interface S3<A, B, C> extends Serializable { void run(A a, B b, C c); }
    public interface S4<A, B, C, D> extends Serializable { void run(A a, B b, C c, D d); }
    public interface S5<A, B, C, D, E> extends Serializable { void run(A a, B b, C c, D d, E e); }
    public interface S6<A, B, C, D, E, F> extends Serializable { void run(A a, B b, C c, D d, E e, F f); }
    public interface S7<A, B, C, D, E, F, G> extends Serializable { void run(A a, B b, C c, D d, E e, F f, G g); }
    public interface S8<A, B, C, D, E, F, G, H> extends Serializable { void run(A a, B b, C c, D d, E e, F f, G g, H h); }

    public static Map<Class<?>, ArgumentType<?>> mapToArg = Map.ofEntries(
            Map.entry(Boolean.class, BoolArgumentType.bool()),
            Map.entry(boolean.class, BoolArgumentType.bool()),
            Map.entry(Double.class, DoubleArgumentType.doubleArg()),
            Map.entry(double.class, DoubleArgumentType.doubleArg()),
            Map.entry(Float.class, FloatArgumentType.floatArg()),
            Map.entry(float.class, FloatArgumentType.floatArg()),
            Map.entry(Integer.class, IntegerArgumentType.integer()),
            Map.entry(int.class, IntegerArgumentType.integer()),
            Map.entry(Long.class, LongArgumentType.longArg()),
            Map.entry(long.class, LongArgumentType.longArg()),
            Map.entry(String.class, StringArgumentType.string()),
            Map.entry(BlockPosition.class, ArgumentTypes.blockPosition()),
            Map.entry(BlockState.class, ArgumentTypes.blockState()),
            Map.entry(NamedTextColor.class, ArgumentTypes.namedColor()),
            Map.entry(Component.class, ArgumentTypes.component()),
            Map.entry(World.class, ArgumentTypes.world()),
            Map.entry(Player.class, ArgumentTypes.entity()),
            Map.entry(Entity.class, ArgumentTypes.entity()),
            Map.entry(LookAnchor.class, ArgumentTypes.entityAnchor()),
            Map.entry(GameMode.class, ArgumentTypes.gameMode()),
            Map.entry(ItemStackPredicate.class, ArgumentTypes.itemPredicate()),
            Map.entry(ItemStack.class, ArgumentTypes.itemStack()),
            Map.entry(Criteria.class, ArgumentTypes.objectiveCriteria()),
            Map.entry(StructureRotation.class, ArgumentTypes.templateRotation()),
            Map.entry(FinePosition.class, ArgumentTypes.finePosition()),
            Map.entry(Key.class, ArgumentTypes.key()),
            Map.entry(NamespacedKey.class, ArgumentTypes.namespacedKey()),
            Map.entry(Style.class, ArgumentTypes.style()),
            Map.entry(Mirror.class, ArgumentTypes.templateMirror()),
            Map.entry(UUID.class, ArgumentTypes.uuid())
    );

    public static Map<Class<?>, ArgumentType<?>> listMapToArg = Map.ofEntries(
            Map.entry(Player.class, ArgumentTypes.players()),
            Map.entry(Entity.class, ArgumentTypes.entities()),
            Map.entry(PlayerProfile.class, ArgumentTypes.playerProfiles())
    );

    /**
     * Finds all methods annotated as @interpretedCattamand and returns a list of them translated to Cattamand Objects
     * @param obj Class to check
     * @return List of now translated Cattamands
     */
    public static Cattamand fromClass(Class<?> classType, Object obj)
    {
        ArrayList<LiteralCattamand> interpreted = new ArrayList<>();

        //find annotated methods in the class
        for(Method method : classType.getMethods())
        {

            if(method.isAnnotationPresent(Catterpret.class)) {

                LiteralCattamand command;

                //do we pass an object (is it static?)
                //thb I don't think this part actually matters
                if(Modifier.isStatic(method.getModifiers()))
                {
                    //this is static so doesn't need an object
                    command = fromMethodStatic(method);
                }
                else if(obj != null)
                {
                    //this is NOT static and does need an object
                    command = fromMethod(obj, method);
                }
                else
                {
                    //this is not static and does Need an object but doesn't have one so skip it
                    continue;
                }


                int altId = 1;


                //has this function already been interpreted
                for (Cattamand cat : interpreted) {
                    if (cat.getName().equals(command.getName())) {
                        altId++;
                    }
                }

                //change the name if it has since we don't want overlap with commands
                if (altId != 1)
                    command.setName(command.getName() + altId);

                //command ready add it to the list
                interpreted.add(command);
            }
        }

        String rootName = classType.getName();

        //check the class for a catterpret
        if(classType.isAnnotationPresent(Catterpret.class))
        {
            //it does have one
            Catterpret classInfo = classType.getAnnotation(Catterpret.class);

            //assemble the command from the class annotation
            return new LiteralCattamand(classInfo.value(), classInfo.permission(), interpreted)
                    .setAliases(Arrays.stream(classInfo.aliases()).toList());
        }

        return new LiteralCattamand(rootName, interpreted);
    }

    public static Cattamand fromClass(Class<?> classType)
    {
        return fromClass(classType, null);
    }

    public static Cattamand fromClass(Object obj)
    {
        return fromClass(obj.getClass(), obj);
    }


    public static ArgumentType<?> convertToArgumentType(Type type) throws IllegalArgumentException
    {
        //is this a generic type (like list<...>)
        if(type instanceof ParameterizedType parameterizedType)
        {
            Type[] insideArgs = parameterizedType.getActualTypeArguments();

            if(parameterizedType.getRawType().equals(List.class)
                && insideArgs.length > 0)
            {
                return listMapToArg.getOrDefault(insideArgs[0], e -> {throw new IllegalArgumentException("Illegal Parameter Type in cattamand method: " + type.toString() + " of " + Arrays.toString(insideArgs));});
            }
            throw new IllegalArgumentException("Illegal Parameter Type in cattamand method: " + type.toString() + " of " + Arrays.toString(insideArgs));
        }
        else
        {
            //this is a regular type
            return mapToArg.getOrDefault(type, e -> {throw new IllegalArgumentException("Illegal Parameter Type in cattamand method: " + type.toString());});
        }
    }

    public static Map.Entry<String, String> convertToSuggestion(String str)
    {
        String[] split = str.split("\\|", 2);

        return Map.entry(split[0], split[1]);
    }

    public static LiteralCattamand fromMethodStatic(Method method)
    {
        if(Modifier.isStatic(method.getModifiers()))
            return fromMethod(null, method);

        throw new IllegalArgumentException("Method provided " + method.getName() + " is NOT static");
    }

    public static LiteralCattamand fromMethod(Object instance, Method method)
    {
        Catterpret catterpret = method.getAnnotation(Catterpret.class);

        //get the arguments
        Type[] argTypes = method.getGenericParameterTypes();
        //get their parameter info
        java.lang.reflect.Parameter[] parameters = method.getParameters();

        //using boolean here so that it is final in the lambda for execution
        boolean hasLeadingCtx = argTypes.length > 0 &&
                argTypes[0] instanceof ParameterizedType part && part.getRawType().equals(CommandContext.class);

        for(Type type : argTypes)
        {
            System.out.println(type.getTypeName());
        }

        //if it has a leading ctx then chop that off
        if(hasLeadingCtx)
        {
            System.out.println("leading ctx");
            argTypes = Arrays.stream(argTypes).skip(1).toArray(Type[]::new);
            parameters = Arrays.stream(parameters).skip(1).toArray(Parameter[]::new);
        }

        InterperetedCattarameter[] args = new InterperetedCattarameter[argTypes.length];


        //convert from types to argumentTypes
        for(int i = 0; i < args.length; i++)
        {

            Paramatter parameter = parameters[i].getAnnotation(Paramatter.class);

            Map<String, String> suggestions = new HashMap<>();

            if(parameter != null)
            {
                //split suggestions
                for(String str : parameter.suggestions())
                {
                    var suggestion = convertToSuggestion(str);
                    suggestions.put(suggestion.getKey(), suggestion.getValue());
                }

                args[i] = new InterperetedCattarameter(
                        parameter == null ? parameters[i].getName() : parameter.value(),
                        convertToArgumentType(argTypes[i]),
                        suggestions,
                        parameter.defaultSuggestions()
                ).setSingle(!(argTypes[i] instanceof ParameterizedType));
            }
            else
            {
                //no parameter tag provided so we're going in blind
                args[i] = new InterperetedCattarameter(
                        parameters[i].getName(),
                        convertToArgumentType(argTypes[i])
                ).setSingle(!(argTypes[i] instanceof ParameterizedType));
            }
        }

        Command<CommandSourceStack> execute = ctx -> {
            try {
                Object[] resolvedArgs;

                if(hasLeadingCtx)
                {
                    resolvedArgs = Stream.concat(Stream.of(ctx), Arrays.stream(args).map(arg -> UltResolve.resolve(ctx, arg))).toArray();
                }
                else {
                    resolvedArgs = Arrays.stream(args).map(arg -> {return UltResolve.resolve(ctx, arg);}).toArray();
                }

                System.out.println(Arrays.toString(Arrays.stream(resolvedArgs).map(Object::getClass).map(Class::toString).toArray()));
                System.out.println(Arrays.toString(Arrays.stream(method.getParameters()).map(Parameter::toString).toArray()));

                method.invoke(instance, resolvedArgs);

                return 1;
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        };

        String name = catterpret.value();
        String permission = catterpret.permission();

        return new LiteralCattamand.Builder(name).permission(permission).argument(
                List.of(args)
        ).aliases(
                List.of(catterpret.aliases())
        ).executes(
                execute
        ).build();
    }

    public static LiteralCattamand fromMethod(S0 s) { return fromMethodBase(s); }
    public static <A> LiteralCattamand fromMethod(S1<A> s) { return fromMethodBase(s); }
    public static <A, B> LiteralCattamand fromMethod(S2<A, B> s) { return fromMethodBase(s); }
    public static <A, B, C> LiteralCattamand fromMethod(S3<A, B, C> s) { return fromMethodBase(s); }
    public static <A, B, C, D> LiteralCattamand fromMethod(S4<A, B, C, D> s) { return fromMethodBase(s); }
    public static <A, B, C, D, E> LiteralCattamand fromMethod(S5<A, B, C, D, E> s) { return fromMethodBase(s); }
    public static <A, B, C, D, E, F> LiteralCattamand fromMethod(S6<A, B, C, D, E, F> s) { return fromMethodBase(s); }
    public static <A, B, C, D, E, F, G> LiteralCattamand fromMethod(S7<A, B, C, D, E, F, G> s) { return fromMethodBase(s); }
    public static <A, B, C, D, E, F, G, H> LiteralCattamand fromMethod(S8<A, B, C, D, E, F, G, H> s) { return fromMethodBase(s); }

    private static LiteralCattamand fromMethodBase(Serializable lambda) {
        try {
            var methodData = LambdaInterpreter.getMethodFromlambda(lambda);
            return fromMethod(methodData.left(), methodData.right());
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }
}
