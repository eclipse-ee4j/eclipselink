package org.eclipse.persistence.testing.jaxb.dynamic.util;

import java.util.Vector;

public class MyList extends Vector {

    @Override
    public synchronized int capacity() {
        return 2;
    }

    @Override
    public synchronized String toString() {
        return "MyList: " + hashCode();
    }

}