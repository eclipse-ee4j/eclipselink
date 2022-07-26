/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.DatabaseRecord;


//Created by Ian Reid
//Date: Mar 21, 2k3

public class TargetInvocationWhileInvokingRowExtractionMethodTest extends ExceptionTest {
    public TargetInvocationWhileInvokingRowExtractionMethodTest() {
        setDescription("This tests Target Invocation While Invoking Row Extraction Method (TL-ERROR 103)");
    }
    RelationalDescriptor descriptor;
    InheritancePolicy policy;
    DatabaseRecord row;

    @Override
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
        Class<?>[] parmClasses = { DataRecord.class };


        expectedException = DescriptorException.targetInvocationWhileInvokingRowExtractionMethod(row, TargetInvocationWhileInvokingRowExtractionMethodTest.class.getDeclaredMethod("invalidMethod", parmClasses), descriptor, new Exception());
    }

    @Override
    public void test() {
        try {
            policy.classFromRow(row, (AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public static void invalidMethod(DataRecord row) throws java.lang.IllegalAccessException {
        throw new IllegalAccessException();
    }

}
