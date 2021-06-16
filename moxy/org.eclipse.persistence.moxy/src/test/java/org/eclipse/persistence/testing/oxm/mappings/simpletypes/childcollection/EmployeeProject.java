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
package org.eclipse.persistence.testing.oxm.mappings.simpletypes.childcollection;

// TopLink imports
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {

    public EmployeeProject() {
        super();
        this.addDescriptor(getEmployeeDescriptor());
        this.addDescriptor(getPhoneDescriptor());
    }

    XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("employee");

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("name");
        mapping.setXPath("name/text()");
        xmlDescriptor.addMapping(mapping);

        XMLCompositeCollectionMapping cmapping = new XMLCompositeCollectionMapping();
        cmapping.setAttributeName("phones");
        cmapping.setReferenceClass(Phone.class);
        cmapping.useCollectionClass(java.util.Vector.class);
        cmapping.setXPath("phone-no");
        xmlDescriptor.addMapping(cmapping);

        return xmlDescriptor;
    }

    XMLDescriptor getPhoneDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Phone.class);

        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("number");
        mapping.setXPath("text()");
        xmlDescriptor.addMapping(mapping);

        return xmlDescriptor;
    }
}
