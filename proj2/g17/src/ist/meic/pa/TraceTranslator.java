package ist.meic.pa;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

public class TraceTranslator implements Translator {

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        CtClass clazz = pool.get(classname);

        if (clazz.getSimpleName().equals("TraceHistory")) return;

        clazz.instrument(new TraceExprEditor());
    }
}

class TraceExprEditor extends ExprEditor {

    public void edit(MethodCall methodCall) throws CannotCompileException {
        try {
            CtMethod method = methodCall.getMethod();

            methodCall.replace(
                    String.format("{" +
                            "$_ = $proceed($$);" +
                            "String filename = \"%s\";" +
                            "String method = \"%s\";" +
                            "int line = %d;" +
                            "ist.meic.pa.TraceHistory.putLTR($args, filename, method, line);" +
                            "ist.meic.pa.TraceHistory.putRTL(($w)$_, filename, method, line);" +
                            "}", methodCall.getFileName(), method.getLongName(), methodCall.getLineNumber())
            );
        } catch (NotFoundException ignored) {
        }
    }

    public void edit(NewExpr newExpr) throws CannotCompileException {
        try {
            CtConstructor constructor = newExpr.getConstructor();

            newExpr.replace(
                    String.format("{" +
                            "$_ = $proceed($$);" +
                            "if ($_ != null && $_.toString().length() > 0 && $_.toString() != \"{}\") {" +
                            "ist.meic.pa.TraceHistory.putRTL($_, \"%s\", \"%s\", %d);" +
                            "}" +
                            "}", newExpr.getFileName(), constructor.getLongName(), newExpr.getLineNumber())
            );
        } catch (NotFoundException ignored) {
        }
    }

}
