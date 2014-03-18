package ist.meic.pa.shell.command;

import ist.meic.pa.shell.Shell;

public class load implements ICommand {

    private final static Integer NUM_PARAMS = 1;

    private String arg;

    public load(String[] args) {
        if (args.length == NUM_PARAMS) {
            this.arg = args[0];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) {
        Object object = shell.getObject(arg);
        shell.setLatestObject(object);
    }

}
