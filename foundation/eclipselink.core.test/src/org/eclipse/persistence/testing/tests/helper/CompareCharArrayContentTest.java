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
package org.eclipse.persistence.testing.tests.helper;

import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.testing.framework.*;

public class CompareCharArrayContentTest extends AutoVerifyTestCase {
    Exception e;
    char[] array1 = { 'a', 'b', 'c' };
    char[] array2 = { 'a', 'b', 'c' };
    char[] array3 = { 'x', 'y', 'z' };
    boolean test1ResultIsTrue = false;
    boolean test2ResultIsTrue = false;

    public CompareCharArrayContentTest() {
        setDescription("Test of Helper.compareCharArrays(char[] array1, char[] array2) method's comparison of char array content.");
    }

    public void reset() {
        array1 = null;
        array2 = null;
        array3 = null;
    }

    public void setup() {
    }

    public void test() {
        try {
            test1ResultIsTrue = Helper.compareCharArrays(array1, array2);
            test2ResultIsTrue = Helper.compareCharArrays(array1, array3);

        } catch (Exception e) {
            this.e = e;
            throw new TestErrorException("An exception should not have been thrown when comparing char array content.");
        }
    }

    public void verify() {
        if (!test1ResultIsTrue) {
            throw new TestErrorException("Helper.compareCharArrays(char[] array1, char[] array2) does not recognize that arrays contain the same elements.");
        }

        if (test2ResultIsTrue) {
            throw new TestErrorException("Helper.compareCharArrays(char[] array1, char[] array2) does not recognize that arrays contain different elements.");
        }
        if (e != null) {
            throw new TestErrorException("An exception should not have been thrown when comparing char array content: " + e.toString());
        }
    }
}
