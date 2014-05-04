package ist.meic.pa.translator.editor;

import javassist.CannotCompileException;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

public class TraceExprEditor extends ExprEditor {

    public void edit(MethodCall methodCall) throws CannotCompileException {
        try {
            CtMethod method = methodCall.getMethod();

            methodCall.replace(
                    String.format("{" +
                            "String filename = \"%s\";" +
                            "String method = \"%s\";" +
                            "int line = %d;" +
                            "ist.meic.pa.TraceHistory.putLTR($args, filename, method, line);" +
                            "$_ = $proceed($$);" +
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
