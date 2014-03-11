package ist.meic.pa.shell.command;

import ist.meic.pa.Inspector;
import ist.meic.pa.shell.Shell;

public class SetCommand implements ICommand {

    private String arg;
    private Object object;

    public SetCommand(String arg) {
        this.arg = arg;
    }

    public void execute(Shell shell) {
    	this.setObject(arg, this.getLatestObject());

        System.out.println("Saved name for object of type: " + object.getClass().getName());
    }

    public void undo() {

    }

}
