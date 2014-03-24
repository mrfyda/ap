package ist.meic.pa.shell.command;

import ist.meic.pa.ExtraInspector;
import ist.meic.pa.shell.Shell;

import java.util.Arrays;

public class call implements ICommand {

    private final static String DESCRIPTION = "calls method with typed arguments. call <method> type:value,type:value ...";

    private final static Integer NUM_PARAMS = 1;

    private String methodName;

    private String[] methodArgs;

    public call(String[] args) {
        String[] splitedArgs = args[0].split(" ", 2);
        this.methodName = splitedArgs[0];

        if (splitedArgs.length > 1) {
            this.methodArgs = splitedArgs[1].split(",");
        } else {
            this.methodArgs = new String[0];
        }
    }

    public void execute(Shell shell) {
        Object object = shell.getLatestObject();

        ExtraInspector inspector = new ExtraInspector();
        inspector.invokeTypedMethod(shell, object, methodName, methodArgs);
    }

}
