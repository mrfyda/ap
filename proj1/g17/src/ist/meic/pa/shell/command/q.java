package ist.meic.pa.shell.command;

import ist.meic.pa.shell.Shell;

public class q implements ICommand {

    private final static String DESCRIPTION = "terminates current shell";

    private final static Integer NUM_PARAMS = 0;

    public q(String[] args) {
        if (args.length != NUM_PARAMS) {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) throws TerminateInspectionException {
        throw new TerminateInspectionException();
    }

}
