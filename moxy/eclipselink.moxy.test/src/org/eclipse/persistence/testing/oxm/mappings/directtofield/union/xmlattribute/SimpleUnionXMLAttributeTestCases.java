/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.union.xmlattribute;

import java.math.BigInteger;
import org.eclipse.persistence.exceptions.ConversionException;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directtofield.union.Person;

public class SimpleUnionXMLAttributeTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/union/xmlattribute/SimpleUnionXMLAttribute.xml";
    private final static String CONTROL_AGE = "10";
    private final static String CONTROL_FIRST_NAME = "Jane";
    private final static String CONTROL_LAST_NAME = "Doe";

    public SimpleUnionXMLAttributeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setProject(new SimpleUnionXMLAttributeProject());
    }

    protected Object getControlObject() {
        Person person = new Person();
        person.setAge(new BigInteger(CONTROL_AGE));
        person.setFirstName(CONTROL_FIRST_NAME);
        person.setLastName(CONTROL_LAST_NAME);
        return person;
    }
}
