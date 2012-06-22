/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
import org.eclipse.persistence.testing.models.employee.domain.*;

public class CheckAreVectorTypesAssignableTest extends AutoVerifyTestCase {
    Exception e;
    Vector v1;
    Vector v2;
    boolean test1ResultIsTrue = false;

    public CheckAreVectorTypesAssignableTest() {
        setDescription("Test of Helper.areTypesAssignable(Vector types1, Vector types2).");
    }

    public void reset() {
        v1 = null;
        v2 = null;
    }

    public void setup() {
        v1 = new Vector();
        v1.addElement(Integer.class);

        v2 = new Vector();
        v2.addElement(Employee.class);
    }

    public void test() {
        try {
            test1ResultIsTrue = Helper.areTypesAssignable(v1, v2);

        } catch (Exception e) {
            this.e = e;
            throw new TestErrorException("An exception should not have been thrown when checking if vectors are assignable.");
        }
    }

    public void verify() {
        if (test1ResultIsTrue) {
            throw new TestErrorException("An exception should not have been thrown when checking if vectors are assignable.");
        }
        if (e != null) {
            throw new TestErrorException("An exception should not have been thrown when checking if vectors are assignable: " + e.toString());
        }
    }
}
