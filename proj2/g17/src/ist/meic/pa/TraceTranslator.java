package ist.meic.pa;

import javassist.*;

public class TraceTranslator implements Translator {

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        CtClass clazz = pool.get(classname);

        if (clazz.getPackageName() != null && (
                clazz.getName().equals("ist.meic.pa.TraceHistory") ||
                        clazz.getName().equals("ist.meic.pa.TraceStep"))) return;

        CtConstructor[] constructors = clazz.getDeclaredConstructors();

        for (CtConstructor constructor : constructors) {
            constructor.insertAfter(
                    String.format("{" +
                            "StackTraceElement ste = Thread.currentThread().getStackTrace()[2];" +
                            "ist.meic.pa.TraceHistory.putRTL($0, ste.getFileName(), \"%s\", ste.getLineNumber());" +
                            "}", constructor.getLongName())
            );
        }

        CtMethod[] methods = clazz.getDeclaredMethods();

        for (CtMethod method : methods) {
            method.insertBefore(
                    String.format("{" +
                            "StackTraceElement ste = Thread.currentThread().getStackTrace()[2];" +
                            "ist.meic.pa.TraceHistory.putLTR($args, ste.getFileName(), \"%s\", ste.getLineNumber());" +
                            "}", method.getLongName())
            );
        }
    }
}
