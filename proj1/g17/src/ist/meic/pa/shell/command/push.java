package ist.meic.pa.shell.command;

import ist.meic.pa.ExtraInspector;
import ist.meic.pa.shell.Shell;

public class push implements ICommand {

    private final static String DESCRIPTION = "stash the current context";

    private final static Integer NUM_PARAMS = 0;

    public push(String[] args) {
        if (args.length != NUM_PARAMS) {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) throws TerminateInspectionException {
        Object object = shell.getLatestObject();
        new ExtraInspector().push(object);
    }

}
