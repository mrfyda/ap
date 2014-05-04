package ist.meic.pa.translator.editor;

import javassist.CannotCompileException;
import javassist.CtClass;
import javassist.CtField;
import javassist.NotFoundException;
import javassist.expr.*;

public class TraceExprEditorExtended extends TraceExprEditor {

    public void edit(FieldAccess fieldAccess) throws CannotCompileException {
        try {
            CtField field = fieldAccess.getField();

            if (fieldAccess.isReader()) {
                fieldAccess.replace(
                        String.format("{" +
                                "$_ = $proceed($$);" +
                                "if ($_ != null && $_.toString().length() > 0 && $_.toString() != \"{}\") {" +
                                "ist.meic.pa.TraceHistory.putFieldRead($_, \"%s\", \"%s\", %d);" +
                                "}" +
                                "}", fieldAccess.getFileName(), field.getName(), fieldAccess.getLineNumber())
                );
            } else if (fieldAccess.isWriter()) {
                fieldAccess.replace(
                        String.format("{" +
                                "$_ = $proceed($$);" +
                                "ist.meic.pa.TraceHistory.putFieldWrite($1, \"%s\", \"%s\", %d);" +
                                "}", fieldAccess.getFileName(), field.getName(), fieldAccess.getLineNumber())
                );
            }
        } catch (NotFoundException ignored) {
        }
    }

    public void edit(Handler exceptionHandler) throws CannotCompileException {
        try {
            CtClass exception = exceptionHandler.getType();

            exceptionHandler.insertBefore(
                    String.format("{" +
                            "ist.meic.pa.TraceHistory.putHandler($1, \"%s\", \"%s\", %d);" +
                            "}", exceptionHandler.getFileName(), exception.getName(), exceptionHandler.getLineNumber())
            );
        } catch (NotFoundException ignored) {
        }
    }

    public void edit(Cast cast) throws CannotCompileException {
        try {
            CtClass clazz = cast.getType();

            cast.replace(
                    String.format("{" +
                            "$_ = $proceed($$);" +
                            "if ($_ != null && $_.toString().length() > 0 && $_.toString() != \"{}\") {" +
                            "ist.meic.pa.TraceHistory.putCast($_, \"%s\", \"%s\", %d);" +
                            "}" +
                            "}", cast.getFileName(), clazz.getName(), cast.getLineNumber())
            );
        } catch (NotFoundException ignored) {
        }
    }

    public void edit(Instanceof instanceOf) throws CannotCompileException {
        try {
            CtClass clazz = instanceOf.getType();

            instanceOf.replace(
                    String.format("{" +
                            "$_ = $proceed($$);" +
                            "ist.meic.pa.TraceHistory.putInstanceOf($1, \"%s\", \"%s\", %d);" +
                            "}", instanceOf.getFileName(), clazz.getName(), instanceOf.getLineNumber())
            );
        } catch (NotFoundException ignored) {
        }
    }

    public void edit(NewArray newArray) throws CannotCompileException {
        try {
            CtClass clazz = newArray.getComponentType();

            newArray.replace(
                    String.format("{" +
                            "$_ = $proceed($$);" +
                            "if ($_ != null && $_.toString().length() > 0 && $_.toString() != \"{}\") {" +
                            "ist.meic.pa.TraceHistory.putNewArray($_, \"%s\", \"%s\", %d);" +
                            "}" +
                            "}", newArray.getFileName(), clazz.getName(), newArray.getLineNumber())
            );
        } catch (NotFoundException ignored) {
        }
    }

}
