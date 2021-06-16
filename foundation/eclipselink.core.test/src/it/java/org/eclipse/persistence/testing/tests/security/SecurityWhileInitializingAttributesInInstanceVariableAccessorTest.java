/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.tests.security;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.mappings.AttributeAccessor;
import org.eclipse.persistence.mappings.DirectToFieldMapping;

//Created by Ian Reid
//Date: April 25, 2k3
public class SecurityWhileInitializingAttributesInInstanceVariableAccessorTest extends ExceptionTestSaveSecurityManager {

    private AttributeAccessor accessor;

    public SecurityWhileInitializingAttributesInInstanceVariableAccessorTest() {
        super("This tests Security While Initializing Attributes In Instance Variable Accessor (TL-ERROR 86)", VariableAccessor.class);
    }

    protected void setup() {
        super.setup();
        expectedException = DescriptorException.securityWhileInitializingAttributesInInstanceVariableAccessor("p_name", "SecurityWhileInitializingAttributesInInstanceVariableAccessorTest", new Exception());

        DirectToFieldMapping mapping = new DirectToFieldMapping();
        mapping.setAttributeName("p_name");
        mapping.setFieldName("EMPLOYEE.F_NAME");
        accessor = mapping.getAttributeAccessor();
    }

    public void test() {
        try {
            accessor.initializeAttributes(getTestClass());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    static class VariableAccessor {
        private String p_name;
    }
}
