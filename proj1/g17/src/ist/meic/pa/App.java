package ist.meic.pa;

import a.B;
import a.C;
import a.R;
import a.Y;

public class App {

    public static void main(String[] args) throws IllegalAccessException {
        B b = new B();
        new Inspector().inspect(b);

        C c = new C();
        new Inspector().inspect(c);

        Y y = new Y();
        new Inspector().inspect(y);

        R r = new R();
        new Inspector().inspect(r);
    }

}
