package ist.meic.pa;

import ist.meic.pa.shell.Shell;

import java.beans.PropertyEditor;
import java.beans.PropertyEditorManager;
import java.util.HashMap;
import java.util.Map;

public class ArgumentHelper extends Inspector {

    private static final Map<String, Class> primitiveTypes = new HashMap<String, Class>();

    private static final Map<String, Class> realTypes = new HashMap<String, Class>();

    static {
        primitiveTypes.put("Integer", Integer.TYPE);
        primitiveTypes.put("int", Integer.TYPE);
        primitiveTypes.put("Long", Long.TYPE);
        primitiveTypes.put("long", Long.TYPE);
        primitiveTypes.put("Double", Double.TYPE);
        primitiveTypes.put("double", Double.TYPE);
        primitiveTypes.put("Float", Float.TYPE);
        primitiveTypes.put("float", Float.TYPE);
        primitiveTypes.put("Boolean", Boolean.TYPE);
        primitiveTypes.put("bool", Boolean.TYPE);
        primitiveTypes.put("Byte", Byte.TYPE);
        primitiveTypes.put("byte", Byte.TYPE);
        primitiveTypes.put("Short", Short.TYPE);
        primitiveTypes.put("short", Short.TYPE);
        primitiveTypes.put("String", String.class);
        primitiveTypes.put("str", String.class);

        realTypes.put("Integer", Integer.class);
        realTypes.put("int", Integer.TYPE);
        realTypes.put("Long", Long.class);
        realTypes.put("long", Long.TYPE);
        realTypes.put("Double", Double.class);
        realTypes.put("double", Double.TYPE);
        realTypes.put("Float", Float.class);
        realTypes.put("float", Float.TYPE);
        realTypes.put("Boolean", Boolean.class);
        realTypes.put("bool", Boolean.TYPE);
        realTypes.put("Byte", Byte.class);
        realTypes.put("byte", Byte.TYPE);
        realTypes.put("Short", Short.class);
        realTypes.put("short", Short.TYPE);
        realTypes.put("String", String.class);
        realTypes.put("str", String.class);
    }

    public void prepareArgs(Shell shell, String[] args, Class<?>[] types, Object[] values) {
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

    public Object parseArgVal(Shell shell, String strType, String strValue) {
        Object argValue = null;

        if (!strType.toLowerCase().contains("obj")) {
            Class primitiveType = primitiveTypes.get(strType);
            PropertyEditor editor = PropertyEditorManager.findEditor(primitiveType);
            editor.setAsText(strValue);
            argValue = editor.getValue();
        } else {
            argValue = shell.getObject(strValue);
        }

        return argValue;
    }

    private Class<?> getTypeByName(Shell shell, String argType, String argValue) throws IllegalArgumentException, ClassNotFoundException {
        Class<?> argClass = null;

        if (!argType.toLowerCase().contains("obj")) {
            /* Check for well known types */
            argClass = realTypes.get(argType);
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

}
