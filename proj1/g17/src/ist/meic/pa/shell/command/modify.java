package ist.meic.pa.shell.command;


import ist.meic.pa.ExtraInspector;
import ist.meic.pa.shell.Shell;

public class modify implements ICommand {

    private final static String DESCRIPTION = "modifies a field with the typed value. modify <name> type:value";

    private String fieldName;

    private String valueTyped;

    public modify(String[] args) {
        String[] splitedArgs = args[0].split(" ", 2);
        this.fieldName = splitedArgs[0];
        this.valueTyped = splitedArgs[1];
    }

    public void execute(Shell shell) {
        Object object = shell.getLatestObject();

        ExtraInspector inspector = new ExtraInspector();
        inspector.modifyTypedField(shell, fieldName, valueTyped, object);
    }
}
