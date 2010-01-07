/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.xmlattribute;

import java.io.StringWriter;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.oxm.XMLMarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.identifiedbyname.Employee;
import org.eclipse.persistence.internal.helper.Helper;

public class DirectToXMLAttributeIdentifiedByNameSpecialCharactersTestCases extends OXTestCase {
    private final static int CONTROL_ID = 123;
    private final static String CONTROL_FIRST_NAME = "A<B&C<";
    private final static String CONTROL_LAST_NAME = null;
    private final static String CONTROL_XML = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + Helper.cr() + "<employee id=\"123\" first-name=\"A&lt;B&amp;C&lt;\"/>";
    private XMLMarshaller xmlMarshaller;

    public DirectToXMLAttributeIdentifiedByNameSpecialCharactersTestCases(String name) {
        super(name);
    }

    public void setUp() {
        DirectToXMLAttributeIdentifiedByNameProject project = new DirectToXMLAttributeIdentifiedByNameProject();
        XMLContext xmlContext = new XMLContext(project);
        xmlMarshaller = xmlContext.createMarshaller();
        xmlMarshaller.setFormattedOutput(false);
    }

    public void testMarshalSpecialCharacters() {
        StringWriter stringWriter = new StringWriter();
        xmlMarshaller.marshal(getControlObject(), stringWriter);
        String testXML = stringWriter.toString();
        log("EXPECTED:");
        log(CONTROL_XML);
        log("ACTUAL:");
        log(testXML);
        assertEquals(CONTROL_XML, testXML);
    }

    private Object getControlObject() {
        Employee employee = new Employee();
        employee.setID(CONTROL_ID);
        employee.setFirstName(CONTROL_FIRST_NAME);
        employee.setLastName(CONTROL_LAST_NAME);
        return employee;
    }
}
