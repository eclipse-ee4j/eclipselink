/*
 * Copyright (c) 2011, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     bdoughan - March 12/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class InheritanceRootTestCases extends XMLMappingTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/InheritanceRoot.xml";

    public InheritanceRootTestCases(String name) throws Exception {
        super(name);
        setProject(new InheritanceProject());
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.setId(123);
        employee.setAddress(new Address());
        return employee;
    }

}
