package ist.meic.pa.shell.command;

import ist.meic.pa.Inspector;
import ist.meic.pa.shell.Shell;

import java.util.Arrays;

public class c implements ICommand {

    private final static Integer NUM_PARAMS = 1;

    private String methodName;

    private String[] methodArgs;

    public c(String[] args) {
        if (args.length >= NUM_PARAMS) {
            this.methodName = args[0];
            this.methodArgs = Arrays.copyOfRange(args, 1, args.length);
        } else {
            throw new IllegalArgumentException();
        }
    }

    public void execute(Shell shell) {
        Object object = shell.getLatestObject();

        Inspector inspector = new Inspector();
        inspector.invokeMethod(object, methodName, methodArgs);
    }

}
