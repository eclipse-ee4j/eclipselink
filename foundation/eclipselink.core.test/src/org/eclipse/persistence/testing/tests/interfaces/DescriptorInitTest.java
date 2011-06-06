/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.tests.interfaces;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.sessions.*;
import org.eclipse.persistence.testing.framework.*;
import org.eclipse.persistence.testing.models.interfaces.*;

public class DescriptorInitTest extends TestCase {
    public Project project;
    public DatabaseSession dbsession;

    public DescriptorInitTest() {
        super();
        setName("DescriptorInitTest");
        setDescription("Tests initialization of descriptors while adding during enumeration.");
    }

    public void reset() {
        // Must disconnect the previous session.
        ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).login();
    }

    public void setup() {
        project = new InterfaceHashtableProject();
        project.setLogin((DatabaseLogin)getSession().getLogin().clone());
        dbsession = project.createDatabaseSession();
        dbsession.setSessionLog(getSession().getSessionLog());

        // Must disconnect the previous session.
        ((org.eclipse.persistence.sessions.DatabaseSession)getSession()).logout();
    }

    public void test() {
        for (java.util.Iterator iterator = project.getDescriptors().values().iterator(); iterator.hasNext(); ) {
            ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
            String className = descriptor.getJavaClass().toString();

            String part1;
            String part2;
            Character ch = new Character(className.charAt(className.length() - 2));
            if (ch.equals(new Character('0'))) { //Class100
                part1 = className.substring(6, className.length() - 3);
                part2 = className.substring(className.length() - 3);
            } else if (Character.isDigit(ch.charValue())) { //Class##
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

    public void verify() {
        //Make sure all Descriptors have been initialized.
        for (java.util.Iterator iterator = project.getDescriptors().values().iterator(); iterator.hasNext(); ) {
            ClassDescriptor descriptor = (ClassDescriptor)iterator.next();
            if (!descriptor.isFullyInitialized()) {
                throw new TestErrorException("Descriptor \"" + descriptor + "\" is NOT INITIALIZED");
            }
        }
    }
}
