/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.tests.junit.helper;

import java.math.BigDecimal;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import org.eclipse.persistence.internal.helper.Helper;
import org.junit.Assert;
import org.junit.Test;

public class HelperTest {

    @Test
    public void basicTest() {
        Assert.assertFalse("Failed to compare two different BigDecimal numbers.",
                Helper.compareBigDecimals(new BigDecimal(0.01), new BigDecimal(0.001)));
        Assert.assertFalse("Failed to compare two equal BigDecimal numbers.",
                Helper.compareBigDecimals(new BigDecimal(1.01), new BigDecimal(01.001)));

        Assert.assertEquals("Failed to replace single slash with double slashes from the String.",
                "c:\\\\test1\\\\test2\\\\test3\\\\", Helper.doubleSlashes("c:\\test1\\test2\\test3\\"));

        Assert.assertEquals("Failed to remove vowels from String.",
                "llllll", Helper.removeVowels("lalelilolule"));

        Assert.assertEquals("Failed to remove Character to fit String.",
                "123x", Helper.removeCharacterToFit("1x2x3x", 'x', 4));

        Helper.printTimeFromMilliseconds(100);
        Helper.printTimeFromMilliseconds(10000);
        Helper.printTimeFromMilliseconds(100000);

        Assert.assertTrue("Failed to get instance from Class.", Helper.getInstanceFromClass(this.getClass()) instanceof HelperTest);

        Assert.assertTrue("Failed to check if Character.class is a primitive wrapper.", Helper.isPrimitiveWrapper(Character.class));
        Assert.assertTrue("Failed to check if Boolean.class is a primitive wrapper.", Helper.isPrimitiveWrapper(Boolean.class));
        Assert.assertTrue("Failed to check if Byte.class is a primitive wrapper.", Helper.isPrimitiveWrapper(Byte.class));
        Assert.assertTrue("Failed to check if Short.class is a primitive wrapper.", Helper.isPrimitiveWrapper(Short.class));
        Assert.assertTrue("Failed to check if Integer.class is a primitive wrapper.", Helper.isPrimitiveWrapper(Integer.class));
        Assert.assertTrue("Failed to check if Long.class is a primitive wrapper.", Helper.isPrimitiveWrapper(Long.class));
        Assert.assertTrue("Failed to check if Float.class is a primitive wrapper.", Helper.isPrimitiveWrapper(Float.class));
        Assert.assertTrue("Failed to check if Double.class is a primitive wrapper.", Helper.isPrimitiveWrapper(Double.class));

        Vector<Object> aVector = new Vector<Object>();
        Object elem = new String("dummy");
        aVector.addElement(elem);
        Assert.assertEquals("Failed to make a java.util.Vector from a java.util.Vector.",
                aVector, Helper.makeVectorFromObject(aVector));

        HashSet set = new HashSet();
        set.add(elem);
        Assert.assertEquals("Failed to make a java.util.Vector from a java.util.Set.",
                aVector, Helper.makeVectorFromObject(set));

        aVector.clear();
        for (int i = 0; i < 3; i++) {
            aVector.add(i, new Integer(i));
        }

        Vector reverseVector = Helper.reverseVector(aVector);
        for (int i = 0; i < 3; i++) {
            Assert.assertEquals("Failed to reverse elements of java.util.Vector",
                    2 - i, ((Integer)reverseVector.elementAt(i)).intValue());
        }
    }

    @Test
    public void checkAreVectorTypesAssignableTest() {
        Vector v1 = new Vector();
        v1.addElement(Integer.class);
        Vector v2 = new Vector();
        v2.addElement(String.class);
        Assert.assertFalse("An exception should not have been thrown when checking if vectors are assignable.",
                Helper.areTypesAssignable(v1, v2));
    }

    @Test
    public void checkAreVectorTypesAssignableWithNullVectorTest() {
        Vector v1 = new Vector();
        v1.addElement(new Integer(1));
        Assert.assertFalse("An exception should not have been thrown when checking if vectors are assignable - when one of the vectors is null.",
                Helper.areTypesAssignable(v1, null));
    }

    @Test
    public void checkClassIsSubclassWithNullSuperclassTest() {
        Assert.assertFalse("Helper.classIsSubclass(Class subClass, Class superClass) does not recognize that parent class is null.",
                Helper.classIsSubclass(HashMap.class, null));
    }

    @Test
    public void checkCompareBigDecimalsTest() {
        BigDecimal bd1 = new BigDecimal(1);
        bd1.setScale(1);
        BigDecimal bd2 = new BigDecimal(-2);
        bd2.setScale(2);
        BigDecimal bd3 = new BigDecimal(1);
        bd3.setScale(3);

        Assert.assertFalse("Helper.compareBigDecimals(java.math.BigDecimal one, java.math.BigDecimal two) - with two non-infinity but different argurments - returns incorrectly.",
                Helper.compareBigDecimals(bd1, bd2));
        Assert.assertTrue("Helper.compareBigDecimals(java.math.BigDecimal one, java.math.BigDecimal two) - with two non-infinity but identical argurments - returns incorrectly.",
                Helper.compareBigDecimals(bd1, bd3));
    }

    @Test
    public void checkCompareByteArraysWithDifferentElementsTest() {
        byte[] b1 = "12345".getBytes();
        byte[] b2 = "12346".getBytes();
        Assert.assertFalse("Helper.compareByteArrays(b1,b2) when comparing byte arrays with different elements.",
                Helper.compareByteArrays(b1, b2));
    }

    @Test
    public void compareArrayContentTest() {
        Integer[] array1 = new Integer[3];
        Integer[] array2 = new Integer[3];
        Integer[] array3 = new Integer[3];
        for (int count = 0; count < 3; count++) {
            Integer counter = new Integer(count);
            Integer counter2 = new Integer(count + 9);
            array1[count] = counter;
            array2[count] = counter;
            array3[count] = counter2;
        }

        Assert.assertTrue("Helper.compareArrays(Object[] array1, Object[] array2) does not recognize that object arrays contain identical elements.",
                Helper.compareArrays(array1, array2));
        Assert.assertFalse("Helper.compareArrays(Object[] array1, Object[] array2) does not recognize that object arrays contain different elements.",
                Helper.compareArrays(array1, array3));
    }

    @Test
    public void compareArrayLengthTest() {
        Integer[] array1 = new Integer[2];
        Integer[] array2 = new Integer[2];
        Integer[] array3 = new Integer[3];
        for (int count = 0; count < 2; count++) {
            Integer counter = new Integer(count);
            array1[count] = counter;
            array2[count] = counter;
            array3[count] = counter;
        }
        array3[2] = new Integer(10);

        Assert.assertTrue("Helper.compareArrays(Object[] array1, Object[] array2) does not recognize that object arrays are of same length.",
                Helper.compareArrays(array1, array2));
        Assert.assertFalse("Helper.compareArrays(Object[] array1, Object[] array2) does not recognize that object arrays are of different length.",
                Helper.compareArrays(array1, array3));
    }

    @Test
    public void compareCharArrayContentTest() {
        char[] array1 = { 'a', 'b', 'c' };
        char[] array2 = { 'a', 'b', 'c' };
        char[] array3 = { 'x', 'y', 'z' };

        Assert.assertTrue("Helper.compareCharArrays(char[] array1, char[] array2) does not recognize that arrays contain the same elements.",
                Helper.compareCharArrays(array1, array2));
        Assert.assertFalse("Helper.compareCharArrays(char[] array1, char[] array2) does not recognize that arrays contain different elements.",
                Helper.compareCharArrays(array1, array3));
    }

    @Test
    public void compareCharArrayLengthTest() {
        char[] array1 = { 'a', 'b' };
        char[] array2 = { 'a', 'b' };
        char[] array3 = { 'a', 'b', 'c' };

        Assert.assertTrue("Helper.compareCharArrays(char[] array1, char[] array2) does not recognize that arrays are of same length.",
                Helper.compareCharArrays(array1, array2));
        Assert.assertFalse("Helper.compareCharArrays(char[] array1, char[] array2) does not recognize that arrays are of different length.",
                Helper.compareCharArrays(array1, array3));
    }

    @Test
    public void timeFromDateTest() {
        boolean optimizedDatesState = Helper.shouldOptimizeDates();
        try {
            Date testDate = Helper.utilDateFromLong(new Long(System.currentTimeMillis()));
            String testTime = new Time(testDate.getTime()).toString();

            Helper.setShouldOptimizeDates(false);
            Assert.assertEquals("Failed to convert java.util.Date to java.sql.Time when shouldOptimizedDates is off",
                    testTime, Helper.timeFromDate(testDate).toString());

            Helper.setShouldOptimizeDates(true);
            Assert.assertEquals("Failed to convert java.util.Date to java.sql.Time when shouldOptimizedDates is on",
                    testTime, Helper.timeFromDate(testDate).toString());
        } finally {
            Helper.setShouldOptimizeDates(optimizedDatesState);
        }
    }

    @Test
    public void TimeFromLongTest() {
        boolean optimizedDatesState = Helper.shouldOptimizeDates();
        try {
            Long currentTime = new Long(System.currentTimeMillis());
            Time expectedTestTime = new Time(currentTime.longValue());

            Helper.setShouldOptimizeDates(false);
            Time actualTime = Helper.timeFromLong(currentTime);
            Assert.assertEquals("Failed to convert Long to java.sql.Time when shouldOptimizedDates is off",
                    expectedTestTime, actualTime);
            Assert.assertEquals("Failed to convert Long to java.sql.Time when shouldOptimizedDates is off",
                    expectedTestTime.toString(), actualTime.toString());

            Helper.setShouldOptimizeDates(true);
            actualTime = Helper.timeFromLong(currentTime);
            Assert.assertEquals("Failed to convert Long to java.sql.Time when shouldOptimizedDates is on",
                    expectedTestTime, actualTime);
            Assert.assertEquals("Failed to convert Long to java.sql.Time when shouldOptimizedDates is on",
                    expectedTestTime.toString(), actualTime.toString());
        } finally {
            Helper.setShouldOptimizeDates(optimizedDatesState);
        }
    }

    @Test
    public void timeFromStringTest() {
        boolean optimizedDatesState = Helper.shouldOptimizeDates();
        try {
            String currentTime = new Time(System.currentTimeMillis()).toString();

            Helper.setShouldOptimizeDates(false);
            Assert.assertEquals("Failed to convert String to java.sql.Time when shouldOptimizedDates is off",
                    currentTime, Helper.timeFromString(currentTime).toString());

            Helper.setShouldOptimizeDates(true);
            Assert.assertEquals("Failed to convert String to java.sql.Time when shouldOptimizedDates is on",
                    currentTime, Helper.timeFromString(currentTime).toString());
        } finally {
            Helper.setShouldOptimizeDates(optimizedDatesState);
        }
    }

    @Test
    public void timestampFromDateTest() {
        boolean optimizedDatesState = Helper.shouldOptimizeDates();
        try {
            Date currentTime = Helper.utilDateFromLong(new Long(System.currentTimeMillis()));
            String testTime = new Timestamp(currentTime.getTime()).toString();

            Helper.setShouldOptimizeDates(true);
            Assert.assertEquals("Failed to convert java.util.Date to java.sql.Timestamp when shouldOptimizedDates is on",
                    testTime, Helper.timestampFromDate(currentTime).toString());
        } finally {
            Helper.setShouldOptimizeDates(optimizedDatesState);
        }
    }

    @Test
    public void timestampFromLongTest() {
        boolean optimizedDatesState = Helper.shouldOptimizeDates();
        try {
            Long currentTime = new Long(System.currentTimeMillis());
            String testTime = new Timestamp(currentTime.longValue()).toString();

            Helper.setShouldOptimizeDates(true);
            Assert.assertEquals("Failed to convert Long to java.sql.Timestamp when shouldOptimizedDates is on",
                    testTime, Helper.timestampFromLong(currentTime).toString());
        } finally {
            Helper.setShouldOptimizeDates(optimizedDatesState);
        }
    }

    @Test
    public void timestampFromStringTest() {
        boolean optimizedDatesState = Helper.shouldOptimizeDates();
        try {
            Helper.setShouldOptimizeDates(true);
            
            String currentTime = new Timestamp(System.currentTimeMillis()).toString();
            Assert.assertEquals("Failed to convert String to java.sql.Timestamp when shouldOptimizedDates is on",
                    currentTime, Helper.timestampFromString(currentTime).toString());

            Assert.assertEquals("Failed to convert String to java.sql.Timestamp when shouldOptimizedDates is on",
                    Timestamp.valueOf("2018-01-01 16:24:33.00013"), Helper.timestampFromString("2018-01-01 16:24:33.00013+02"));

            Assert.assertEquals("Failed to convert String to java.sql.Timestamp when shouldOptimizedDates is on",
                    Timestamp.valueOf("2018-01-01 16:24:33.0"), Helper.timestampFromString("2018-01-01 16:24:33+02"));

            Assert.assertEquals("Failed to convert String to java.sql.Timestamp when shouldOptimizedDates is on",
                    Timestamp.valueOf("2018-01-01 16:24:33.1"), Helper.timestampFromString("2018-01-01 16:24:33.1+02"));

            Assert.assertEquals("Failed to convert String to java.sql.Timestamp when shouldOptimizedDates is on",
                    Timestamp.valueOf("2018-01-01 16:24:33.0"), Helper.timestampFromString("2018-01-01 16:24:33.0000000+02"));

            Assert.assertEquals("Failed to convert String to java.sql.Timestamp when shouldOptimizedDates is on",
                    Timestamp.valueOf("2018-01-01 16:24:33.0"), Helper.timestampFromString("2018-01-01 16:24:33.0000000"));

            Assert.assertEquals("Failed to convert String to java.sql.Timestamp when shouldOptimizedDates is on",
                    Timestamp.valueOf("2018-01-01 16:24:33.0"), Helper.timestampFromString("2018-01-01 16:24:33"));

        } finally {
            Helper.setShouldOptimizeDates(optimizedDatesState);
        }
    }
}
