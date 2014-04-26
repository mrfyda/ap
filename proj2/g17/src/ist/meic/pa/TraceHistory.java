package ist.meic.pa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TraceHistory {

    public static HashMap<Object, List<TraceStep>> traceSteps = new HashMap<Object, List<TraceStep>>();

    public static void put(Object object, String file, String method, int line) {
        List<TraceStep> steps = traceSteps.get(object);

        if (steps == null) {
            steps = new ArrayList<TraceStep>();
        }

        TraceStep step = new TraceStep("<-", method, file, line);
        steps.add(step);
        traceSteps.put(object, steps);
    }
}
