/*
 * Copyright (c) 1998, 2025 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.tests.security;

import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.mappings.TransformationMapping;
import org.eclipse.persistence.sessions.DataRecord;
import org.eclipse.persistence.sessions.Session;

import java.util.Date;

//Created by Ian Reid
//Date: April 25, 2k3
public class SecurityOnInitializingAttributeMethodTest extends ExceptionTestSaveSecurityManager {

    private TransformationMapping mapping;

    public SecurityOnInitializingAttributeMethodTest(Class<?> c) {
        super("This tests security on initializing attribute method (TL-ERROR 84)", c);
    }

    @Override
    protected void setup() {
        super.setup();
        expectedException = DescriptorException.securityOnInitializingAttributeMethod("buildNormalHours", mapping, new Exception());

        mapping = new TransformationMapping();
        mapping.setAttributeName("normalHours");
        mapping.setAttributeTransformation("buildNormalHours");//
        mapping.addFieldTransformation("EMPLOYEE.START_TIME", "getStartTime");//
        mapping.addFieldTransformation("EMPLOYEE.END_TIME", "getEndTime");//
        mapping.setDescriptor(getTestDescriptor());
    }

    @Override
    public void test() {
        try {
            mapping.initialize((AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    static class AttributeMethod {
        public String getStartTime(Session s) {
            //do nothing security manager will cause error to occur
            return "";
        }

        public String getEndTime(Session s) {
            //do nothing security manager will cause error to occur
            return "";
        }
    }

    static class AttributeMethodOneArg extends AttributeMethod {
        public Date buildNormalHours(DataRecord dataRecord) {
            //do nothing security manager will cause error to occur
            return null;
        }
    }

    static class AttributeMethodAbstractSession extends AttributeMethod {
        public Date buildNormalHours(DataRecord dataRecord, AbstractSession session) {
            //do nothing security manager will cause error to occur
            return null;
        }
    }

    static class AttributeMethodSession extends AttributeMethod {
        public Date buildNormalHours(DataRecord dataRecord, Session s) {
            //do nothing security manager will cause error to occur
            return null;
        }
    }
}
