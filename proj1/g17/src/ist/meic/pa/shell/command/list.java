package ist.meic.pa.shell.command;

import ist.meic.pa.shell.Shell;

public class list implements ICommand {

    private final static String DESCRIPTION = "lists available commands";

    private final static Integer NUM_PARAMS = 0;

    public list(String[] args) {
        if (args.length != NUM_PARAMS) {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) {
        shell.printAvailableCommands();
    }

}
