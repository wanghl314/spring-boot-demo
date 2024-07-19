package com.whl.spring.demo.others;

import java.util.Optional;

public class OptionalTests {

    public static void main(String[] args) {
        A a = new A(new B(null));
        System.out.println(Optional.of(a).map(A::getB).map(B::getC).map(C::getD).map(D::getI).orElse(999));
        a = new A(new B(new C(new D(111))));
        System.out.println(Optional.of(a).map(A::getB).map(B::getC).map(C::getD).map(D::getI).orElse(999));
        a = null;
        System.out.println(Optional.ofNullable(a).map(A::getB).map(B::getC).map(C::getD).map(D::getI).orElse(999));
    }

    static class A {
        private B b;

        public A(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    static class B {
        private C c;

        public B(C c) {
            this.c = c;
        }

        public C getC() {
            return c;
        }

        public void setC(C c) {
            this.c = c;
        }
    }

    static class C {
        private D d;

        public C(D d) {
            this.d = d;
        }

        public D getD() {
            return d;
        }

        public void setD(D d) {
            this.d = d;
        }
    }

    static class D {
        private int i;

        public D(int i) {
            this.i = i;
        }

        public int getI() {
            return i;
        }

        public void setI(int i) {
            this.i = i;
        }
    }

}
