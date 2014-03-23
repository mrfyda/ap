package ist.meic.pa.shell.command;

import ist.meic.pa.ExtraInspector;
import ist.meic.pa.shell.Shell;

public class status implements ICommand {

    private final static String DESCRIPTION = "restores stashed context";

    private final static Integer NUM_PARAMS = 0;

    public status(String[] args) {
        if (args.length != NUM_PARAMS) {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) {
        ExtraInspector inspector = new ExtraInspector();
        Object object = shell.getLatestObject();

        System.err.printf("Inspecting %s %n", object);
        inspector.printClassInfo(object);
        System.err.println("----------");
        System.err.println("Stashed contexts:");
        inspector.printHistoryStatus();
        System.err.println("----------");
        System.err.println("Saved objects:");
        shell.printObjectsStatus();
    }

}
