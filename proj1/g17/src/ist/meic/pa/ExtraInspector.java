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

        Integer argNumber = consArgs.length;
        Class<?>[] consArgTypes = new Class<?>[argNumber];
        Object[] consArgValues = new Object[argNumber];
        prepareArgs(shell, consArgs, consArgTypes, consArgValues);

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

    private void invokeByName(Object object, String methodName, Class<?>[] argTypes, Object[] argValues) {
        assert (argTypes.length == argValues.length);

        try {
            Method method = object.getClass().getDeclaredMethod(methodName, argTypes);
            method.setAccessible(true);

            Object output = method.invoke(object, argValues);

            if (output != null) {
                System.err.println(output);
            }
        } catch (NoSuchMethodException e) {
            System.err.println("Unknown method: " + methodName);
        } catch (NumberFormatException e) {
            System.err.println("Parameters must be integers!");
        } catch (Exception e) {
            System.err.println("This is weird, how did you get here?");
            e.printStackTrace();
        }
    }

    public void invokeTypedMethod(Shell shell, Object object, String methodName, String[] methodArgs) {
        Integer argNumber = methodArgs.length;
        Class<?>[] argTypes = new Class<?>[argNumber];
        Object[] argValues = new Object[argNumber];
        prepareArgs(shell, methodArgs, argTypes, argValues);

        invokeByName(object, methodName, argTypes, argValues);
    }

    private void prepareArgs(Shell shell, String[] args, Class<?>[] types, Object[] values) {
        try {
            for (int i = 0; i < args.length; i++) {
                String[] argInfo = args[i].split(":");
                String argType = argInfo[0];
                String argValue = argInfo[1];

                types[i] = parseArgType(shell, argType, argValue);
                values[i] = parseArgVal(shell, argType, argValue);
            }
        } catch (ArrayIndexOutOfBoundsException e) {
            System.err.println("Missing parameter info. usage> type:value");
        }
    }

    private Class<?> parseArgType(Shell shell, String strType, String strValue) {
        Class argType = null;

        try {
            argType = getTypeByName(shell, strType, strValue);
        } catch (ClassNotFoundException e) {
            System.err.println("Unknown parameter type: " + e.getMessage());
        }

        return argType;
    }

    private Object parseArgVal(Shell shell, String strType, String strValue) {
        Object argValue = null;

        if (strType.contains("Integer")) {
            argValue = Integer.parseInt(strValue);
        } else if (strType.contains("String")) {
            argValue = strValue;
        } else if (strType.contains("Long")) {
            argValue = Long.parseLong(strValue);
        } else if (strType.contains("Short")) {
            argValue = Short.parseShort(strValue);
        } else if (strType.contains("Boolean")) {
            argValue = Boolean.parseBoolean(strValue);
        } else if (strType.contains("Float")) {
            argValue = Float.parseFloat(strValue);
        } else if (strType.contains("Double")) {
            argValue = Double.parseDouble(strValue);
        } else if (strType.contains("Character")) {
            argValue = strValue.charAt(0);
        } else if (strType.contains("Byte")) {
            argValue = Byte.parseByte(strValue);
        } else if (strType.contains("Object")) {
            argValue = shell.getObject(strValue);
        } else {
            argValue = strType;
        }

        return argValue;
    }

    private Class<?> getTypeByName(Shell shell, String argType, String argValue) throws ClassNotFoundException {
        Class<?> argClass = null;

        try {
            argClass = Class.forName("java.lang." + argType);
        } catch (ClassNotFoundException ex1) {

            try {
                argClass = Class.forName(argType);
            } catch (ClassNotFoundException ex2) {
                Object obj = shell.getObject(argValue);

                if (obj != null) {
                    argClass = obj.getClass();
                }

            }
        }

        if (argClass == null) {
            throw new ClassNotFoundException(argType);
        }

        return argClass;
    }
}
