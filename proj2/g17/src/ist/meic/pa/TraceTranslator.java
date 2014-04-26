package ist.meic.pa;

import javassist.*;
import javassist.bytecode.MethodInfo;
import javassist.expr.ExprEditor;
import javassist.expr.MethodCall;
import javassist.expr.NewExpr;

import java.util.List;

public class TraceTranslator implements Translator {

    @Override
    public void start(ClassPool pool) throws NotFoundException, CannotCompileException {
    }

    @Override
    public void onLoad(ClassPool pool, String classname) throws NotFoundException, CannotCompileException {
        CtClass clazz = pool.get(classname);

        onMethodCall(clazz);
    }

    private void onMethodCall(CtClass clazz) throws CannotCompileException {
        clazz.instrument(new ExprEditor() {
            public void edit(MethodCall methodCall) throws CannotCompileException {
                try {
                    CtMethod method = methodCall.getMethod();

                    if (!method.getName().equals("main")) {
                        MethodInfo methodInfo = method.getMethodInfo();
                        List attributes = methodInfo.getAttributes();
                        TraceHistory.putLTR(attributes.toArray(), methodCall.getFileName(), method.getLongName(), methodCall.getLineNumber());
                    }
                } catch (NotFoundException e) {
                    System.out.println("Method " + methodCall.getMethodName() + " was not found!");
                }
            }

            public void edit(NewExpr constructorCall) throws CannotCompileException {
                try {
                    CtConstructor constructor = constructorCall.getConstructor();

                    MethodInfo methodInfo = constructor.getMethodInfo();
                    List attributes = methodInfo.getAttributes();
                    TraceHistory.putRTL(attributes.toArray()[0], constructorCall.getFileName(), constructor.getLongName(), constructorCall.getLineNumber());

                    System.out.println("Constructor " + constructor.getLongName() + " was successfully executed, in file " + constructorCall.getFileName() + " at line " + constructorCall.getLineNumber());
                    System.out.println("Value " + attributes.toArray()[0]);

                } catch (NotFoundException e) {
                    System.out.println("Constructor " + constructorCall.getClassName() + " was not found!");
                }
            }
        });
    }
}
