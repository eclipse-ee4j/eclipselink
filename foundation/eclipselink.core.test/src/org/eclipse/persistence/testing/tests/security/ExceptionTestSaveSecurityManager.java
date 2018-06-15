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

import java.lang.reflect.Field;

import org.eclipse.persistence.descriptors.RelationalDescriptor;
import org.eclipse.persistence.exceptions.IntegrityChecker;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.tests.validation.ExceptionTest;


//Created By Ian Reid
//This class should be sub-classed for all test cases in which a security manager is changed
//to the session OR the integrityChecker is changed
public class ExceptionTestSaveSecurityManager extends ExceptionTest {

    private SecurityManager orgSecurityManager;
    private IntegrityChecker orgIntegrityChecker;
    private boolean orgDefaultUseDoPrivilegedValue;
    private Class cls;
    private RelationalDescriptor descriptor;

    public ExceptionTestSaveSecurityManager() {
    }

    public ExceptionTestSaveSecurityManager(String description, Class cls) {
        super();
        if (cls != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(super.getName());
            sb.append("(");
            sb.append(cls.getSimpleName());
            sb.append(")");
            setName(sb.toString());
        }
        setDescription(description);
        this.cls = cls;
    }

    protected Class getTestClass() {
        return cls;
    }

    protected RelationalDescriptor getTestDescriptor() {
        return descriptor;
    }

    protected void setup() {
        orgIntegrityChecker = getSession().getIntegrityChecker();
        getSession().setIntegrityChecker(new IntegrityChecker()); //moved into setup
        getSession().getIntegrityChecker().dontCatchExceptions(); //moved into setup
        orgSecurityManager = System.getSecurityManager(); //security java.policy must allow full access
        System.setSecurityManager(new TestSecurityManager());
        orgDefaultUseDoPrivilegedValue = getOrgDefaultUseDoPrivilegedValue();
        PrivilegedAccessHelper.setDefaultUseDoPrivilegedValue(true);
        descriptor = new RelationalDescriptor();
        descriptor.setJavaClass(getTestClass());
    }

    public void reset() {
        getSession().setIntegrityChecker(orgIntegrityChecker); //security java.policy must allow full access
        System.setSecurityManager(orgSecurityManager);
        PrivilegedAccessHelper.setDefaultUseDoPrivilegedValue(orgDefaultUseDoPrivilegedValue);
    }

    private boolean getOrgDefaultUseDoPrivilegedValue() {
        Field def = null;
        try {
            def = PrivilegedAccessHelper.getDeclaredField(PrivilegedAccessHelper.class, "defaultUseDoPrivilegedValue", true);
            return (Boolean) def.get(null);
        } catch (Exception e) {
            throw new TestErrorException(e.getMessage());
        } finally {
            if (def != null) {
                def.setAccessible(false);
            }
        }
    }
}
