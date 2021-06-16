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
// dmccann - 1.0M9 - Initial implementation
package org.eclipse.persistence.testing.oxm.events.descriptor;

import java.util.ArrayList;
import java.util.Vector;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.descriptors.DescriptorEvent;
import org.eclipse.persistence.descriptors.DescriptorEventAdapter;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.*;

import org.eclipse.persistence.testing.oxm.events.Employee;

public class EmployeeProject extends org.eclipse.persistence.testing.oxm.events.EmployeeProject {
    ArrayList events;

    public EmployeeProject() {
        super();
    }

    public XMLDescriptor addEmployeeDescriptor() {
        XMLDescriptor descriptor = super.addEmployeeDescriptor();
        descriptor.getEventManager().addListener(new DescriptorEventAdapter() {
            public void postBuild(DescriptorEvent event) {
                events.add(PostBuildEventTestCases.EMPLOYEE_POST_BUILD);
            }
        });
        return descriptor;
    }

    public XMLDescriptor addAddressDescriptor() {
        XMLDescriptor descriptor = super.addAddressDescriptor();
        descriptor.getEventManager().addListener(new DescriptorEventAdapter() {
            public void postBuild(DescriptorEvent event) {
                events.add(PostBuildEventTestCases.ADDRESS_POST_BUILD);
            }
        });
        return descriptor;
    }

    public void setup() {
        events = new ArrayList();
    }
}
