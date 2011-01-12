/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Mike Norman - from Proof-of-concept, become production code
 ******************************************************************************/
package org.eclipse.persistence.platform.database.oracle.publisher.sqlrefl;

//javase imports
import java.lang.reflect.Modifier;

/**
 * The Modifier class provides static methods and constants to decode class and member access
 * modifiers. It extends the java.lang.reflect.Modifier class to support additional modifiers for
 * other languages.
 */
public class PublisherModifier extends Modifier

/*
 * The inherited Java modifiers are: PUBLIC PRIVATE PROTECTED STATIC FINAL SYNCHRONIZED VOLATILE
 * TRANSIENT NATIVE INTERFACE ABSTRACT
 */{
    public static final int MAP = 0x80000000;
    public static final int ORDER = 0x40000000;
    public static final int INCOMPLETE = 0x20000000;

    public static boolean isMap(int mod) {
        // INFEASIBLE
        return ((mod & PublisherModifier.MAP) != 0);
    }

    public static boolean isOrder(int mod) {
        // INFEASIBLE
        return ((mod & PublisherModifier.ORDER) != 0);
    }

    public static boolean isIncomplete(int mod) {
        return ((mod & PublisherModifier.INCOMPLETE) != 0);
    }
}
