package ist.meic.pa;

import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.NotFoundException;
import javassist.Translator;

public class Loader extends javassist.Loader {
    private ClassPool source;
    private Translator translator;

    public Loader() {
    }

    @Override
    public void setClassPool(ClassPool cp) {
        super.setClassPool(cp);
        source = cp;
    }

    @Override
    public void addTranslator(ClassPool cp, Translator t)
            throws NotFoundException, CannotCompileException {
        super.addTranslator(cp, t);
        source = cp;
        translator = t;
    }

    @Override
    protected Class delegateToParent(String classname)
            throws ClassNotFoundException {

        try {
            if (source != null) {
                if (translator != null) {
                    translator.onLoad(source, classname);
                }
            }
        } catch (NotFoundException e) {
            System.out.println("[NotFoundException] Class: " + classname + ", this ain't good!");
        } catch (CannotCompileException e) {
            System.out.println("[CannotCompileException] Class: " + classname + ", this ain't good!");
        }

        ClassLoader cl = getParent();
        if (cl != null) {
            return cl.loadClass(classname);
        } else {
            return findSystemClass(classname);
        }
    }
}
