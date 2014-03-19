package ist.meic.pa.shell.command;

import java.util.Arrays;

import ist.meic.pa.Inspector;
import ist.meic.pa.shell.Shell;

public class m implements ICommand {
	
	 private final static String DESCRIPTION = "";
	 
	 private final static Integer NUM_PARAMS = 2;
	 
	 private String fieldName;
	 
	 private String fieldValue;
	 
	 public m(String[] args) {
	        if (args.length >= NUM_PARAMS) {
	            this.fieldName = args[0];
	            this.fieldValue = args[1];
	        } else {
	            throw new IllegalArgumentException();
	        }
	    }
	 
	
	public void execute(Shell shell) {
		  Object object = shell.getLatestObject();

		  Inspector inspector = new Inspector();
		  inspector.modifyField(fieldName, fieldValue, object);	  
	}

}
