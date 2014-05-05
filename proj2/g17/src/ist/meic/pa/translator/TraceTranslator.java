package ist.meic.pa.translator;

import javassist.*;
import javassist.expr.ExprEditor;

public class TraceTranslator implements Translator {

    private ExprEditor exprEditor;

    public TraceTranslator(ExprEditor exprEditor) {
        this.exprEditor = exprEditor;
    }

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        CtClass clazz = pool.get(classname);

        clazz.instrument(exprEditor);
    }
}
