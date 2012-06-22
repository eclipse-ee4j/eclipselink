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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.sessions.DatabaseSession;


public class CatchMultilpeSetTableNameTest extends ExceptionTest {
    public CatchMultilpeSetTableNameTest() {
        super();
        setDescription("This test attempts to call setTableName() multiple times.");
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor employeeDescriptor = new RelationalDescriptor();
        employeeDescriptor.setJavaClass(Employee.class);
        employeeDescriptor.setTableName("VAL_EMP");
        employeeDescriptor.setTableName("VAL_ADD");
        return employeeDescriptor;
    }

    protected void setup() {
        expectedException = DescriptorException.onlyOneTableCanBeAddedWithThisMethod(null);
    }

    public void test() {
        try {
            ((DatabaseSession)getSession()).addDescriptor(descriptor());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
