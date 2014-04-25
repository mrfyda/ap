import ist.meic.pa.Trace;

class TestAux0 {

    public Object foo() {
        return new MyString("Foo");
    }

    public Object bar() {
        return new MyString("Bar");
    }

    public Object identity(Object o) {
        return o;
    }

    public void test() {
        Trace.print(foo());
        Object b = bar();
        Trace.print(identity(b));
    }
}

class MyString {
    String string;

    public MyString(String string) {
        this.string = new String(string);
    }

    public String toString() {
        return string;
    }
}

public class Test0 {

    public static void main(String args[]) {
        (new TestAux0()).test();
    }
}