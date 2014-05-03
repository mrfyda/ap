package ist.meic.pa;

import java.util.ArrayList;
import java.util.IdentityHashMap;
import java.util.List;

public class TraceHistory {

    public static IdentityHashMap<Object, List<TraceStep>> traceSteps = new IdentityHashMap<Object, List<TraceStep>>();

    public static void putLTR(Object[] objects, String file, String method, int line) {
        for (Object object : objects) {
            put(object, "->", file, method, line);
        }
    }

    public static void putRTL(Object object, String file, String method, int line) {
        put(object, "<-", file, method, line);
    }

    public static void putFieldWrite(Object object, String file, String fieldName, int line) {
        put(object, "Wrote ", file, fieldName, line);
    }

    public static void putFieldRead(Object object, String file, String fieldName, int line) {
        put(object, "Read ", file, fieldName, line);
    }

    public static void putHandler(Object object, String file, String exceptioName, int line) {
        put(object, "Catched ", file, exceptioName, line);
    }

    public static void putCast(Object object, String file, String className, int line) {
        put(object, "Cast to", file, className, line);
    }

    public static void putInstanceOf(Object object, String file, String className, int line) {
        put(object, "Checked instance of", file, className, line);
    }

    public static void putNewArray(Object object, String file, String className, int line) {
        put(object, "New array of ", file, className, line);
    }

    private static void put(Object object, String direction, String file, String method, int line) {
        List<TraceStep> steps = traceSteps.get(object);

        if (steps == null) {
            steps = new ArrayList<TraceStep>();
        }

        TraceStep step = new TraceStep(direction, method, file, line);
        steps.add(step);
        traceSteps.put(object, steps);
    }

    public static void printTraceSteps(Object object) {
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

class TraceStep {
    private String direction;
    private String method;
    private String file;
    private Integer line;

    public TraceStep(String direction, String method, String file, Integer line) {
        this.direction = direction;
        this.file = file;
        this.method = method;
        this.line = line;
    }

    @Override
    public String toString() {
        return String.format("  %s %s on %s:%d", direction, method, file, line);
    }
}
