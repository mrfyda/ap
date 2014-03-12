package ist.meic.pa.shell.command;

import ist.meic.pa.shell.Shell;

public class GetCommand implements ICommand {

    private String arg;
    private Object object;

    public GetCommand(String arg) {
        this.arg = arg;
    }

    public void execute(Shell shell) {
        object = shell.getObject(arg);
        shell.setLatestObject(object);
    }

    public void undo() {

    }

}
