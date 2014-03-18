package ist.meic.pa.shell.command;

import ist.meic.pa.Inspector;
import ist.meic.pa.shell.Shell;

public class make implements ICommand {

    private final static Integer NUM_PARAMS = 1;

    private String arg;

    public make(String[] args) {
        if (args.length == NUM_PARAMS) {
            this.arg = args[0];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) {
        Inspector inspector = new Inspector();
        Object object = inspector.getInstance(arg);
        shell.setLatestObject(object);
    }

}
