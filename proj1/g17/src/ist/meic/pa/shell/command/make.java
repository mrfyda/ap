package ist.meic.pa.shell.command;

import ist.meic.pa.Inspector;
import ist.meic.pa.shell.Shell;

import java.util.Arrays;

public class make implements ICommand {

    private final static String DESCRIPTION = "";

    private final static Integer NUM_PARAMS = 0;

    private String className;

    private String[] consArgs;

    public make(String[] args) {
        if (args.length >= NUM_PARAMS) {
            this.className = args[0];
            this.consArgs = Arrays.copyOfRange(args, 1, args.length);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) {
        Inspector inspector = new Inspector();
        Object object = inspector.getInstance(shell, className, consArgs);
        shell.setLatestObject(object);
    }

}
