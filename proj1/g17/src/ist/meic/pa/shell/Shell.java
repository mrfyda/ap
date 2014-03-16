package ist.meic.pa.shell;

import ist.meic.pa.shell.command.ICommand;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeMap;

public class Shell {
    private TreeMap<String, Object> objects = new TreeMap<String, Object>();
    private Object object;

    public Shell(Object object) {
        this.object = object;
    }

    public void putObject(String name, Object o) {
        objects.put(name, o);
    }

    public Object getObject(String name) {
        return objects.get(name);
    }

    public void setLatestObject(Object o) {
        object = o;
    }

    public Object getLatestObject() {

        return object;
    }

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.err.print("> ");

            String line = scanner.nextLine();
            String[] parts = line.split(" ");

            String commandName = parts[0];
            
            try {
                String commandPackage = "ist.meic.pa.shell.command." + commandName;
                Class<?> commandClass = Class.forName(commandPackage);
                Class[] constructorParamTypes = new Class[]{String[].class};
                Constructor<?> commandConstructor =
                        commandClass.getConstructor(constructorParamTypes);

                Object[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);
                Object[] constructorParams = new Object[]{commandArgs};
                ICommand command = (ICommand) commandConstructor.newInstance(constructorParams);
                command.execute(this);
            } catch (ClassNotFoundException e) {
                System.err.println("Unknown command");
            } catch (Exception e) {
                System.err.println("This is weird, how did you get here?");
                e.printStackTrace();
            }
        }

        //scanner.close();
    }

}


