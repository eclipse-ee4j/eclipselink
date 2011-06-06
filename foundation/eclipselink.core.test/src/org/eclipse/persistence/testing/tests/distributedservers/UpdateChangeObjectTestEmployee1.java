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
 package org.eclipse.persistence.testing.tests.distributedservers;

import org.eclipse.persistence.testing.models.aggregate.Employee1;
import org.eclipse.persistence.testing.models.aggregate.HomeAddress;
import org.eclipse.persistence.testing.models.aggregate.WorkingAddress;

/** 
 * Test changing private parts of an object.
 * 
 */
public class UpdateChangeObjectTestEmployee1 extends ComplexUpdateTest {

    public UpdateChangeObjectTestEmployee1() {
        super();
    }

    public UpdateChangeObjectTestEmployee1(Employee1 originalObject) {
        super(originalObject);
    }

    protected void changeObject() {
        Employee1 employee = (Employee1)this.workingCopy;
        // Transformation
        if (employee.getAddress() instanceof HomeAddress) {
            employee.setAddress(WorkingAddress.example1());
        }else{
            employee.setAddress(HomeAddress.example1());
        }
    }
}
