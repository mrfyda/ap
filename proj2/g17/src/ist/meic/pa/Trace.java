package ist.meic.pa;

import java.util.List;

public class Trace {

    public static void print(Object object) {
        List<TraceStep> steps = TraceHistory.traceSteps.get(object);

        if (steps == null) {
            System.out.println("Tracing for " + object + " is nonexistent!");
        } else {
            System.out.println("Tracing for " + object);

            for (TraceStep step : steps) {
                System.out.println(step);
            }
        }
    }

}
