package com.asolutions.scmsshd.util;

/**
 * @author Oleg Ilyenko
 */
public final class Tuple<A, B> {

    private A a;

    private B b;

    private Tuple(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public static <A, B> Tuple<A, B> valueOf(A a, B b) {
        return new Tuple<A, B>(a, b);
    }

    public A getA() {
        return a;
    }

    public B getB() {
        return b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Tuple tuple = (Tuple) o;

        if (a != null ? !a.equals(tuple.a) : tuple.a != null) return false;
        if (b != null ? !b.equals(tuple.b) : tuple.b != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = a != null ? a.hashCode() : 0;
        result = 31 * result + (b != null ? b.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "(" + a + ", " + b + ')';
    }
}
