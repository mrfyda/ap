package ist.meic.pa;

import ist.meic.pa.shell.Shell;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Collection;
import java.util.HashMap;

public class Inspector {

    public void inspect(Object object) {
        printClassInfo(object);

        Shell shell = new Shell(object);
        shell.run();
    }

    private void printClassInfo(Object object) {
        printClassName(object);
        System.err.println("----------");
        printFields(object);
    }

    private void printClassName(Object object) {
        String className = object.getClass().getName();
        System.err.printf("%s is an instance of class %s %n", object, className);
    }

    private void printFields(Object object) {
        Collection<Field> fields = getAllFields(object).values();

        for (Field field : fields) {
            try {
                System.err.printf("%s %s %s = %s %n",
                        Modifier.toString(field.getModifiers()), field.getType(), field.getName(), field.get(object));
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    private HashMap<String, Field> getAllFields(Object object) {
        HashMap<String, Field> fields = new HashMap<String, Field>();
        Class clazz = object.getClass();

        while (!clazz.equals(Object.class)) {
            for (Field field : clazz.getDeclaredFields()) {
                field.setAccessible(true);
                fields.put(field.getName(), field);
            }
            clazz = clazz.getSuperclass();
        }

        return fields;
    }

    public void inspectField(String fieldName, Object object) {
        try {
            Field field = getAllFields(object).get(fieldName);
            Object fieldValue = field.get(object);

            if (isPrimitiveType(field.getType())) {
                System.err.println(fieldValue);
            } else {
                inspect(fieldValue);
            }
        } catch (NullPointerException e) {
            System.err.printf("Field '%s' value is null %n", fieldName);
        } catch (IllegalAccessException e) {
            System.err.printf("Field '%s' not found %n", fieldName);
        }
    }

    public void modifyField(String fieldName, String fieldValue, Object object) {
        try {
            Field field = getAllFields(object).get(fieldName);

            field.setInt(object, Integer.parseInt(fieldValue));

            printClassInfo(object);
        } catch (NullPointerException e) {
            System.err.printf("Field '%s' not found %n", fieldName);
        } catch (NumberFormatException e) {
            System.err.printf("Field value '%s' is not an Integer %n", fieldValue);
        } catch (Exception e) {
            System.err.printf("Operation error with Field '%s' and value '%s' %n", fieldName, fieldValue);
        }
    }

    public void invokeMethod(Object object, String methodName, String[] methodArgs) {
        try {
            Integer argsNumber = methodArgs.length;

            Class[] cArgs = new Class[argsNumber];
            for (int i = 0; i < cArgs.length; i++) {
                cArgs[i] = int.class;
            }

            Method method = object.getClass().getDeclaredMethod(methodName, cArgs);
            method.setAccessible(true);

            Object[] params = new Object[argsNumber];
            for (int i = 0; i < params.length; i++) {
                params[i] = Integer.parseInt(methodArgs[i]);
            }

            String output = method.invoke(object, params).toString();

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

    private boolean isPrimitiveType(Class clazz) {
        return clazz.isPrimitive()
                || clazz.equals(Boolean.class)
                || clazz.equals(Integer.class)
                || clazz.equals(Character.class)
                || clazz.equals(Byte.class)
                || clazz.equals(Short.class)
                || clazz.equals(Double.class)
                || clazz.equals(Long.class)
                || clazz.equals(Float.class)
                || clazz.equals(String.class);
    }
}
