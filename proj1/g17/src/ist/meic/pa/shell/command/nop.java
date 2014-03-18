package ist.meic.pa.shell.command;

import ist.meic.pa.shell.Shell;

public class nop implements ICommand {

    private final static String DESCRIPTION = "does nothing";

    public void execute(Shell shell) {
    }

}
