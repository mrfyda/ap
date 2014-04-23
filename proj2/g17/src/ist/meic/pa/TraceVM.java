package ist.meic.pa;

import javassist.*;

public class TraceVM {

    public static void main(String[] args) {
        if (args.length < 1) {
            System.err.println("usage: ist.meic.pa.TraceVM <target application class name> [<target application arguments>]");
            System.exit(1);
        }

        Translator translator = new TracerTranslator();
        ClassPool pool = ClassPool.getDefault();
        Loader classLoader = new Loader();

        try {
            classLoader.addTranslator(pool, translator);
        } catch (NotFoundException e) {
            e.printStackTrace();
        } catch (CannotCompileException e) {
            e.printStackTrace();
        }

        try {
            String[] restArgs = new String[args.length - 1];
            System.arraycopy(args, 1, restArgs, 0, restArgs.length);

            classLoader.run(args[0], restArgs);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
    }

}
