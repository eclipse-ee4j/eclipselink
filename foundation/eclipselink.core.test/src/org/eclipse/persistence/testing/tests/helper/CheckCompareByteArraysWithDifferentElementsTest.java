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

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.testing.framework.*;

public class CheckCompareByteArraysWithDifferentElementsTest extends AutoVerifyTestCase {
    Exception e;
    String s1;
    String s2;
    byte[] b1;
    byte[] b2;
    boolean test1ResultIsTrue;

    public CheckCompareByteArraysWithDifferentElementsTest() {
        setDescription("Test of Helper.compareByteArrays(byte[] array1, byte[] array2) with byte arrays containing different elements");
    }

    public void setup() {
        s1 = "12345";
        s2 = "12346";
        b1 = s1.getBytes();
        b2 = s2.getBytes();
    }

    public void test() {
        try {
            test1ResultIsTrue = Helper.compareByteArrays(b1, b2);

        } catch (Exception e) {
            this.e = e;
            throw new TestErrorException("An exception should not have been thrown when comparing byte arrays with different elements.");
        }
    }

    public void reset() {
        s1 = null;
        s2 = null;
        b1 = null;
        b2 = null;
    }

    public void verify() {
        if (test1ResultIsTrue) {
            throw new TestErrorException("Helper.compareByteArrays(b1,b2) when comparing byte arrays with different elements.");
        }
        if (e != null) {
            throw new TestErrorException("An exception should not have been thrown when comparing byte arrays with different elements.: " + e.toString());
        }
    }
}
