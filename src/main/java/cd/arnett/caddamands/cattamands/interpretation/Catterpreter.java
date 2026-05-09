package cd.arnett.caddamands.cattamands.interpretation;

import cd.arnett.caddamands.cattamands.cattamand.Cattamand;
import cd.arnett.caddamands.cattamands.cattamand.LiteralCattamand;
import cd.arnett.caddamands.cattamands.interpretation.annotations.Catterpret;
import cd.arnett.caddamands.cattamands.interpretation.annotations.Paramatter;
import com.destroystokyo.paper.profile.PlayerProfile;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.arguments.*;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.papermc.paper.command.brigadier.CommandSourceStack;
import io.papermc.paper.command.brigadier.argument.ArgumentTypes;
import io.papermc.paper.command.brigadier.argument.predicate.ItemStackPredicate;
import io.papermc.paper.command.brigadier.argument.resolvers.ArgumentResolver;
import io.papermc.paper.entity.LookAnchor;
import io.papermc.paper.math.BlockPosition;
import io.papermc.paper.math.FinePosition;
import it.unimi.dsi.fastutil.Pair;
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
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class Catterpreter {

    //region From Class

    /*=================================================================================================
                    -  From Class  -
    =================================================================================================*/

    /**
     * Finds all methods annotated as @Catterpret and returns a single root Cattamand based on the class
     * (see @Catterpret/@Paramatter) with child Cattamands for each method
     * @param classType Class to convert
     * @param obj Reference to the object commands call methods from (if NULL will only include the static methods as children)
     * @return Now translated Cattamand from class, permissions default to 'op'
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


    /**
     * Finds all STATIC methods annotated as @Catterpret and returns a single root Cattamand based on the class
     * (see @Catterpret/@Paramatter) with child Cattamands for each STATIC method
     * @param classType Class to convert
     * @return Now translated Cattamand from class with only STATIC methods, permissions default to 'op'
     */
    public static Cattamand fromClass(Class<?> classType)
    {
        return fromClass(classType, null);
    }


    /**
     * Finds all methods annotated as @Catterpret and returns a single root Cattamand based on the class
     * (see @Catterpret/@Paramatter) with child Cattamands for each method
     * @param obj Reference to the object commands call methods from (if NULL will only include the static methods as children)
     * @return Now translated Cattamand from class, permissions default to 'op'
     */
    public static Cattamand fromClass(Object obj)
    {
        return fromClass(obj.getClass(), obj);
    }

    //endregion


    //region From Methods

    /*=================================================================================================
                    -  From Methods  -
    =================================================================================================*/

    /**
     * Converts a Static Method into a Literal Cattamand, using any annotation info provided (see @Catterpret/@Paramatter)
     * @param method The method to convert
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
    public static LiteralCattamand fromMethodStatic(Method method)
    {
        if(Modifier.isStatic(method.getModifiers()))
            return fromMethod(null, method);

        throw new IllegalArgumentException("Method provided " + method.getName() + " is NOT static");
    }


    /**
     * Converts a Method into a Literal Cattamand, using any annotation info provided (see @Catterpret/@Paramatter)
     * @param instance Instance of the object command calls methods from
     *                (if this is for a static method this can be NULL, or use #fromMethodStatic(method))
     * @param method Method to be called from the instance
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
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
                    resolvedArgs = Stream.concat(Stream.of(ctx), Arrays.stream(args).map(arg -> resolve(ctx, arg))).toArray();
                }
                else {
                    resolvedArgs = Arrays.stream(args).map(arg -> {return resolve(ctx, arg);}).toArray();
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

    //endregion


    //region lambda Interpreter

    /*=================================================================================================
                    -  lambda Interpreter  -
    =================================================================================================*/

    /** interfaces for Lambda References, allows simple pass like "object:method" for 0 parameters */
    public interface S0 extends Serializable { void run(); }
    /** interfaces for Lambda References, allows simple pass like "object:method" for 1 parameter */
    public interface S1<A> extends Serializable { void run(A a); }
    /** interfaces for Lambda References, allows simple pass like "object:method" for 2 parameters */
    public interface S2<A, B> extends Serializable { void run(A a, B b); }
    /** interfaces for Lambda References, allows simple pass like "object:method" for 3 parameters */
    public interface S3<A, B, C> extends Serializable { void run(A a, B b, C c); }
    /** interfaces for Lambda References, allows simple pass like "object:method" for 4 parameters */
    public interface S4<A, B, C, D> extends Serializable { void run(A a, B b, C c, D d); }
    /** interfaces for Lambda References, allows simple pass like "object:method" for 5 parameters */
    public interface S5<A, B, C, D, E> extends Serializable { void run(A a, B b, C c, D d, E e); }
    /** interfaces for Lambda References, allows simple pass like "object:method" for 6 parameters */
    public interface S6<A, B, C, D, E, F> extends Serializable { void run(A a, B b, C c, D d, E e, F f); }
    /** interfaces for Lambda References, allows simple pass like "object:method" for 7 parameters */
    public interface S7<A, B, C, D, E, F, G> extends Serializable { void run(A a, B b, C c, D d, E e, F f, G g); }
    /** interfaces for Lambda References, allows simple pass like "object:method" for 8 parameters */
    public interface S8<A, B, C, D, E, F, G, H> extends Serializable { void run(A a, B b, C c, D d, E e, F f, G g, H h); }


    /**
     * Converts a Method into a Literal Cattamand, using any annotation info provided (see @Catterpret/@Paramatter)
     * @param s SerializableLambda referencing method with (0 parameters)
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
    private static LiteralCattamand fromMethod(S0 s) { return fromMethodBase(s); }
    /**
     * Converts a Method into a Literal Cattamand, using any annotation info provided (see @Catterpret/@Paramatter)
     * @param s SerializableLambda referencing method with (1 parameter)
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
    private static <A> LiteralCattamand fromMethod(S1<A> s) { return fromMethodBase(s); }
    /**
     * Converts a Method into a Literal Cattamand, using any annotation info provided (see @Catterpret/@Paramatter)
     * @param s SerializableLambda referencing method with (2 parameters)
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
    private static <A, B> LiteralCattamand fromMethod(S2<A, B> s) { return fromMethodBase(s); }
    /**
     * Converts a Method into a Literal Cattamand, using any annotation info provided (see @Catterpret/@Paramatter)
     * @param s SerializableLambda referencing method with (3 parameters)
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
    private static <A, B, C> LiteralCattamand fromMethod(S3<A, B, C> s) { return fromMethodBase(s); }
    /**
     * Converts a Method into a Literal Cattamand, using any annotation info provided (see @Catterpret/@Paramatter)
     * @param s SerializableLambda referencing method with (4 parameters)
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
    private static <A, B, C, D> LiteralCattamand fromMethod(S4<A, B, C, D> s) { return fromMethodBase(s); }
    /**
     * Converts a Method into a Literal Cattamand, using any annotation info provided (see @Catterpret/@Paramatter)
     * @param s SerializableLambda referencing method with (5 parameters)
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
    private static <A, B, C, D, E> LiteralCattamand fromMethod(S5<A, B, C, D, E> s) { return fromMethodBase(s); }
    /**
     * Converts a Method into a Literal Cattamand, using any annotation info provided (see @Catterpret/@Paramatter)
     * @param s SerializableLambda referencing method with (6 parameters)
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
    private static <A, B, C, D, E, F> LiteralCattamand fromMethod(S6<A, B, C, D, E, F> s) { return fromMethodBase(s); }
    /**
     * Converts a Method into a Literal Cattamand, using any annotation info provided (see @Catterpret/@Paramatter)
     * @param s SerializableLambda referencing method with (7 parameters)
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
    private static <A, B, C, D, E, F, G> LiteralCattamand fromMethod(S7<A, B, C, D, E, F, G> s) { return fromMethodBase(s); }
    /**
     * Converts a Method into a Literal Cattamand, using any annotation info provided (see @Catterpret/@Paramatter)
     * @param s SerializableLambda referencing method with (8 parameters)
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
    private static <A, B, C, D, E, F, G, H> LiteralCattamand fromMethod(S8<A, B, C, D, E, F, G, H> s) { return fromMethodBase(s); }

    /**
     * The base function for converting A lambda refrence to a Cattamand
     * @param lambda The now Serializable lambda from the fromMethods which were used to allow the user to easily pass like regular lambda
     * @return The completed Cattamand, permissions default to 'op' and name to the name of the method
     */
    private static LiteralCattamand fromMethodBase(Serializable lambda) {
        try {
            var methodData = getMethodFromlambda(lambda);
            return fromMethod(methodData.left(), methodData.right());
        }
        catch (Exception e)
        {
            System.err.println(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    /**
     * Returns a pair of Object and Method from a lambda reference
     * @param lambdaMethod
     * @return Pair of Object + Method, object instance of class (null if static), and Method being refrenced by lambda
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalAccessException
     * @throws ClassNotFoundException
     */
    public static Pair<Object, Method> getMethodFromlambda(Serializable lambdaMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        //get the method data from the lambda
        Method writeReplace = lambdaMethod.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        SerializedLambda lambdaData = (SerializedLambda)writeReplace.invoke(lambdaMethod);

        //get the first argument which is the instance of the class it's being called on, or null if this would be static
        Object callingObj = lambdaData.getImplMethodKind() != 6 ? lambdaData.getCapturedArg(0) : null;

        //find the method being called

        //get the class to search though ( / -> . because of how the name is stored)
        String className = lambdaData.getImplClass().replace('/', '.');
        String methodName = lambdaData.getImplMethodName();
        Class<?> clazz = Class.forName(className);

        //if there is an interpret tagged on one of these, save it since it's gonna be our best option
        AtomicReference<Method> withInterpret = new AtomicReference<>();

        List<Method> possibleMethods = Arrays.stream(clazz.getMethods()).filter(method -> {
            //general name check for the function, overloads kinda do us in here
            if(method.getName().equals(methodName))
            {
                if(method.isAnnotationPresent(Catterpret.class))
                    withInterpret.set(method);
                return true;
            }
            return false;
        }).toList();

        //filter out to one if we have more to pick from
        if(withInterpret.get() != null)
        {
            return Pair.of(callingObj, withInterpret.get());
        }
        else if(!possibleMethods.isEmpty())
        {
            return Pair.of(callingObj, possibleMethods.getLast());
        }
        else
        {
            throw new NoSuchMethodException("Unable to find method " + methodName);
        }
    }

    //endregion


    //region Converters

    /*=================================================================================================
                    -  Converters  -
    =================================================================================================*/

    //converter map
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

    //converter map for lists
    public static Map<Class<?>, ArgumentType<?>> listMapToArg = Map.ofEntries(
            Map.entry(Player.class, ArgumentTypes.players()),
            Map.entry(Entity.class, ArgumentTypes.entities()),
            Map.entry(PlayerProfile.class, ArgumentTypes.playerProfiles())
    );

    /**
     * Converts the String from the Paramatter to a Cattamand Suggestion (by the format "Suggestion|Hovertext")
     * @param string The string to convert
     * @return A Map entry splitting the String by the first '|'
     */
    public static Map.Entry<String, String> convertToSuggestion(String string)
    {
        String[] split = string.split("\\|", 2);

        return Map.entry(split[0], split[1]);
    }

    /**
     * Converts the passed type to an Argument type, throws IllegalArgumentException if no ArgumentType is found
     * @param type The type to convert
     * @return The converted ArgumentType
     */
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

    //endregion


    //region Resolver

    /*=================================================================================================
                    -  Resolver  -
    =================================================================================================*/

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

    //endregion


}
