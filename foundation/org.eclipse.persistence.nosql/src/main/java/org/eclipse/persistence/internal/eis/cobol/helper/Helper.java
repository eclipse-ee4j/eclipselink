/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.internal.eis.cobol.helper;


/**
* <b>Purpose</b>: This class contains some simple methods that are helpful and used through
* the classes
*/
public class Helper {

    /** takes a string as an argument and returns the <code>Integer</code> value */
    public static Integer integerFromString(String string) {
        Integer intValue;
        try {
            intValue = Integer.valueOf(string);
        } catch (NumberFormatException exception) {
            return null;
        }
        return intValue;
    }

    /** takes a hex string representation and returns the Integer value */
    public static Integer integerFromHexString(String string) {
        Integer intValue;
        try {
            intValue = Integer.valueOf(string, 16);
        } catch (NumberFormatException exception) {
            return null;
        }
        return intValue;
    }

    public static byte byteFromString(String string) {
        return Byte.valueOf(string).byteValue();
    }

    /** takes a hex string and returns an int value */
    public static int intFromHexString(String string) {
        return integerFromHexString(string).intValue();
    }

    /** takes a byte and returns the Integer value */
    public static Integer integerFromByte(byte byteValue) {
        return Integer.valueOf(intFromByte(byteValue));
    }

    /** takes a byte value and returns int value */
    public static int intFromByte(byte byteValue) {
        Byte bigByte = Byte.valueOf(byteValue);
        return bigByte.intValue();
    }

    /** takes a int an returns a byte */
    public static byte byteFromInt(int intValue) {
        return Integer.valueOf(intValue).byteValue();
    }

    /** calculates a exponential value give the base and power */
    public static int power(int base, int power) {
        int total = base;
        if (power == 0) {
            return 1;
        } else if (power == 1) {
            return base;
        } else {
            for (int i = 1; i < power; i++) {
                total *= base;
            }
        }
        return total;
    }
}
