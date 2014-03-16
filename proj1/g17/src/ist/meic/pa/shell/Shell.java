package ist.meic.pa.shell;

import ist.meic.pa.shell.command.ClassCommand;
import ist.meic.pa.shell.command.GetCommand;
import ist.meic.pa.shell.command.ICommand;
import ist.meic.pa.shell.command.SetCommand;

import java.util.Scanner;
import java.util.TreeMap;

public class Shell {
    private enum ShellCommand {CLASS, GET, SET, INDEX}

    private TreeMap<String, Object> objects = new TreeMap<String, Object>();
    private Object object;

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

        while (true) {
            System.err.print("> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            String[] parts = line.split(" ");

            try {
                ShellCommand shellCommand = ShellCommand.valueOf(parts[0]);
                ICommand command;
                // TODO: redo with reflection
                switch (shellCommand) {
                    case CLASS:
                        command = new ClassCommand(parts[1]);
                        command.execute(this);
                        break;
                    case GET:
                        command = new GetCommand(parts[1]);
                        command.execute(this);
                        break;
                    case SET:
                        command = new SetCommand(parts[1]);
                        command.execute(this);
                        break;
                    case INDEX:
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.err.println("Unknown command: " + parts[0]);

//                try {
//                    Class<?>[] params = {};
//                    Method method = object.getClass().getClass().getMethod(commandText);
//                    method.invoke(object.getClass());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }

            scanner.close();
        }
    }

}


