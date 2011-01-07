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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self;

import java.util.Calendar;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class AttributeOnTargetTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/AttributeOnTarget.xml";
    private final static String CONTROL_STREET = "123 A St.";

    public AttributeOnTargetTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new AttributeOnTargetProject());
    }

    protected Object getControlObject() {
        Employee employee = new Employee();
       
        Address address = new Address();
        address.street = CONTROL_STREET;
        employee.address = address;

        return employee;
    }
}
