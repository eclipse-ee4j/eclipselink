/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.mappings.directtofield.cdata;

import java.math.BigInteger;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class DirectToFieldCDATATestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/cdata/employee.xml";
    private final static String CONTROL_FIRST_NAME = "Jane";
    private final static String CONTROL_LAST_NAME = "Doe";
    private final static String CONTROL_DATA = "A string containing bad xml characters like < and > and /> and stuff like that";

    public DirectToFieldCDATATestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new DirectToFieldCDATAProject());
    }

    protected Object getControlObject() {
        Employee emp = new Employee();
        emp.firstName = CONTROL_FIRST_NAME;
        emp.lastName = CONTROL_LAST_NAME;
        emp.data = CONTROL_DATA;
        
        return emp;
    }
    
    public void testObjectToContentHandler() throws Exception {
        //CDATA not supported by content handlers
    }
    
}

