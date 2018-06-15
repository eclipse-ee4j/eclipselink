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
package org.eclipse.persistence.testing.tests.security;

import org.eclipse.persistence.descriptors.InheritancePolicy;
import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.DescriptorException;
import org.eclipse.persistence.exceptions.EclipseLinkException;
import org.eclipse.persistence.internal.sessions.AbstractRecord;
import org.eclipse.persistence.internal.sessions.AbstractSession;
import org.eclipse.persistence.sessions.Record;

//Created by Ian Reid
//Date: April 25, 2k3
public class SecurityWhileInitializingClassExtractionMethodTest extends ExceptionTestSaveSecurityManager {

    private InheritancePolicy policy;

    public SecurityWhileInitializingClassExtractionMethodTest(Class c) {
        super("This tests Security While Initializing Class Extraction Method (TL-ERROR 88)", c);
    }

    protected void setup() {
        super.setup();
        expectedException = DescriptorException.securityWhileInitializingClassExtractionMethod("Dummy_Method", new RelationalDescriptor(), new Exception());

        getTestDescriptor().setInheritancePolicy(new InheritancePolicy());
        policy = getTestDescriptor().getInheritancePolicy();
        //need not getClassExtractionMethodName() == null
        policy.setClassExtractionMethodName("dummy_Method"); //this method does exist in above class
        //need NoSuchMethod thrown from         setClassExtractionMethod(Helper.getDeclaredMethod(getDescriptor().getJavaClass(), getClassExtractionMethodName(), declarationParameters));
        //nice if isChildDescriptor() == false
        policy.setParentClass(null);
    }

    public void test() {
        try {
            policy.preInitialize((AbstractSession)getSession());
        } catch (EclipseLinkException exception) {
            caughtException = exception;
        }
    }

    static class ExtractionAbstractRecord {
        public static void dummy_Method(AbstractRecord record) {
        }
    }

    static class ExtractionRecord {
        public static void dummy_Method(Record record) {
        }
    }
}
