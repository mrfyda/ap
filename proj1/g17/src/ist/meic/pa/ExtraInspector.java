package ist.meic.pa;

import ist.meic.pa.shell.Shell;
import ist.meic.pa.shell.command.TerminateInspectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.EmptyStackException;
import java.util.Stack;

public class ExtraInspector extends Inspector {

    private static Stack<Object> history = new Stack<Object>();

    public void push(Object object) throws TerminateInspectionException {
        history.push(object);
        throw new TerminateInspectionException();
    }

    public void pop() {
        try {
            Object object = history.pop();
            inspect(object);
        } catch (EmptyStackException e) {
            System.err.println("Stash is empty");
        }
    }

    public Object getInstance(Shell shell, String className, String[] consArgs) {
        Object instance = null;

        try {
            Class<?> commandClass = Class.forName(className);

            Class[] consArgTypes = parseArgTypes(shell, consArgs);
            Constructor<?> commandConstructor = commandClass.getConstructor(consArgTypes);

            Object[] consArgValues = parseArgVals(shell, consArgs);
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

    public void invokeTypedMethod(Shell shell, Object object, String methodName, String[] methodArgs) {
        Class[] argTypes = parseArgTypes(shell, methodArgs);
        Object[] argValues = parseArgVals(shell, methodArgs);

        invokeByName(object, methodName, argTypes, argValues);
    }

    private void invokeByName(Object object, String methodName, Class<?>[] argTypes, Object[] argValues) {
        assert (argTypes.length == argValues.length);

        try {
            Method method = object.getClass().getDeclaredMethod(methodName, argTypes);
            method.setAccessible(true);

            String output = method.invoke(object, argValues).toString();

            System.err.println(output);
        } catch (NoSuchMethodException e) {
            System.err.println("Unknown method: " + methodName);
        } catch (NumberFormatException e) {
            System.err.println("Parameters must be integers!!!");
        } catch (Exception e) {
            System.err.println("This is weird, how did you get here?");
            e.printStackTrace();
        }
    }

    private Class<?>[] parseArgTypes(Shell shell, String[] methodArgs) {
        Integer argsNumber = methodArgs.length;
        Class[] argTypes = new Class[argsNumber];

        try {
            for (int i = 0; i < argTypes.length; i++) {
                String[] argInfo = methodArgs[i].split(":");
                String argType = argInfo[0];
                String argValue = argInfo[1];

                argTypes[i] = getTypeByName(shell, argType, argValue);
            }
        } catch (ClassNotFoundException e) {
            System.err.println("Unknown parameter type: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("This is weird, how did you get here?");
            e.printStackTrace();
        }

        return argTypes;
    }

    private Object[] parseArgVals(Shell shell, String[] methodArgs) {
        Integer argsNumber = methodArgs.length;

        Object[] argValues = new Object[argsNumber];
        for (int i = 0; i < argValues.length; i++) {
            String[] argInfo = methodArgs[i].split(":");
            String argType = argInfo[0];
            String argValue = argInfo[1];

            if (argType.contains("Integer") || argType.contains("int")) {
                argValues[i] = Integer.parseInt(argValue);
            } else if (argType.contains("Float")) {
                argValues[i] = Float.parseFloat(argValue);
            } else if (argType.contains("Double")) {
                argValues[i] = Double.parseDouble(argValue);
            } else if (argType.equals("type")) {
                argValues[i] = shell.getObject(argValue);
            } else {
                argValues[i] = argValue;
            }
        }

        return argValues;
    }

    private Class<?> getTypeByName(Shell shell, String argType, String argValue) throws ClassNotFoundException {
        Class<?> argClass = null;

        try {
            argClass = Class.forName("java.lang." + argType);
        } catch (ClassNotFoundException ex1) {
            try {
                argClass = Class.forName(argType);
            } catch (ClassNotFoundException ex2) {
                argClass = shell.getObject(argValue).getClass();
            }
        }

        if (argClass == null) {
            throw new ClassNotFoundException(argType);
        }

        return argClass;
    }
}
