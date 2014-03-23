package ist.meic.pa.shell.command;


import ist.meic.pa.ExtraInspector;
import ist.meic.pa.shell.Shell;

import java.util.Arrays;

public class modify implements ICommand{

    private final static String DESCRIPTION = "modifies a field with the typed value";

    private final static Integer NUM_PARAMS = 2;

    private String fieldName;

    private String valueTyped;

    public modify(String[] args) {
        if (args.length >= NUM_PARAMS) {
            this.fieldName = args[0];
            this.valueTyped =args[1];
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) {
        Object object = shell.getLatestObject();

        ExtraInspector inspector = new ExtraInspector();
        inspector.modifyTypedField(shell, fieldName, valueTyped, object);
    }
}
