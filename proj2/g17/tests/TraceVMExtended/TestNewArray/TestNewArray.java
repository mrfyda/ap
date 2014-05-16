import ist.meic.pa.Trace;

class TestNewArrayAux {

    public void test() {
        String[] strArray1 = {"Bar", "Baz"};
        String[] strArray2 = new String[2];

        Trace.print(strArray1);
        Trace.print(strArray2);
    }
}

public class TestNewArray {
    public static void main(String args[]) {
        (new TestNewArrayAux()).test();
    }
}
