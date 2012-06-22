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
 *     John Vandale - initial API and implementation
 ******************************************************************************/  
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.io.File;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.testing.models.employee.domain.Employee;

/**
 * Bug 295383
 * Test to ensure that NonSynchronizedVector is used instead of java.util.Vector
 * for event listeners collection when coming from project XML.
 */
public class EventListenerCollectionTest extends org.eclipse.persistence.testing.framework.TestCase {

    protected org.eclipse.persistence.sessions.Project project = null;
    public static final String FILENAME = "EventListenersCollectionShouldBeNonSynchronizedTest.xml";

    public EventListenerCollectionTest() {
        setDescription("Ensure event listeners collection is a NonSynchronizedVector.");
    }

    public void setup() {
        project = new EmployeeProject();

        ClassDescriptor descriptor =
            project.getDescriptors().get(Employee.class);

        descriptor.getEventManager().addListener(new DescriptorEventAdapter());
    }
    
    public void test() {
        XMLProjectWriter.write(FILENAME, project);
        project = XMLProjectReader.read(FILENAME, getClass().getClassLoader());
    }

    public void verify() {
        ClassDescriptor descriptor =
            project.getDescriptors().get(Employee.class);

        java.util.Vector listeners = descriptor.getEventManager().getEventListeners();

        //test that the collection type is a NonSynchronizedVector
        if (!(listeners instanceof org.eclipse.persistence.internal.helper.NonSynchronizedVector)) {
        	throwError("Descriptor from project XML with event listeners does not use NonSynchronizedVector for event listeners collection.");
        }
    }
    
    public void reset() {
        File file = new File(FILENAME);
        file.delete();
    }
}
