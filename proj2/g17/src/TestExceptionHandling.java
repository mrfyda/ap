import ist.meic.pa.Trace;

class TestExceptionHandlingAux {

    Exception exception = new NullPointerException();

    public void foo() throws Exception {
        throw exception;
    }

    public void test() {
        try {
            foo();
        } catch (Exception ignore) {
        }

        Trace.print(exception);
    }
}

public class TestExceptionHandling {
    public static void main(String args[]) {
        (new TestExceptionHandlingAux()).test();
    }
}
