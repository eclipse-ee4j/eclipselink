/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.helper;

import org.eclipse.persistence.internal.helper.Helper;
import org.eclipse.persistence.testing.framework.*;
import java.math.BigDecimal;
import java.util.*;

public class BasicTest extends AutoVerifyTestCase {
    public static void main(String[] args) throws Throwable {
        BasicTest test = new BasicTest();
        test.setup();
        test.test();
        test.verify();
        test.reset();
    }

    public BasicTest() {
        super();
        setDescription("Test some basic functions of Helper");
    }

    public void test() {
        if (Helper.compareBigDecimals(new BigDecimal(0.01), new BigDecimal(0.001))) {
            throw new TestErrorException("Failed to compare two different BigDecimal numbers.");
        }
        if (Helper.compareBigDecimals(new BigDecimal(1.01), new BigDecimal(01.001))) {
            throw new TestErrorException("Failed to compare two equal BigDecimal numbers.");
        }

        String path = "c:\\test1\\test2\\test3\\";
        String expectedString = "c:\\\\test1\\\\test2\\\\test3\\\\";
        if (!Helper.doubleSlashes(path).equals(expectedString)) {
            throw new TestErrorException("Failed to replace single slash with double slashes from the String.");
        }

        String vowels = "lalelilolule";
        expectedString = "llllll";
        if (!Helper.removeVowels(vowels).equals(expectedString)) {
            throw new TestErrorException("Failed to remove vowels from String.");
        }

        String excessCharacterString = "1x2x3x";
        expectedString = "123x";
        if (!Helper.removeCharacterToFit(excessCharacterString, 'x', 4).equals(expectedString)) {
            throw new TestErrorException("Failed to remove Character to fit String.");
        }

        Helper.printTimeFromMilliseconds(100);
        Helper.printTimeFromMilliseconds(10000);
        Helper.printTimeFromMilliseconds(100000);

        if (!(Helper.getInstanceFromClass(this.getClass()) instanceof BasicTest)) {
            throw new TestErrorException("Failed to get instance from Class.");
        }

        if (!(Helper.isPrimitiveWrapper(Character.class) && Helper.isPrimitiveWrapper(Boolean.class) && Helper.isPrimitiveWrapper(Byte.class) && Helper.isPrimitiveWrapper(Short.class) && Helper.isPrimitiveWrapper(Integer.class) && Helper.isPrimitiveWrapper(Long.class) && Helper.isPrimitiveWrapper(Float.class) && Helper.isPrimitiveWrapper(Double.class))) {
            throw new TestErrorException("Failed to check if a class is a primitive wrapper.");
        }

        java.util.Vector aVector = new java.util.Vector();
        Object elem = new BasicTest();
        aVector.addElement(elem);

        if (!Helper.makeVectorFromObject(aVector).equals(aVector)) {
            throw new TestErrorException("Failed to make a java.util.Vector from a java.util.Vector.");
        }

        HashSet set = new HashSet();
        set.add(elem);
        if (!Helper.makeVectorFromObject(set).equals(aVector)) {
            throw new TestErrorException("Failed to make a java.util.Vector from a java.util.Set.");
        }

        aVector.add(null);
        if (!Helper.removeNullElement(aVector)) {
            throw new TestErrorException("Failed to remove the first null element from java.util.Vector");
        }

        aVector.clear();
        for (int i = 0; i < 3; i++) {
            aVector.add(i, new Integer(i));
        }

        Vector reverseVector = Helper.reverseVector(aVector);
        for (int i = 0; i < 3; i++) {
            if (((Integer)reverseVector.elementAt(i)).intValue() != (2 - i)) {
                throw new TestErrorException("Failed to reverse elements of java.util.Vector");
            }
        }
    }
}
