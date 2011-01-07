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
package org.eclipse.persistence.testing.tests.unitofwork.changeflag;

import java.beans.PropertyChangeEvent;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.descriptors.changetracking.AttributeChangeListener;
import org.eclipse.persistence.internal.sessions.UnitOfWorkImpl;
import org.eclipse.persistence.sessions.UnitOfWork;
import org.eclipse.persistence.testing.tests.validation.ExceptionTest;
import org.eclipse.persistence.testing.models.employee.domain.Employee;


public class WrongPropertyNameTest extends ExceptionTest {

    ClassDescriptor des;
    UnitOfWork uow;
    PropertyChangeEvent even;

    public WrongPropertyNameTest() {
        setDescription("Tests if wrongPropertyNameInChangeEvent exception would be thrown");
    }

    protected void setup() {
        expectedException = 
                org.eclipse.persistence.exceptions.ValidationException.wrongPropertyNameInChangeEvent(null, null);

        des = getSession().getDescriptor(Employee.class);
        uow = getSession().acquireUnitOfWork();
        even = new PropertyChangeEvent(new Employee(), "wrongName", "oldName", "newName");
    }

    protected void test() {
        try {
            new AttributeChangeListener(des, (UnitOfWorkImpl)uow, new Employee()).internalPropertyChange(even);
        } catch (EclipseLinkException ex) {
            caughtException = ex;
        }
    }
}
