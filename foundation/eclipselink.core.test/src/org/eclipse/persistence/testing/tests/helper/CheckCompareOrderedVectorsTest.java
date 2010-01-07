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

import java.util.Vector;
import org.eclipse.persistence.internal.helper.*;
import org.eclipse.persistence.testing.framework.*;

public class CheckCompareOrderedVectorsTest extends AutoVerifyTestCase {
    Exception e;
    Vector v1;
    Vector v2;
    boolean test1ResultIsTrue = false;

    public CheckCompareOrderedVectorsTest() {
        setDescription("Test of Helper.compareOrderedVectors(Vector vector1, Vector vector2) when vectors are identical.");
    }

    public void reset() {
        v1 = null;
        v2 = null;
    }

    public void setup() {
        v1 = new Vector();
        v1.insertElementAt(new Integer(1), 0);
        v1.insertElementAt(new Integer(2), 1);
        v1.insertElementAt(new Integer(3), 2);
        v2 = v1;
    }

    public void test() {
        try {
            test1ResultIsTrue = Helper.compareOrderedVectors(v1, v2);

        } catch (Exception e) {
            this.e = e;
            throw new TestErrorException("An exception should not have been thrown when comparing Vectors - when the Vectors are identical.");
        }
    }

    public void verify() {
        if (!test1ResultIsTrue) {
            throw new TestErrorException("Helper.compareOrderedVectors(v1, v2) does not recognize that Vectors are identical.");
        }
        if (e != null) {
            throw new TestErrorException("An exception should not have been thrown when comparing Vectors - when the Vectors are identical: " + e.toString());
        }
    }
}
