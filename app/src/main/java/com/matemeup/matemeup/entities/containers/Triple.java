package com.matemeup.matemeup.entities.containers;

import com.matemeup.matemeup.entities.containers.Pair;

public class Triple<A, B, C> extends Pair<A, B> {
    public C third;

    public Triple(A a, B b, C c)
    {
        super(a, b);
        third = c;
    }
}
