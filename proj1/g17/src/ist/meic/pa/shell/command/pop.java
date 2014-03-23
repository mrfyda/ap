package ist.meic.pa.shell.command;

import ist.meic.pa.ExtraInspector;
import ist.meic.pa.shell.Shell;

public class pop implements ICommand {

    private final static String DESCRIPTION = "restores stashed context";

    private final static Integer NUM_PARAMS = 0;

    public pop(String[] args) {
        if (args.length != NUM_PARAMS) {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) {
        new ExtraInspector().popHistory();
    }

}
