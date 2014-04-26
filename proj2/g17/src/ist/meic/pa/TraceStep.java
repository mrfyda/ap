package ist.meic.pa;

public class TraceStep {
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
