package ist.meic.pa.vm;

import ist.meic.pa.translator.TraceTranslator;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.Loader;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;

public class TraceRunner {

    private ExprEditor exprEditor;

    public TraceRunner(ExprEditor exprEditor) {
        this.exprEditor = exprEditor;
    }

    public void run(String[] args) {
        if (args.length < 1) {
            System.err.println("missing target application class");
            System.exit(1);
        }

        ClassPool pool = ClassPool.getDefault();
        Loader classLoader = new Loader();

        try {
            classLoader.addTranslator(pool, new TraceTranslator(this.exprEditor));
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
