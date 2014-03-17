package ist.meic.pa.shell;

import ist.meic.pa.shell.command.ICommand;
import ist.meic.pa.shell.command.TerminateInspectionException;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.Scanner;
import java.util.TreeMap;

public class Shell {
    private TreeMap<String, Object> objects = new TreeMap<String, Object>();
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

    public void run() {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            try {
                System.err.print("> ");

                String line = scanner.nextLine();
                String[] parts = line.split(" ");

                String commandName = parts[0];
                String[] commandArgs = Arrays.copyOfRange(parts, 1, parts.length);

                ICommand command = getCommandForName(commandName, commandArgs);
                if (command != null) {
                    command.execute(this);
                }
            } catch (TerminateInspectionException e) {
                break;
            }
        }

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
        } catch (Exception e) {
            System.err.println("This is weird, how did you get here?");
            e.printStackTrace();
        }

        return null;
    }

}


