package com.matemeup.matemeup.entities;

public class Triple<A, B, C> extends Pair<A, B> {
    public C third;

    public Triple(A a, B b, C c)
    {
        super(a, b);
        third = c;
    }
}
