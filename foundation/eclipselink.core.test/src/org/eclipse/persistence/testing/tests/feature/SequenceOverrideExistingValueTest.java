/*******************************************************************************
 * Copyright (c) 1998, 2008 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.feature;

import java.math.*;
import org.eclipse.persistence.testing.framework.*;

public class SequenceOverrideExistingValueTest extends AutoVerifyTestCase {
    public SequenceOverrideExistingValueTest() {
        super();
    }

    String hugeNumberStr = "1000000000000000000000000551";
    boolean okBigDecimal;
    boolean okBigInteger;
    boolean okString;

    public void setup() {
        if (getSession().getPlatform().getDefaultSequence().shouldAcquireValueAfterInsert()) {
            throw new TestWarningException("This test doesn't work with 'after-insert' native sequencing");
        }
    }

    public void test() {
        BigDecimal bigDecimal = new BigDecimal(hugeNumberStr);
        okBigDecimal = !getSession().getPlatform().getDefaultSequence().shouldOverrideExistingValue(null, bigDecimal);

        BigInteger bigInteger = new BigInteger(hugeNumberStr);
        okBigInteger = !getSession().getPlatform().getDefaultSequence().shouldOverrideExistingValue(null, bigInteger);

        okString = !getSession().getPlatform().getDefaultSequence().shouldOverrideExistingValue(null, hugeNumberStr);
    }

    public void verify() {
        if (!okBigDecimal) {
            throw new TestErrorException("Sequencing has overridden positive BigDecimal");
        }
        if (!okBigInteger) {
            throw new TestErrorException("Sequencing has overridden positive BigInteger");
        }
        if (!okString) {
            throw new TestErrorException("Sequencing has overridden String");
        }
    }
}