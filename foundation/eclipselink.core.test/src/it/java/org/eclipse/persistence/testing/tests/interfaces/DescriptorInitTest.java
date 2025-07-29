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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.DatabaseLogin;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.framework.TestCase;
import org.eclipse.persistence.testing.framework.TestErrorException;
import org.eclipse.persistence.testing.models.interfaces.InterfaceHashtableProject;

public class DescriptorInitTest extends TestCase {
    public Project project;
    public DatabaseSession dbsession;

    public DescriptorInitTest() {
        super();
        setName("DescriptorInitTest");
        setDescription("Tests initialization of descriptors while adding during enumeration.");
    }

    @Override
    public void reset() {
        // Must disconnect the previous session.
        ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).login();
    }

    @Override
    public void setup() {
        project = new InterfaceHashtableProject();
        project.setLogin((DatabaseLogin)getSession().getLogin().clone());
        dbsession = project.createDatabaseSession();
        dbsession.setSessionLog(getSession().getSessionLog());

        // Must disconnect the previous session.
        ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).logout();
    }

    @Override
    public void test() {
        for (ClassDescriptor descriptor : project.getDescriptors().values()) {
            String className = descriptor.getJavaClass().toString();

            String part1;
            String part2;
            Character ch = className.charAt(className.length() - 2);
            if (ch.equals('0')) { //Class100
                part1 = className.substring(6, className.length() - 3);
                part2 = className.substring(className.length() - 3);
            } else if (Character.isDigit(ch)) { //Class##
                part1 = className.substring(6, className.length() - 2);
                part2 = className.substring(className.length() - 2);
            } else { //Class#
                part1 = className.substring(6, className.length() - 1);
                part2 = className.substring(className.length() - 1);
            }
            String interfaceName = part1 + "I" + part2;

            try {
                descriptor.getInterfacePolicy().addParentInterface(Class.forName(interfaceName));
            } catch (Throwable e) {
                throw new TestErrorException("Specified class was not found: " + interfaceName);
            }
        }

        dbsession.login();
        dbsession.logout();
    }

    @Override
    public void verify() {
        //Make sure all Descriptors have been initialized.
        for (ClassDescriptor descriptor : project.getDescriptors().values()) {
            if (!descriptor.isFullyInitialized()) {
                throw new TestErrorException("Descriptor \"" + descriptor + "\" is NOT INITIALIZED");
            }
        }
    }
}
