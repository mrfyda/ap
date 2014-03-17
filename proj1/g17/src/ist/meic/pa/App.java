package ist.meic.pa;

import a.B;
import a.C;

public class App {

    public static void main(String[] args) throws IllegalAccessException {
        B b = new B();
        new Inspector().inspect(b);

        C c = new C();
        new Inspector().inspect(c);
    }

}
