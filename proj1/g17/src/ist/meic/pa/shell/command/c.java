package ist.meic.pa.shell.command;

import ist.meic.pa.shell.Shell;

import java.lang.reflect.Method;
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
        Class[] cArgs = null;
        Integer argsNumber = methodArgs.length;

        if (argsNumber > 0) {
            cArgs = new Class[argsNumber];
            for (int i = 0; i < cArgs.length; i++) {
                cArgs[i] = Integer.class;
            }
        }

        try {
            Method method = object.getClass().getDeclaredMethod(methodName, cArgs);
            method.setAccessible(true);

            Integer[] params = new Integer[argsNumber];
            for (int i = 0; i < params.length; i++) {
                params[i] = Integer.parseInt(methodArgs[i]);
            }

            String output = method.invoke(object, params).toString();

            System.err.println(output);
        } catch (NoSuchMethodException e) {
            System.err.println("Unknown method: " + methodName);
        } catch (NumberFormatException e) {
            System.err.println("Parameters must be integers!!!");
        } catch (Exception e) {
            System.err.println("This is weird, how did you get here?");
            e.printStackTrace();
        }
    }

    public void undo() {

    }

}
