package ist.meic.pa;

public class TraceStep {
    public String file;
    public String method;
    public Integer line;

    public TraceStep(String file, String method, Integer line) {
        this.file = file;
        this.method = method;
        this.line = line;
    }

    @Override
    public String toString() {
        return method + " on " + file + ":" + line;
    }
}
