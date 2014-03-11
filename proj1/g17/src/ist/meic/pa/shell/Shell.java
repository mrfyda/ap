package ist.meic.pa.shell;

import ist.meic.pa.shell.command.ClassCommand;

import java.util.Scanner;
import java.util.TreeMap;

public class Shell {
    private enum ShellCommand {CLASS, GET, SET, INDEX}

    private TreeMap<String, Object> objects = new TreeMap<String, Object>();
    private Object object;

    public void run() {

        while (true) {
            System.out.print("Command:> ");
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            String[] parts = line.split(" ");

            try {
                ShellCommand shellCommand = ShellCommand.valueOf(parts[0]);

                switch (shellCommand) {
                    case CLASS:
                        ClassCommand command = new ClassCommand(parts[1]);
                        command.execute();
                        object = command.getResult();
                        break;
                    case GET:
                        objects.put(parts[1], object);

                        System.out.println("Saved name for object of type: " + object.getClass().getName());
                        break;
                    case SET:
                        object = objects.get(parts[1]);
                        break;
                    case INDEX:
                        break;
                }
            } catch (IllegalArgumentException e) {
                System.out.println("Unknown command: " + parts[0]);

//                try {
//                    Class<?>[] params = {};
//                    Method method = object.getClass().getClass().getMethod(commandText);
//                    method.invoke(object.getClass());
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
            }
        }
    }

}


