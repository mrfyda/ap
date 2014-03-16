package ist.meic.pa;

import ist.meic.pa.shell.Shell;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

public class Inspector {

    public void inspect(Object object) throws IllegalAccessException {
        printClassName(object);
        System.err.println("----------");
        printFields(object);

        Shell shell = new Shell();
        shell.run();
    }

    private void printClassName(Object object) {
        String className = object.getClass().getName();
        System.err.printf("%s is an instance of class %s %n", object, className);
    }

    private void printFields(Object object) throws IllegalAccessException {
        Field[] fields = object.getClass().getDeclaredFields();

        for (Field field : fields) {
            field.setAccessible(true);

            System.err.printf("%s %s %s = %s %n",
                    Modifier.toString(field.getModifiers()), field.getType(), field.getName(), field.get(object));
        }
    }

    public Object getInstance(String className) {
        try {
            Object object = Class.forName(className).newInstance();
            System.out.println("class " + object.getClass().getName());
            return object;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
