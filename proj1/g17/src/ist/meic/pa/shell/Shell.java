package ist.meic.pa.shell;

import ist.meic.pa.shell.command.ICommand;
import ist.meic.pa.shell.command.TerminateInspectionException;
import ist.meic.pa.shell.command.nop;

import java.io.File;
import java.io.FilenameFilter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.util.*;

public class Shell {
    private static TreeMap<String, Object> objects = new TreeMap<String, Object>();
    private Object latestObject;

    public Shell(Object object) {
        this.latestObject = object;
    }

    public void putObject(String name, Object o) {
        objects.put(name, o);
    }

    public Object getObject(String name) {
        return objects.get(name);
    }

    public void setLatestObject(Object o) {
        latestObject = o;
    }

    public Object getLatestObject() {
        return latestObject;
    }

    public void printObjectsStatus() {
        for (Map.Entry<String, Object> entry : objects.entrySet()) {
            System.err.printf("%s -> %s %n", entry.getKey(), entry.getValue());
        }
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        System.err.println("[New context started]");

        while (true) {
            try {
                System.err.print("> ");

                String line = scanner.nextLine();
                String[] parts = line.split(" ");

                String commandName = parts[0];
                String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

                ICommand command = getCommandForName(commandName, commandArgs);
                command.execute(this);
            } catch (TerminateInspectionException e) {
                break;
            }
        }

        System.err.println("[Context terminated]");

        //scanner.close();
    }

    private ICommand getCommandForName(String commandName, Object[] commandArgs) {
        try {
            String commandQualifiedName = ICommand.class.getPackage().getName() + "." + commandName;
            Class<?> commandClass = Class.forName(commandQualifiedName);

            Class[] constructorParamTypes = new Class[]{String[].class};
            Constructor<?> commandConstructor = commandClass.getConstructor(constructorParamTypes);

            Object[] constructorParams = new Object[]{commandArgs};
            return (ICommand) commandConstructor.newInstance(constructorParams);
        } catch (ClassNotFoundException e) {
            System.err.printf("Command '%s' not found %n", commandName);
        } catch (NoClassDefFoundError e) {
            System.err.printf("Command '%s' not found %n", commandName);
        } catch (Exception e) {
            System.err.println("Incorrect number of arguments");
        }

        return new nop();
    }

    public void printAvailableCommands() {
        String currentPath = ICommand.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        String classesPath = ICommand.class.getPackage().getName().replace(".", "/");

        File[] classFiles = listClassFiles(currentPath + classesPath);
        List<Class> classes = getClassesFromFiles(classFiles);

        for (Class clazz : classes) {
            try {
                Field field = clazz.getDeclaredField("DESCRIPTION");
                field.setAccessible(true);
                String description = field.get(null).toString();

                System.err.printf("%s: %s %n", clazz.getSimpleName(), description);
            } catch (Exception ignored) {
            }
        }
    }

    private File[] listClassFiles(String path) {
        File folder = new File(path);
        File[] files = folder.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.toLowerCase().endsWith(".class");
            }
        });

        assert files != null;

        return files;
    }

    private List<Class> getClassesFromFiles(File[] files) {
        List<Class> classes = new ArrayList<Class>();

        for (File file : files) {
            try {
                String className = file.getName().replaceAll(".class", "");
                String classQualifiedName = ICommand.class.getPackage().getName() + "." + className;

                Class clazz = Class.forName(classQualifiedName);

                classes.add(clazz);
            } catch (ClassNotFoundException ignored) {
            }
        }

        return classes;
    }

}
