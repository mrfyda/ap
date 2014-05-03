import ist.meic.pa.Trace;

class TestCastAux {

    public Object foo() {
        return new String("Foo");
    }

    public void test() {
        Object obj = foo();

        String str = (String) obj;

        Trace.print(obj);
    }
}

public class TestCast {
    public static void main(String args[]) {
        (new TestCastAux()).test();
    }
}
