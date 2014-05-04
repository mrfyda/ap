package ist.meic.pa;

import ist.meic.pa.translator.editor.TraceExprEditor;
import ist.meic.pa.vm.TraceRunner;

public class TraceVM {

    public static void main(String[] args) {
        TraceRunner traceRunner = new TraceRunner(new TraceExprEditor());
        traceRunner.run(args);
    }

}
