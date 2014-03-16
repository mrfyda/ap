package ist.meic.pa;

import a.B;

public class App {

    public static void main(String[] args) throws IllegalAccessException {
        B b = new B();
        new Inspector().inspect(b);
    }

}
