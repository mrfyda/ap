package ist.meic.pa;

import ist.meic.pa.translator.editor.TraceExprEditorExtended;
import ist.meic.pa.vm.TraceRunner;

public class TraceVMExtended {

    public static void main(String[] args) {
        TraceRunner traceRunner = new TraceRunner(new TraceExprEditorExtended());
        traceRunner.run(args);
    }

}
