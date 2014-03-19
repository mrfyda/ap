package ist.meic.pa.shell.command;

import ist.meic.pa.ExtraInspector;
import ist.meic.pa.shell.Shell;

import java.util.Arrays;

public class call implements ICommand {

    private final static String DESCRIPTION = "calls method with typed arguments";

    private final static Integer NUM_PARAMS = 1;

    private String methodName;

    private String[] methodArgs;

    public call(String[] args) {
        if (args.length >= NUM_PARAMS) {
            this.methodName = args[0];
            this.methodArgs = Arrays.copyOfRange(args, 1, args.length);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) {
        Object object = shell.getLatestObject();

        ExtraInspector inspector = new ExtraInspector();
        inspector.invokeTypedMethod(shell, object, methodName, methodArgs);
    }

}
