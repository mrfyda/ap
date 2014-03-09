package ap;

import java.util.Scanner;
import java.lang.reflect.Method;
import java.util.ArrayList;

class Shell {

    public static void main(String[] args) throws Exception {

        ShellStorage ss = new ShellStorage();

        while (true) {
	        System.out.print("Command:> ");
	        Scanner scanner = new Scanner(System.in);
	        String line = scanner.nextLine();
	        
	        String[] parts = line.split(" ");
	        String command = parts[0];
	        
	        if (command.equals("Class")) {
		        String arg = parts[1];
		        	        	
	        	Object c = Class.forName(arg).newInstance();
	        	
	        	ss.object = c;
	        	
	        	System.out.println("class " + ss.object.getClass().getName());
	        }
	        else if (command.equals("Set")) {
		        String arg = parts[1];
		        		        
	        	ss.objects.put(arg, ss.object);
	        	
	        	System.out.println("Saved name for object of type: " + ss.object.getClass().getName());
	        }
	        else if (command.equals("Get")) {
		        String arg = parts[1];
		        
		        ss.object = ss.objects.get(arg);
	        }
	        else if (command.equals("Index")) {
	        }
	        else if (command.equals("Exit")) {
	        	return;
	        }
	        else {       	
	        	try {
	        		Class<?>[] params = {};
	        		Method method = ss.object.getClass().getClass().getMethod(command);
	        		method.invoke(ss.object.getClass());
	        	}
	        	catch(Exception e) {
	        		e.printStackTrace();
	        	}
	        }
        }
    }

}


