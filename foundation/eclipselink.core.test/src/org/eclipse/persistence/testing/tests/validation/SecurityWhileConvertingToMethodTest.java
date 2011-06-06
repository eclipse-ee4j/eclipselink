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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.TransformationMapping;


//Created by Ian Reid
//Date: April 25, 2k3

public class SecurityWhileConvertingToMethodTest extends ExceptionTestSaveSecurityManager {

    TransformationMapping mapping;

    public SecurityWhileConvertingToMethodTest() {
        setDescription("This tests security while converting to method (TL-ERROR 85)");
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
        // the following three lines ensure that the mapping is not isWriteOnly();
        mapping.setAttributeName(null);
        mapping.getAttributeAccessor().setAttributeName(null);
        mapping.setAttributeTransformation(null);

        mapping.setDescriptor(descriptor);

        expectedException = DescriptorException.securityWhileConvertingToMethod("dummy_Method", mapping, new Exception());
    }

    public void test() {
        try {
            //need to use the remote Initialization as the normal one would product TL-084 before check this test
            mapping.remoteInitialization((org.eclipse.persistence.sessions.remote.DistributedSession)(new org.eclipse.persistence.sessions.remote.corba.sun.CORBAConnection(new org.eclipse.persistence.sessions.remote.corba.sun.CORBARemoteSessionControllerDispatcher(getSession()))).createRemoteSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
