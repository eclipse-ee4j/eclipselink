/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     bdoughan - March 12/2010 - 2.1 - Initial implementation
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self;

import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class InheritanceChildTestCases extends XMLMappingTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/InheritanceChild.xml";

    public InheritanceChildTestCases(String name) throws Exception {
        super(name);
        setProject(new InheritanceProject());
        setControlDocument(XML_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        Employee employee = new Employee();
        employee.setId(123);
        employee.setAddress(new CanadianAddress());
        return employee;
    }

}
