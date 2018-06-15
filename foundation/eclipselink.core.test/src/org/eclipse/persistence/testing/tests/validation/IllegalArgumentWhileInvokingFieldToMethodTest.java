/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.helper.DatabaseField;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.TransformationMapping;

//need for DatabaseField class


//Created by Ian Reid
//Date: Mar 24, 2k3
//uses EmployeeWithProblems (but normal will work too)

public class IllegalArgumentWhileInvokingFieldToMethodTest extends ExceptionTest {
    public IllegalArgumentWhileInvokingFieldToMethodTest() {
        setDescription("This tests Illegal Argument While Invoking Field To Method (TL-ERROR 030)");
    }
    RelationalDescriptor descriptor;
    TransformationMapping mapping;
    DatabaseField field;

    protected void setup() {
        descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(EmployeeWithProblems.class);
        descriptor.addTableName("EMPLOYEE");
        mapping = new TransformationMapping();
        mapping.setAttributeName("normalHours");
        mapping.setAttributeTransformation("buildNormalHours");
        mapping.addFieldTransformation("EMPLOYEE.START_TIME", "getCourses");
        descriptor.addMapping(mapping);
        mapping.initialize((AbstractSession)getSession());
        field = descriptor.buildField("START_TIME");

        expectedException = DescriptorException.illegalArgumentWhileInvokingFieldToMethod("invalidMethod", mapping, new Exception());
    }

    public void test() {
        try {
            //IllegalArgumentWhileInvokingFieldToMethodTest class cause expected error to occur.
            mapping.valueFromObject(new IllegalArgumentWhileInvokingFieldToMethodTest(), field, (AbstractSession)getSession());
            //        mapping.valueFromObject(new EmployeeWithProblems(), field, getSession());         //no problems
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
    //  public String invalidMethod(String row)  {
    //    return "";
    //  }

}
