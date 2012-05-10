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

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.DirectToFieldMapping;


//Created by Ian Reid
//Date: Mar 5, 2k3

public class InvalidMappingOperationTest extends ExceptionTest {

    String methodName;

    public InvalidMappingOperationTest(String methodName) {
        this.methodName = methodName;
        setDescription("This tests Invalid Mapping Operation (" + methodName + ") (TL-ERROR 151)");
    }

    protected void setup() {
        expectedException = DescriptorException.invalidMappingOperation(null, "Dummy_operation");
    }


    public void test() {
        DirectToFieldMapping mapping = new DirectToFieldMapping();
        try {
            if (methodName.equals("buildBackupCloneForPartObject")) {
                mapping.buildBackupCloneForPartObject(null, null, null, null);
            } else if (methodName.equals("buildCloneForPartObject")) {
                mapping.buildCloneForPartObject(null, null, null, null, null, true);
            } else if (methodName.equals("createUnitOfWorkValueHolder")) {
                mapping.createCloneValueHolder(null, null, null, null, null, false);
            } else if (methodName.equals("getContainerPolicy")) {
                mapping.getContainerPolicy();
            } else if (methodName.equals("getRealCollectionAttributeValueFromObject")) {
                mapping.getRealCollectionAttributeValueFromObject(null, null);
            } else if (methodName.equals("getValueFromRemoteValueHolder")) {
                mapping.getValueFromRemoteValueHolder(null);
            } else if (methodName.equals("iterateOnRealAttributeValue")) {
                mapping.iterateOnRealAttributeValue(null, null);
            } else if (methodName.equals("simpleAddToCollectionChangeRecord")) {
                mapping.simpleAddToCollectionChangeRecord(null, null, null, null);
            } else if (methodName.equals("simpleRemoveFromCollectionChangeRecord")) {
                mapping.simpleRemoveFromCollectionChangeRecord(null, null, null, null);
            } else {
                throw new org.eclipse.persistence.testing.framework.TestProblemException("Wrong method name for testing Invalid Mapping Operation");
            }

        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }
}
