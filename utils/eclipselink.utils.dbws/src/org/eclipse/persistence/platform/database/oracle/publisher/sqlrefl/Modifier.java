package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

/**
 * The Modifier class provides static methods and constants to decode class and member access
 * modifiers. It extends the java.lang.reflect.Modifier class to support additional modifiers for
 * other languages.
 */
public class Modifier extends java.lang.reflect.Modifier

/*
 * The inherited Java modifiers are: PUBLIC PRIVATE PROTECTED STATIC FINAL SYNCHRONIZED VOLATILE
 * TRANSIENT NATIVE INTERFACE ABSTRACT
 */{
    public static final int MAP = 0x80000000;
    public static final int ORDER = 0x40000000;
    public static final int INCOMPLETE = 0x20000000;

    public static boolean isMap(int mod) {
        // INFEASIBLE
        return ((mod & Modifier.MAP) != 0);
    }

    public static boolean isOrder(int mod) {
        // INFEASIBLE
        return ((mod & Modifier.ORDER) != 0);
    }

    public static boolean isIncomplete(int mod) {
        return ((mod & Modifier.INCOMPLETE) != 0);
    }
}
