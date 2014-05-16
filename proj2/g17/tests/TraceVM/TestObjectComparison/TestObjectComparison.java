import ist.meic.pa.Trace;

import java.util.HashMap;
import java.util.Map;

class TestObjectComparisonAux {

    Map m = new HashMap();

    public Object identity(Object o) {
        return o;
    }

    public void test() {
        Object o = new String("MyObj");

        identity(o);
        m.put(2, o);
        m.get(2);

        Trace.print(o);

        Object o1 = new String("MyObj");

        for (Object obj : m.values()) {
            System.out.println(obj);
        }

        Trace.print(o);
        Trace.print(o1);

    }
}

public class TestObjectComparison {
    public static void main(String args[]) {
        (new TestObjectComparisonAux()).test();
    }
}
