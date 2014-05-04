import ist.meic.pa.Trace;

class TestFieldAccessAux {

    class ObjectWField {
        Object object;
    }

    public Object foo() { return new String("Foo"); }

    public void test() {
        ObjectWField objWField = new ObjectWField();

        objWField.object = foo();

        Trace.print(objWField.object);
    }
}

public class TestFieldAccess {
    public static void main(String args[]) {
        (new TestFieldAccessAux()).test();
    }
}
