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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
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
        example.aCharacter = new Character('t');
        example.anInteger = new Integer(1);
        example.aFloat = new Float(1.0);
        example.aBoolean = new Boolean(false);
        example.aLong = new Long(1L);
        example.aDouble = new Double(1.0);
        example.aByte = new Byte((byte)1);
        example.aShort = new Short((short)1);
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
        example.aByteArray = new Byte[] { new Byte("4"), new Byte("5"), new Byte("6") };
        example.aCharacterArray = new Character[] { new Character('C'), new Character('H'), new Character('A') };

        return example;
    }
}
