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
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.DirectToFieldMapping;


//Created by Ian Reid
//Date: Feb 26, 2k3

public class NormalDescriptorsDoNotSupportNonRelationalExtensionsTest extends ExceptionTest {
    public NormalDescriptorsDoNotSupportNonRelationalExtensionsTest(String methodName) {
        super();
        this.methodName = methodName;
        setDescription("This tests Normal Descriptors Do Not Support Non Relational Extensions (" + methodName + ") ( TL-ERROR 157) " + "");
    }

    protected void setup() {
        expectedException = DescriptorException.normalDescriptorsDoNotSupportNonRelationalExtensions(null);
        orgIntegrityChecker = getSession().getIntegrityChecker();

        getSession().setIntegrityChecker(new IntegrityChecker());
        getSession().getIntegrityChecker().dontCatchExceptions();
    }

    String methodName;
    IntegrityChecker orgIntegrityChecker;

    public void reset() {
        if (orgIntegrityChecker != null) {
            getSession().setIntegrityChecker(orgIntegrityChecker);
        }
    }

    public void test() {
        RelationalDescriptor descriptor = descriptor();
        try {
            if (methodName.equals("buildDirectValuesFromFieldValue")) {
                descriptor.buildDirectValuesFromFieldValue(null);
            } else if (methodName.equals("buildFieldValueFromDirectValues")) {
                descriptor.buildFieldValueFromDirectValues(null, null, (AbstractSession)getSession());
            } else if (methodName.equals("buildFieldValueFromForeignKeys")) {
                descriptor.buildFieldValueFromForeignKeys(null, null, (AbstractSession)getSession());
            } else if (methodName.equals("buildFieldValueFromNestedRow")) {
                descriptor.buildFieldValueFromNestedRow(null, (AbstractSession)getSession());
            } else if (methodName.equals("buildFieldValueFromNestedRows")) {
                descriptor.buildFieldValueFromNestedRows(null, null, (AbstractSession)getSession());
            } else if (methodName.equals("buildNestedRowFromFieldValue")) {
                descriptor.buildNestedRowFromFieldValue(null);
            } else if (methodName.equals("buildNestedRowsFromFieldValue")) {
                descriptor.buildNestedRowsFromFieldValue(null, (AbstractSession)getSession());
            } else {
                throw new org.eclipse.persistence.testing.framework.TestProblemException("Wrong method name");
            }

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    public RelationalDescriptor descriptor() {
        RelationalDescriptor descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.tests.validation.EmployeeWithProblems.class);
        descriptor.addTableName("EMPLOYEE");
        descriptor.addTableName("ADDRESS");
        descriptor.addPrimaryKeyFieldName("EMPLOYEE.EMP_ID");

        // Descriptor properties.
        descriptor.useFullIdentityMap();
        descriptor.setIdentityMapSize(100);
        descriptor.useRemoteFullIdentityMap();
        descriptor.setRemoteIdentityMapSize(100);
        descriptor.setSequenceNumberFieldName("EMP_ID");
        descriptor.setSequenceNumberName("EMP_SEQ");

        DirectToFieldMapping idMapping = new DirectToFieldMapping();
        idMapping.setAttributeName("id");
        idMapping.setFieldName("EMPLOYEE.EMP_ID");
        descriptor.addMapping(idMapping);


        return descriptor;
    }
}
