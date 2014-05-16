import ist.meic.pa.Trace;

class Test {

    public Object foo() {
        return new String("Foo");
    }

    public Object bar() {
        return foo();
    }

    public Object baz() {
        return bar();
    }

    public void test() {
        Trace.print(foo());
        Trace.print(bar());
        Trace.print(baz());
    }
}

public class Test1 {

    public static void main(String args[]) {
        (new Test()).test();
    }
}
