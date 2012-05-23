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

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DatabaseRecord;
import org.eclipse.persistence.sessions.Record;


//Created by Ian Reid
//Date: Mar 21, 2k3

public class TargetInvocationWhileInvokingRowExtractionMethodTest extends ExceptionTest {
    public TargetInvocationWhileInvokingRowExtractionMethodTest() {
        setDescription("This tests Target Invocation While Invoking Row Extraction Method (TL-ERROR 103)");
    }
    RelationalDescriptor descriptor;
    InheritancePolicy policy;
    DatabaseRecord row;

    protected void setup() throws NoSuchMethodException {
        descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(TargetInvocationWhileInvokingRowExtractionMethodTest.class);
        descriptor.addTableName("PROJECT");
        descriptor.getInheritancePolicy().setParentClass(null);
        descriptor.getInheritancePolicy().dontReadSubclassesOnQueries();
        descriptor.getInheritancePolicy().setClassIndicatorFieldName("PROJECT.PROJ_TYPE");
        descriptor.getInheritancePolicy().setShouldUseClassNameAsIndicator(true);
        policy = descriptor.getInheritancePolicy();
        policy.setClassExtractionMethodName("invalidMethod");
        policy.preInitialize((AbstractSession)getSession());

        row = new DatabaseRecord();
        Class[] parmClasses = { Record.class };


        expectedException = DescriptorException.targetInvocationWhileInvokingRowExtractionMethod(row, TargetInvocationWhileInvokingRowExtractionMethodTest.class.getDeclaredMethod("invalidMethod", parmClasses), descriptor, new Exception());
    }

    public void test() {
        try {
            policy.classFromRow(row, (AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public static void invalidMethod(Record row) throws java.lang.IllegalAccessException {
        throw new java.lang.IllegalAccessException();
    }

}
