package ist.meic.pa.shell.command;

import ist.meic.pa.shell.Shell;

public interface ICommand {

    void execute(Shell shell) throws TerminateInspectionException;

    void undo();

}
