/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Mike Norman - from Proof-of-concept, become production code
package dbws.testing.shadowddlgeneration.oldjpub;

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
