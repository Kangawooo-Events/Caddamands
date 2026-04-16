package cd.arnett.caddamands.cattamands.interpretation.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method as a Cattamand which can be translated using Catterpreter.interpret([class instance])
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.TYPE})
public @interface Catterpret {

    /**
     * Sender requires this string permission to run the command
     */
    String permission () default "";

    /**
     * The name which the Cattamand uses in the command line. <br>
     *
     * By default, this take the name of the function HOWEVER, if the code gets obfuscated then the command name will also get messed up.
     * So, I recommend filling out the value regardless.
     */
    String value () default "";

    /**
     * Array of strings to be used as aliases for the command
     */
    String[] aliases () default {};
}
