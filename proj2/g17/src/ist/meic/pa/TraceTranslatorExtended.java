package ist.meic.pa;

import javassist.*;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

public class TraceTranslatorExtended implements Translator {

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        CtClass clazz = pool.get(classname);

        String currentPackage = clazz.getPackageName();
        String tracePackage = this.getClass().getPackage().getName();
        if (currentPackage != null && currentPackage.startsWith(tracePackage))
            return;

        clazz.instrument(new TraceExprEditorExtended());
    }
}

class TraceExprEditorExtended extends ExprEditor {

    public void edit(FieldAccess fieldAccess) throws CannotCompileException {
        try {
            CtField field = fieldAccess.getField();

            if (fieldAccess.isReader()) {
                fieldAccess.replace(
                        String.format("{" +
                                "$_ = $proceed($$);" +
                                "if ($_ != null && $_.toString().length() > 0 && $_.toString() != \"{}\") {" +
                                "ist.meic.pa.TraceHistory.putRTL($_, \"%s\", \"%s\", %d);" +
                                "}" +
                                "}", fieldAccess.getFileName(), field.getName(), fieldAccess.getLineNumber())
                );
            } else if (fieldAccess.isWriter()) {
                fieldAccess.replace(
                        String.format("{" +
                                "$_ = $proceed($$);" +
                                "String filename = \"%s\";" +
                                "String method = \"%s\";" +
                                "int line = %d;" +
                                "ist.meic.pa.TraceHistory.putLTR($args, filename, method, line);" +
                                "}", fieldAccess.getFileName(), field.getName(), fieldAccess.getLineNumber())
                );
            }
        } catch (NotFoundException ignored) {
        }
    }

}
