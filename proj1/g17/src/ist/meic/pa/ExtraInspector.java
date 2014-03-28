package ist.meic.pa;

import ist.meic.pa.shell.Shell;
import ist.meic.pa.shell.command.TerminateInspectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.EmptyStackException;
import java.util.Stack;

public class ExtraInspector extends Inspector {

    private static final ArgumentHelper argHelper = new ArgumentHelper();

    private static Stack<Object> history = new Stack<Object>();

    public void pushHistory(Object object) throws TerminateInspectionException {
        try {
            if (history.peek().equals(object)) {
                return;
            }
        } catch (EmptyStackException ignored) {
        }

        history.push(object);
        throw new TerminateInspectionException();
    }

    public void popHistory() {
        try {
            Object object = history.pop();
            inspect(object);
        } catch (EmptyStackException e) {
            System.err.println("Stash is empty");
        }
    }

    public void printHistoryStatus() {
        String indentation = "";
        for (Object object : history) {
            System.err.printf("%s%s %n", indentation, object);
            indentation += " ";
        }
    }

    public Object getInstance(Shell shell, String className, String[] consArgs) {
        Object instance = null;

        Integer argNumber = consArgs.length;
        Class<?>[] consArgTypes = new Class<?>[argNumber];
        Object[] consArgValues = new Object[argNumber];
        argHelper.prepareArgs(shell, consArgs, consArgTypes, consArgValues);

        try {
            Class<?> commandClass = Class.forName(className);

            Constructor<?> commandConstructor = commandClass.getConstructor(consArgTypes);

            instance = commandConstructor.newInstance(consArgValues);
        } catch (ClassNotFoundException e) {
            System.err.printf("Class '%s' not found %n", className);
        } catch (NoSuchMethodException e) {
            System.err.printf("Constructor for class '%s' not found %n", className);
        } catch (Exception e) {
            System.err.println("This is weird, how did you get here?");
            e.printStackTrace();
        }

        return instance;
    }

    private Object invokeByName(Object object, String methodName, Class<?>[] argTypes, Object[] argValues) {
        assert (argTypes.length == argValues.length);

        try {
            System.err.println("method name: " + methodName);
            Method method = object.getClass().getDeclaredMethod(methodName, argTypes);
            System.err.println("success!");
            method.setAccessible(true);

            if (Modifier.isStatic(method.getModifiers())) {
                throw new NoSuchMethodException();
            }

            Object output = method.invoke(object, argValues);

            if (output != null) {
                System.err.println(output);
            }

            return output;
        } catch (NoSuchMethodException e) {
            System.err.println("Unknown method: " + methodName);
        } catch (NumberFormatException e) {
            System.err.println("Parameters must be integers!");
        } catch (Exception e) {
            System.err.println("This is weird, how did you get here?");
            e.printStackTrace();
        }

        return null;
    }

    public void invokeTypedMethod(Shell shell, Object object, String methodName, String[] methodArgs) {
        Integer argNumber = methodArgs.length;
        Class<?>[] argTypes = new Class<?>[argNumber];
        Object[] argValues = new Object[argNumber];
        argHelper.prepareArgs(shell, methodArgs, argTypes, argValues);

        Object result = invokeByName(object, methodName, argTypes, argValues);

        if(result != null) {
            shell.setLatestObject(result);
        }
    }

    public void modifyTypedField(Shell shell, String fieldName, String fieldValue, Object object) {
        try {
            Field field = getAllFields(object).get(fieldName);

            Object argVal = argHelper.parseArgVal(shell, field.getType().getName(), fieldValue);

            field.set(object, argVal);

            printClassInfo(object);
        } catch (NullPointerException e) {
            System.err.printf("Field '%s' not found %n", fieldName);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Missing parameter info. usage> type:value");
        } catch (IllegalArgumentException e) {
            System.err.printf("Field '%s' is not the same type of '%s' %n", fieldValue, fieldName);
        } catch (Exception e) {
            System.err.printf("Operation error with Field '%s' and value '%s' %n", fieldName, fieldValue);
        }
    }
}
