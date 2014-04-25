package ist.meic.pa;

import javassist.*;

public class TraceTranslator implements Translator {

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {

    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        CtClass clazz = pool.get(classname);

        if (clazz.getPackageName() != null && clazz.getPackageName().equals("ist.meic.pa")) return;

        CtConstructor[] constructors = clazz.getConstructors();

        for (CtConstructor constructor : constructors) {
            constructor.insertAfter(
                    String.format("{" +
                            "StackTraceElement ste = Thread.currentThread().getStackTrace()[2];" +
                            "ist.meic.pa.TraceHistory.put($0, ste.getFileName(), \"%s\", ste.getLineNumber());" +
                            "}", constructor.getLongName())
            );
        }
    }
}
