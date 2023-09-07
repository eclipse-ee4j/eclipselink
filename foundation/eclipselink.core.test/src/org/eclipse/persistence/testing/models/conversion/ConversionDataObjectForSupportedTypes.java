/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.models.conversion;

import java.util.*;
import org.eclipse.persistence.internal.helper.Helper;

//This class creates data for DataTypesConvertedXX tests.
public class ConversionDataObjectForSupportedTypes extends ConversionDataObject {
    public String stringToTimestamp;
    public Byte[] aByteArray;
    public Character[] aCharacterArray;

    public static ConversionDataObjectForSupportedTypes example() {
        ConversionDataObjectForSupportedTypes example = new ConversionDataObjectForSupportedTypes();

        example.setAPCharArray(new char[] { 'a', 'b', 'c' });
        example.setAPByteArray(new byte[] { 1, 2, 3 });
        example.aCharacter = 't';
        example.anInteger = 1;
        example.aFloat = 1.0f;
        example.aBoolean = Boolean.FALSE;
        example.aLong = 1L;
        example.aDouble = 1.0;
        example.aByte = (byte) 1;
        example.aShort = (short)1;
        example.aBigDecimal = new java.math.BigDecimal(1.0);
        example.aBigInteger = new java.math.BigInteger("1");
        example.aNumber = example.aBigDecimal;
        example.anSQLDate = Helper.dateFromYearMonthDate(1903, 3, 3);
        Calendar c = Calendar.getInstance();
        c.set(1903, 3, 3);
        c.set(Calendar.MILLISECOND, 0);
        example.aJavaDate = c.getTime();
        example.aCalendar = Calendar.getInstance();
        example.aCalendar.set(1999, 06, 06, 0, 0, 0);
        example.aCalendar.set(Calendar.MILLISECOND, 0);
        example.aTime = Helper.timeFromHourMinuteSecond(3, 3, 3);
        example.aTimestamp = Helper.timestampFromYearMonthDateHourMinuteSecondNanos(1903, 3, 3, 3, 3, 3, 0);
        example.aString = new String("Conversion Managaer Test Example 1");
        example.stringToInt = new String("111");
        example.stringToTimestamp = new String("2003/11/23 23:45:56");
        example.aByteArray = new Byte[] { Byte.valueOf("4"), Byte.valueOf("5"), Byte.valueOf("6") };
        example.aCharacterArray = new Character[] { Character.valueOf('C'), Character.valueOf('H'), Character.valueOf('A') };

        return example;
    }
}
