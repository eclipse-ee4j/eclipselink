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
package org.eclipse.persistence.testing.tests.validation;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.sessions.Session;
import org.eclipse.persistence.testing.framework.AutoVerifyTestCase;


/**
 * This test invokes the getClassDescriptor(...) methods for the
 * AbstractSession and Project classes and checks that null is returned.
 * @author David Minsky
 */
public class

GetClassDescriptorWithNullTest extends AutoVerifyTestCase {

    protected ClassDescriptor testProjectResult;
    protected ClassDescriptor testSessionClassResult;
    protected ClassDescriptor testSessionObjectResult;
    protected ClassDescriptor testSessionDescriptorResult;

    public GetClassDescriptorWithNullTest() {
        super();
        setDescription("Verifies calling getClassDescriptor() with a null parameter returns null");
    }

    public void test() {
        Session aSession = getSession();
        Project aProject = aSession.getProject();

        // important to declare types as Object / Class to invoke correct methods on Session
        Class aClass = null;
        Object anObject = null;

        /* when null is passed to Project / Session getClassDescriptor(Object) /
           getClassDescriptor(Class) the returned value should be null */
        testProjectResult = aProject.getClassDescriptor(aClass);
        testSessionClassResult = aSession.getClassDescriptor(aClass);
        testSessionObjectResult = aSession.getClassDescriptor(anObject);
        // also test underlying getDescriptor method on AbstractSession
        testSessionDescriptorResult = aSession.getDescriptor(anObject);
    }

    public void verify() {
        assertNull("non-null value returned from null param passed to Project>>getClassDescriptor(Class)", testProjectResult);
        assertNull("non-null value returned from null param passed to Session>>getClassDescriptor(Class)", testSessionClassResult);
        assertNull("non-null value returned from null param passed to Session>>getClassDescriptor(Object)", testSessionObjectResult);
        assertNull("non-null value returned from null param passed to Session>>getDescriptor(Object)", testSessionDescriptorResult);
    }

}
