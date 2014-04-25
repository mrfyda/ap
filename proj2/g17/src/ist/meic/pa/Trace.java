package ist.meic.pa;

public class Trace {

    public static void print(Object object) {
        System.out.println("Tracing for " + object);

        System.out.println(TraceHistory.traceSteps.get(object));
    }

}
