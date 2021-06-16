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
package org.eclipse.persistence.testing.oxm.xmlroot.complex;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.*;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {

    public EmployeeProject() {
        super();
        this.addDescriptor(getEmployeeDescriptor());
        this.addDescriptor(getAddressDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Employee.class);
        xmlDescriptor.setDefaultRootElement("employee");

        NamespaceResolver nsResolver = new NamespaceResolver();
        xmlDescriptor.setNamespaceResolver(nsResolver);

        XMLAnyObjectMapping anyObjectMapping = new XMLAnyObjectMapping();
        // anyObjectMapping.setXPath("g");
        anyObjectMapping.setAttributeName("anyObject");
        anyObjectMapping.setUseXMLRoot(true);
        xmlDescriptor.addMapping(anyObjectMapping);

        /*
        XMLAnyCollectionMapping anyCollectionMapping = new XMLAnyCollectionMapping();
        anyCollectionMapping.setAttributeName("anyCollection");
        xmlDescriptor.addMapping(anyCollectionMapping);
        */
        return xmlDescriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Address.class);
        xmlDescriptor.setDefaultRootElement("address");

        NamespaceResolver nsResolver = new NamespaceResolver();
        // nsResolver.put("foo", "http://www.example.org/2");
        xmlDescriptor.setNamespaceResolver(nsResolver);

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("street/text()");
        xmlDescriptor.addMapping(streetMapping);

        XMLDirectMapping cityMapping = new XMLDirectMapping();
        cityMapping.setAttributeName("city");
        cityMapping.setXPath("city/text()");
        xmlDescriptor.addMapping(cityMapping);

        return xmlDescriptor;
    }

}
