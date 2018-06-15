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

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;


public class NoAttributeConversionValueInObjectTypeMapping extends ExceptionTest {
    protected void setup() {
        getSession().getIdentityMapAccessor().initializeIdentityMaps();
        expectedException = DescriptorException.noAttributeValueConversionToFieldValueProvided(null, null);
    }

    protected void test() {
        try {
            getAbstractSession().writeObject(org.eclipse.persistence.testing.models.mapping.Employee.errorExample1());
        } catch (EclipseLinkException exception) {
            this.caughtException = exception;
        }
    }
}
