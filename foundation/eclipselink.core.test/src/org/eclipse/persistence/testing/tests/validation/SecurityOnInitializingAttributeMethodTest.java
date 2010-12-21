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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.TransformationMapping;


//Created by Ian Reid
//Date: April 25, 2k3

public class SecurityOnInitializingAttributeMethodTest extends ExceptionTestSaveSecurityManager {

    TransformationMapping mapping;

    public SecurityOnInitializingAttributeMethodTest() {
        setDescription("This tests security on initializing attribute method (TL-ERROR 84)");
    }

    protected void setup() {
        super.setup();
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(Employee.class);
        mapping = new TransformationMapping();
        mapping.setAttributeName("normalHours");

        mapping.setAttributeTransformation("buildNormalHours");
        mapping.addFieldTransformation("EMPLOYEE.START_TIME", "getStartTime");
        mapping.addFieldTransformation("EMPLOYEE.END_TIME", "getEndTime");
        mapping.setDescriptor(descriptor);


        expectedException = DescriptorException.securityOnInitializingAttributeMethod("dummy_Method", mapping, new Exception());
    }

    public void test() {
        try {
            mapping.initialize((AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
