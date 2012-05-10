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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.sessions.DatabaseSession;


//Created by Ian Reid
//Date: Mar 26, 2k3

public class NullPointerWhileSettingValueThruInstanceVariableAccessorTest extends ExceptionTest {

    DatabaseMapping mapping;

    public NullPointerWhileSettingValueThruInstanceVariableAccessorTest() {
        setDescription("This tests Null Pointer While Setting Value Thru Instance Variable Accessor (TL-ERROR 071)");
    }

    protected void setup() {
        expectedException = DescriptorException.nullPointerWhileSettingValueThruInstanceVariableAccessor(null, null, null);

        ClassDescriptor descriptor = ((DatabaseSession)getSession()).getDescriptor(org.eclipse.persistence.testing.models.employee.domain.Employee.class);
        mapping = descriptor.getMappingForAttributeName("salary");
    }

    public void test() {
        try {
            mapping.setAttributeValueInObject(null, new Integer(0));
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

}
