package ist.meic.pa.shell.command;

import ist.meic.pa.Inspector;

public class ClassCommand implements ICommand {

    private String arg;
    private Object object;

    public ClassCommand(String arg) {
        this.arg = arg;
    }

    public void execute() {
        Inspector inspector = new Inspector();
        object = inspector.getInstance(arg);
    }

    public void undo() {

    }

    public Object getResult() {
        return object;
    }

}
