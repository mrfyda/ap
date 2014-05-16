import ist.meic.pa.Trace;

class TestInstanceOfAux {

    public Object foo() { return new String("Foo"); }

    public void test() {
        Object str = foo();

        Boolean isString = str instanceof String;

        Trace.print(str);
    }
}

public class TestInstanceOf {
    public static void main(String args[]) {
        (new TestInstanceOfAux()).test();
    }
}
