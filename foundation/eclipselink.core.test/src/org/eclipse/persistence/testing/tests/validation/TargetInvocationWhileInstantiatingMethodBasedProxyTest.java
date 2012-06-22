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

import java.lang.reflect.Method;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.TransformerBasedValueHolder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.transformers.MethodBasedAttributeTransformer;
import org.eclipse.persistence.sessions.DatabaseRecord;


//used for Method.class

//Created by Ian Reid
//Date: Mar 21, 2k3

public class TargetInvocationWhileInstantiatingMethodBasedProxyTest extends ExceptionTest {
    public TargetInvocationWhileInstantiatingMethodBasedProxyTest() {
        setDescription("This tests Target Invocation While Instantiating Method Based Proxy (TL-ERROR 100)");
    }
    RelationalDescriptor descriptor;
    DatabaseRecord row;
    TransformerBasedValueHolder valueHolder;
    Method theMethod;
    MethodBasedAttributeTransformer theTransformer;
    Object theReceiver;


    protected void setup() throws NoSuchMethodException {
        descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(TargetInvocationWhileInstantiatingMethodBasedProxyTest.class);
        descriptor.addTableName("Dummy_Table");
        row = new DatabaseRecord();
        Class[] parmClasses = { DatabaseRecord.class };
        theMethod = TargetInvocationWhileInstantiatingMethodBasedProxyTest.class.getDeclaredMethod("invalidMethod", parmClasses);
        theTransformer = new MethodBasedAttributeTransformer();
        theTransformer.setAttributeTransformationMethod(theMethod);
        theReceiver = new TargetInvocationWhileInstantiatingMethodBasedProxyTest();

        valueHolder = new TransformerBasedValueHolder(theTransformer, theReceiver, row, (AbstractSession)getSession());


        expectedException = DescriptorException.targetInvocationWhileInstantiatingMethodBasedProxy(new Exception());
    }

    public void test() {
        try {
            valueHolder.getValue();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public String invalidMethod(DatabaseRecord row) throws java.lang.IllegalAccessException {
        throw new java.lang.IllegalAccessException();
    }

}
