package ist.meic.pa;

import ist.meic.pa.shell.Shell;
import ist.meic.pa.shell.command.TerminateInspectionException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.EmptyStackException;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ExtraInspector extends Inspector {

    private static final Map<String, Class> typeMap = new HashMap<String, Class>();

    static {
        /* Simple Types */
        typeMap.put("int", Integer.TYPE);
        typeMap.put("long", Long.TYPE);
        typeMap.put("double", Double.TYPE);
        typeMap.put("float", Float.TYPE);
        typeMap.put("bool", Boolean.TYPE);
        typeMap.put("char", Character.TYPE);
        typeMap.put("byte", Byte.TYPE);
        typeMap.put("void", Void.TYPE);
        typeMap.put("short", Short.TYPE);

        /* Extended Types */
        typeMap.put("Integer", Integer.class);
        typeMap.put("Long", Long.class);
        typeMap.put("Double", Double.class);
        typeMap.put("Float", Float.class);
        typeMap.put("Boolean", Boolean.class);
        typeMap.put("Character", Character.class);
        typeMap.put("Byte", Byte.class);
        typeMap.put("Void", Void.class);
        typeMap.put("Short", Short.class);
    }

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
            System.err.println("method name: " + methodName);
            Method method = object.getClass().getDeclaredMethod(methodName, argTypes);
            System.err.println("success!");
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

        if (strType.toLowerCase().contains("int")) {
            argValue = Integer.parseInt(strValue);
        } else if (strType.contains("String")) {
            argValue = strValue;
        } else if (strType.toLowerCase().contains("long")) {
            argValue = Long.parseLong(strValue);
        } else if (strType.toLowerCase().contains("short")) {
            argValue = Short.parseShort(strValue);
        } else if (strType.toLowerCase().contains("boolean")) {
            argValue = Boolean.parseBoolean(strValue);
        } else if (strType.toLowerCase().contains("float")) {
            argValue = Float.parseFloat(strValue);
        } else if (strType.toLowerCase().contains("double")) {
            argValue = Double.parseDouble(strValue);
        } else if (strType.toLowerCase().contains("char")) {
            argValue = strValue.charAt(0);
        } else if (strType.toLowerCase().contains("byte")) {
            argValue = Byte.parseByte(strValue);
        } else if (strType.toLowerCase().contains("obj")) {
            argValue = shell.getObject(strValue);
        } else {
            argValue = strType;
        }

        return argValue;
    }

    private Class<?> getTypeByName(Shell shell, String argType, String argValue) throws IllegalArgumentException, ClassNotFoundException {
        Class<?> argClass = null;

        if (!argType.toLowerCase().contains("obj")) {
            /* Check for well known types */
            argClass = typeMap.get(argType);

            if (argClass == null) {
                /* Check for other java classes */
                argClass = Class.forName(argType);
            }
        } else {
            /* Check for stored objects */
            Object obj = shell.getObject(argValue);

            if (obj != null) {
                argClass = obj.getClass();
            } else {
                throw new IllegalArgumentException();
            }
        }

        return argClass;
    }

    public void modifyTypedField(Shell shell, String fieldName, String fieldValue, Object object) {
        try {
            Field field = getAllFields(object).get(fieldName);

            field.set(object, parseArgVal(shell, field.getType().getName(), fieldValue));

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
