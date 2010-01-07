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
import org.eclipse.persistence.testing.models.employee.domain.*;

public class CheckClassIsSubclassWithNullSuperclassTest extends AutoVerifyTestCase {
    Exception e;
    Project parent;
    LargeProject subclass;
    boolean test1ResultIsTrue = false;

    public CheckClassIsSubclassWithNullSuperclassTest() {
        setDescription("Test of Helper.classIsSubclass().");
    }

    public void reset() {
    }

    public void setup() {
    }

    public void test() {
        try {
            test1ResultIsTrue = Helper.classIsSubclass(LargeProject.class, null);

        } catch (Exception e) {
            this.e = e;
            throw new TestErrorException("An exception should not have been thrown when checking for status as a subclass when superclass is null.");
        }
    }

    public void verify() {
        if (test1ResultIsTrue) {
            throw new TestErrorException("Helper.classIsSubclass(Class subClass, Class superClass) does not recognize that parent class is null.");
        }
        if (e != null) {
            throw new TestErrorException("An exception should not have been thrown when checking for status as a subclass when superclass is null: " + e.toString());
        }
    }
}
