package ist.meic.pa.shell.command;

import ist.meic.pa.ExtraInspector;
import ist.meic.pa.shell.Shell;

import java.util.Arrays;

public class make implements ICommand {

    private final static String DESCRIPTION = "creates a new instance. make <class> type:value,type:value ...";

    private final static Integer NUM_PARAMS = 0;

    private String className;

    private String[] consArgs;

    public make(String[] args) {
        String[] splitedArgs = args[0].split(" ", 2);
        this.className = splitedArgs[0];

        if(splitedArgs.length > 1) {
            this.consArgs = splitedArgs[1].split(",");
        } else {
            this.consArgs = new String[0];
        }
    }

    public void execute(Shell shell) {
        ExtraInspector inspector = new ExtraInspector();
        Object object = inspector.getInstance(shell, className, consArgs);
        shell.setLatestObject(object);
    }

}
