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
//     John Vandale - initial API and implementation
//     12/14/2017-3.0 Tomas Kraus
//       - 291546: Performance degradation due to usage of Vector in DescriptorEventManager
package org.eclipse.persistence.testing.tests.workbenchintegration;

import java.io.File;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import org.eclipse.persistence.descriptors.ClassDescriptor;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.descriptors.DescriptorEventListener;
import org.eclipse.persistence.sessions.factories.XMLProjectReader;
import org.eclipse.persistence.sessions.factories.XMLProjectWriter;
import org.eclipse.persistence.testing.models.employee.domain.Employee;
import org.eclipse.persistence.testing.models.employee.relational.EmployeeProject;

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

        List<DescriptorEventListener> listeners = descriptor.getEventManager().getEventListeners();

        //test that the collection type is a NonSynchronizedVector
        if (!(listeners instanceof CopyOnWriteArrayList)) {
            throwError("Descriptor from project XML with event listeners does not use CopyOnWriteArrayList for event listeners collection.");
        }
    }

    public void reset() {
        File file = new File(FILENAME);
        file.delete();
    }
}
