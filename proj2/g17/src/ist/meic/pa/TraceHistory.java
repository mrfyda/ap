package ist.meic.pa;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TraceHistory {

    public static HashMap<Object, List<TraceStep>> traceSteps = new HashMap<Object, List<TraceStep>>();

    public static void putLTR(Object[] objects, String file, String method, int line) {
        for (Object object : objects) {
            put(object, "->", file, method, line);
        }
    }

    public static void putRTL(Object object, String file, String method, int line) {
        put(object, "<-", file, method, line);
    }

    public static void put(Object object, String direction, String file, String method, int line) {
        List<TraceStep> steps = traceSteps.get(object);

        if (steps == null) {
            steps = new ArrayList<TraceStep>();
        }

        TraceStep step = new TraceStep(direction, method, file, line);
        steps.add(step);
        traceSteps.put(object, steps);
    }
}
