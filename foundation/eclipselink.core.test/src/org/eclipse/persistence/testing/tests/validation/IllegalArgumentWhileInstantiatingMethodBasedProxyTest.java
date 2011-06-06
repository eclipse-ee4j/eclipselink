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

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.indirection.TransformerBasedValueHolder;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.transformers.MethodBasedAttributeTransformer;
import org.eclipse.persistence.sessions.DatabaseRecord;


//used for Method.class

//Created by Ian Reid
//Date: Mar 24, 2k3

public class IllegalArgumentWhileInstantiatingMethodBasedProxyTest extends ExceptionTest {
    public IllegalArgumentWhileInstantiatingMethodBasedProxyTest() {
        setDescription("This tests Illegal Argument While Instantiating Method Based Proxy (TL-ERROR 028)");
    }
    ClassDescriptor descriptor;
    DatabaseRecord row;
    TransformerBasedValueHolder valueHolder;
    MethodBasedAttributeTransformer theTransformer;
    Object theReceiver;


    protected void setup() throws NoSuchMethodException {
        descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(IllegalArgumentWhileInstantiatingMethodBasedProxyTest.class);
        descriptor.addTableName("Dummy_Table");
        row = new DatabaseRecord();
        Class[] parmClasses = { DatabaseRecord.class };
        theTransformer = new MethodBasedAttributeTransformer();
        theTransformer.setAttributeTransformationMethod(IllegalArgumentWhileInstantiatingMethodBasedProxyTest.class.getDeclaredMethod("invalidMethod", parmClasses));
        //   theReceiver = new IllegalArgumentWhileInstantiatingMethodBasedProxyTest(); //not correct error
        //the following causes the correct error to occur.
        theReceiver = new DatabaseRecord(); //invalid Receiver causes correct error  

        valueHolder = new TransformerBasedValueHolder(theTransformer, theReceiver, row, (AbstractSession)getSession());


        expectedException = DescriptorException.illegalArgumentWhileInstantiatingMethodBasedProxy(new Exception());
    }

    public void test() {
        try {
            valueHolder.getValue();
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public String invalidMethod(DatabaseRecord row) {
        return "";
    }

}
