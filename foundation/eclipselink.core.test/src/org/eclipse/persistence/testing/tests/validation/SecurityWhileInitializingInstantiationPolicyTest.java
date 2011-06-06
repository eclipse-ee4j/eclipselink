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
import org.eclipse.persistence.internal.descriptors.InstantiationPolicy;
import org.eclipse.persistence.internal.sessions.AbstractSession;


public class SecurityWhileInitializingInstantiationPolicyTest extends ExceptionTestSaveSecurityManager {
    InstantiationPolicy instantiationPolicy;

    public SecurityWhileInitializingInstantiationPolicyTest() {
        setDescription("This tests security while initializing instantiation policy (TL-ERROR 90)");
    }

    protected void setup() {
        super.setup();
        expectedException = DescriptorException.securityWhileInitializingInstantiationPolicy(null, null, null);

        instantiationPolicy = new org.eclipse.persistence.internal.descriptors.InstantiationPolicy();
        instantiationPolicy.useFactoryInstantiationPolicy((Object)null, "getCity");
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.Address.class);
        instantiationPolicy.setDescriptor(descriptor);
    }


    public void test() {
        try {
            instantiationPolicy.initialize((AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
