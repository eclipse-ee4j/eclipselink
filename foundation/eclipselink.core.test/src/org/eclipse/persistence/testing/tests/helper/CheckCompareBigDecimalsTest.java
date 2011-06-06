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
import java.math.BigDecimal;

public class CheckCompareBigDecimalsTest extends AutoVerifyTestCase {
    Exception e;
    BigDecimal bd1;
    BigDecimal bd2;
    BigDecimal bd3;
    boolean test1ResultIsTrue;
    boolean test2ResultIsTrue;
    boolean test3ResultIsTrue;
    boolean test4ResultIsTrue;

    public CheckCompareBigDecimalsTest() {
        setDescription("Test of Helper.compareBigDecimals(java.math.BigDecimal one, java.math.BigDecimal two) when neither of BigDecimals is positive or negative infinity.");
    }

    public void reset() {
        bd1 = null;
        bd2 = null;
        bd3 = null;
    }

    public void setup() {
        bd1 = new BigDecimal(1);
        bd1.setScale(1);
        bd2 = new BigDecimal(-2);
        bd2.setScale(2);
        bd3 = new BigDecimal(1);
        bd3.setScale(3);

    }

    public void test() {
        try {
            test1ResultIsTrue = Helper.compareBigDecimals(bd1, bd2);
            test2ResultIsTrue = Helper.compareBigDecimals(bd1, bd3);

        } catch (Exception e) {
            this.e = e;
            throw new TestErrorException("An exception should not have been thrown when comparing BigDecimal objects - when neither of the arguments is negative or positive infinity.");
        }
    }

    public void verify() {
        if (test1ResultIsTrue) {
            throw new TestErrorException("Helper.compareBigDecimals(java.math.BigDecimal one, java.math.BigDecimal two) - with two non-infinity but different argurments - returns incorrectly.");
        }
        if (!test2ResultIsTrue) {
            throw new TestErrorException("Helper.compareBigDecimals(java.math.BigDecimal one, java.math.BigDecimal two) - with two non-infinity but identical argurments - returns incorrectly.");
        }
        if (e != null) {
            throw new TestErrorException("An exception should not have been thrown when comparing BigDecimal objects - when neither of the arguments is negative or positive infinity: " + e.toString());
        }
    }
}
