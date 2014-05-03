package ist.meic.pa;

public class TraceVMExtended {

    public static void main(String[] args) {
        TraceMain traceMain = new TraceMain(new TraceTranslatorExtended());
        traceMain.run(args);
    }

}
