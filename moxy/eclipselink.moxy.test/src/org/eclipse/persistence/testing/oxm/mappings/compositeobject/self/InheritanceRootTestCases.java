/*******************************************************************************
* Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - March 12/2010 - 2.1 - Initial implementation
******************************************************************************/
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