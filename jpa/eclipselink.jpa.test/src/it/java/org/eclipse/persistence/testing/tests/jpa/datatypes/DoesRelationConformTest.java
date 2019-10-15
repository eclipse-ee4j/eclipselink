/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
//     tware - Bug 326104 - in-memory query BigDecimal equality comparation fails
package org.eclipse.persistence.testing.tests.jpa.datatypes;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.persistence.expressions.ExpressionOperator;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;

public class DoesRelationConformTest extends JUnitTestCase {

    public DoesRelationConformTest() {
        super();
    }

    public DoesRelationConformTest(String name) {
        super(name);
    }

    // Bug 326104
    public void testDoesRelationConform(){
        ExpressionOperator equal = new ExpressionOperator();
        equal.setSelector(ExpressionOperator.Equal);
        ExpressionOperator notEqual = new ExpressionOperator();
        notEqual.setSelector(ExpressionOperator.NotEqual);

        BigDecimal bd1 = new BigDecimal("1.0");
        BigDecimal bd2 = new BigDecimal("1");
        assertTrue("Big Decimals of different scales conform incorrectly for Equals.", equal.doesRelationConform(bd1, bd2));

        assertFalse("Big Decimals of different scales conform incorrectly for NotEquals.", notEqual.doesRelationConform(bd1, bd2));


        Double d1 = Double.NaN;
        Float f1 = Float.NaN;
        assertTrue("NaN of different types  conform incorrectly for Equals.", equal.doesRelationConform(d1, f1));

        assertFalse("NaN of different types  conform incorrectly for NotEquals.", notEqual.doesRelationConform(d1, f1));

    }
}
