package ist.meic.pa.shell.command;

import ist.meic.pa.Inspector;
import ist.meic.pa.shell.Shell;

public class ClassCommand implements ICommand {

    private String arg;
    private Object object;

    public ClassCommand(String arg) {
        this.arg = arg;
    }

    public void execute(Shell shell) {
        Inspector inspector = new Inspector();
        object = inspector.getInstance(arg);
        shell.setLatestObject(object);
    }

    public void undo() {

    }

}
