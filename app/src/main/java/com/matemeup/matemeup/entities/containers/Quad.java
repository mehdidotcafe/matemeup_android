package com.matemeup.matemeup.entities.containers;

public class Quad<A, B, C, D> extends Triple<A, B, C> {
    public D fourth;

    public Quad(A a, B b, C c, D d)
    {
        super(a, b, c);
        fourth = d;
    }
}
