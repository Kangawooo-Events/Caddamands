package cd.arnett.caddamands.cattamands.interpretation.annotations;


import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a parameter as a Cattamand Argument to store data like suggestions for the interpreter
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.PARAMETER})
public @interface Paramatter {

    /**
     * The name which the Parameter uses in the command line. <br><br>
     *
     * By default, this take the name of the parameter HOWEVER,
     * if the jar is not compiled with the parameters flag, the names will be lost, ADDITIONALLY
     * if the code gets obfuscated then the command name will also get messed up.
     * So, I recommend tagging parameters with this regardless.
     */
    String value () default "";

    /**
     * Array of strings to be used as Suggestions for the parameter<br></br>
     *
     * To have hover text, add a vertical line ( like this -> | )
     * and the right of the line will be taken as hovertext for that suggestion
     */
    String[] suggestions () default {};


    /**
     * Whether to show the default argument suggestions of the correlated argumentType
     */
    boolean defaultSuggestions () default true;
}
