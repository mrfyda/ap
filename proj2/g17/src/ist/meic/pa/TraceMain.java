package ist.meic.pa;

import javassist.*;

public class TraceMain {

    private Translator translator;

    public TraceMain() {
        this.translator = new TraceTranslator();
    }

    public TraceMain(Translator translator) {
        this.translator = translator;
    }

    public void run(String[] args) {
        if (args.length < 1) {
            System.err.println("usage: ist.meic.pa.TraceVM <target application class> [<target application arguments>]");
            System.exit(1);
        }

        ClassPool pool = ClassPool.getDefault();
        Loader classLoader = new Loader();

        try {
            classLoader.addTranslator(pool, this.translator);
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
