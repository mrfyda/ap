package ist.meic.pa.shell.command;

import ist.meic.pa.Inspector;
import ist.meic.pa.shell.Shell;

public class i implements ICommand {

    private final static Integer NUM_PARAMS = 1;

    private String fieldName;

    public i(String[] args) {
        if (args.length == NUM_PARAMS) {
            this.fieldName = args[0];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) {
        Object object = shell.getLatestObject();
        new Inspector().inspectField(fieldName, object);
    }

    public void undo() {

    }

}
