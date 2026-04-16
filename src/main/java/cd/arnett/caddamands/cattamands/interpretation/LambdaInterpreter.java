package cd.arnett.caddamands.cattamands.interpretation;

import cd.arnett.caddamands.cattamands.cattamand.LiteralCattamand;
import cd.arnett.caddamands.cattamands.interpretation.annotations.Catterpret;
import it.unimi.dsi.fastutil.Pair;

import java.io.Serializable;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class LambdaInterpreter {

    public static Pair<Object, Method> getMethodFromlambda(Serializable lambdaMethod) throws NoSuchMethodException, InvocationTargetException, IllegalAccessException, ClassNotFoundException {
        //get the method data from the lambda
        Method writeReplace = lambdaMethod.getClass().getDeclaredMethod("writeReplace");
        writeReplace.setAccessible(true);
        SerializedLambda lambdaData = (SerializedLambda)writeReplace.invoke(lambdaMethod);

        //get the first argument which is the instance of the class it's being called on, or null if this would be static
        Object callingObj = lambdaData.getCapturedArgCount() > 0 ? lambdaData.getCapturedArg(0) : null;

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
}
