package com.asolutions.scmsshd.util;

/**
 * @author Oleg Ilyenko
 */
public final class Tuple3<A, B, C> {

    private A a;

    private B b;

    private C c;

    private Tuple3(A a, B b, C c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public static <A, B, C> Tuple3<A, B, C> valueOf(A a, B b, C c) {
        return new Tuple3<A, B, C>(a, b, c);
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    public C getC() {
        return c;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple3 tuple3 = (Tuple3) o;

        if (a != null ? !a.equals(tuple3.a) : tuple3.a != null) return false;
        if (b != null ? !b.equals(tuple3.b) : tuple3.b != null) return false;
        if (c != null ? !c.equals(tuple3.c) : tuple3.c != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        result = 31 * result + (c != null ? c.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ", " + c + ')';
    }
}
