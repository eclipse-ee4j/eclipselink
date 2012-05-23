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
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;


//Created by Ian Reid
//Date: Mar 21, 2k3

public class TargetInvocationWhileInvokingAttributeMethodTest extends ExceptionTest {
    public TargetInvocationWhileInvokingAttributeMethodTest() {
        setDescription("This tests Target Invocation While Invoking Attribute Method (TL-ERROR 101)");
    }
    RelationalDescriptor descriptor;
    TransformationMapping mapping;
    DatabaseRecord row;

    protected void setup() {
        descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(TargetInvocationWhileInvokingAttributeMethodTest.class);
        descriptor.addTableName("EMPLOYEE");
        mapping = new TransformationMapping();
        mapping.setAttributeName("normalHours");
        mapping.setAttributeTransformation("invalidMethod");
        descriptor.addMapping(mapping);
        mapping.initialize((AbstractSession)getSession());
        row = new DatabaseRecord();

        expectedException = DescriptorException.targetInvocationWhileInvokingAttributeMethod(mapping, new Exception());
    }

    public void test() {
        try {
            mapping.invokeAttributeTransformer(row, new TargetInvocationWhileInvokingAttributeMethodTest(), (AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public String invalidMethod(Record row) throws java.lang.IllegalAccessException {
        throw new java.lang.IllegalAccessException();
    }


}
