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
package org.eclipse.persistence.testing.tests.feature;

import org.eclipse.persistence.testing.models.employee.domain.PhoneNumber;
import org.eclipse.persistence.testing.models.employee.relational.*;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.framework.*;

public class InstantiationPolicyTest extends AutoVerifyTestCase {
    //public InstantiationPolicy phoneNumberInstantiationPolicy;
    protected static boolean invokedFactoryStaticMethod = false;
    protected boolean invokedFactoryPhoneNumberMethod = false;

    public static void main(String[] args) throws Exception {
        InstantiationPolicyTest test = new InstantiationPolicyTest();
        test.test();
    }

    protected void test() throws Exception {
        DatabaseSession aSession = createTestSession();
        ClassDescriptor phoneDescriptor = aSession.getClassDescriptor(PhoneNumber.class);
        phoneDescriptor.useFactoryInstantiationPolicy(org.eclipse.persistence.testing.tests.feature.InstantiationPolicyTest.class, "createPhoneNumber");
        aSession.setSessionLog(getSession().getSessionLog());
        aSession.login();
        aSession.readObject(PhoneNumber.class);

        InstantiationPolicyTest factory = (InstantiationPolicyTest)phoneDescriptor.getInstantiationPolicy().getFactory();
        // the session is no longer used - disconnect it.
        aSession.logout();
        if (!factory.invokedFactoryPhoneNumberMethod) {
            throw new TestErrorException("useFactoryInstantiationPolicy(Class, String) does not work.");
        }

        aSession = createTestSession();
        phoneDescriptor = aSession.getClassDescriptor(PhoneNumber.class);
        phoneDescriptor.useFactoryInstantiationPolicy(org.eclipse.persistence.testing.tests.feature.InstantiationPolicyTest.class, "createPhoneNumber", "createPhoneNumberFactory");
        aSession.login();
        aSession.readObject(PhoneNumber.class);

        factory = (InstantiationPolicyTest)phoneDescriptor.getInstantiationPolicy().getFactory();
        // the session is no longer used - disconnect it.
        aSession.logout();
        if (!factory.invokedFactoryPhoneNumberMethod || !InstantiationPolicyTest.invokedFactoryStaticMethod) {
            throw new TestErrorException("useFactoryInstantiationPolicy(Class, String, String) does not work.");
        }

        aSession = createTestSession();
        phoneDescriptor = aSession.getClassDescriptor(PhoneNumber.class);
        phoneDescriptor.useFactoryInstantiationPolicy(new InstantiationPolicyTest(), "createPhoneNumber");
        aSession.login();
        aSession.readObject(PhoneNumber.class);

        factory = (InstantiationPolicyTest)phoneDescriptor.getInstantiationPolicy().getFactory();
        // the session is no longer used - disconnect it.
        aSession.logout();
        if (!factory.invokedFactoryPhoneNumberMethod || !InstantiationPolicyTest.invokedFactoryStaticMethod) {
            throw new TestErrorException("useFactoryInstantiationPolicy(Object, String) does not work.");
        }

        boolean hasException = false;
        try {
            aSession = createTestSession();
            phoneDescriptor = aSession.getClassDescriptor(PhoneNumber.class);
            phoneDescriptor.useMethodInstantiationPolicy("notExistStaticMethodOnDescriptor");
            aSession.login();
            aSession.readObject(PhoneNumber.class);
        } catch (Exception e) {
            hasException = true;
        } finally {
            if (aSession.isConnected()) {
                aSession.logout();
            }
        }
        if (!hasException) {
            throw new TestErrorException("Exeption should be throw when using a non-exist instatiation factory method on the Descriptor class");
        }
    }

    // factory method of PhoneNumber
    public PhoneNumber createPhoneNumber() {
        PhoneNumber phNumber = new PhoneNumber();
        phNumber.setType("instance");

        invokedFactoryPhoneNumberMethod = true;
        return phNumber;

    }

    // factory method of PhoneNumber factory
    public static InstantiationPolicyTest createPhoneNumberFactory() {
        invokedFactoryStaticMethod = true;
        return new InstantiationPolicyTest();
    }

    protected DatabaseSession createTestSession() {
        DatabaseSession aSession = new EmployeeProject().createDatabaseSession();
        aSession.setLogin(getSession().getLogin().clone());
        aSession.setSessionLog(getSession().getSessionLog());
        aSession.logout();
        return aSession;
    }
}
