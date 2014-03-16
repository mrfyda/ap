package ist.meic.pa.shell.command;

import ist.meic.pa.shell.Shell;

public class s implements ICommand {

    private final static Integer NUM_PARAMS = 1;

    private String arg;

    public s(String[] args) {
        if (args.length == NUM_PARAMS) {
            this.arg = args[0];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) {
        Object object = shell.getLatestObject();
        shell.putObject(arg, object);

        System.err.println("Saved name for object of type: " + object.getClass().getName());
    }

    public void undo() {

    }

}
