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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.union;

import java.math.BigInteger;
import java.util.ArrayList;
import javax.xml.namespace.QName;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLUnionField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class UnionWithTypeAttributeTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/union/UnionWithTypeAttribute.xml";
    protected final static Integer CONTROL_AGE = new Integer(10);
    protected final static String CONTROL_FIRST_NAME = "Jane";
    protected final static String CONTROL_LAST_NAME = "Doe";

    public UnionWithTypeAttributeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new SimpleUnionProject();

        ((XMLUnionField)p.getDescriptor(Person.class).getMappingForAttributeName("age").getField()).setIsTypedTextField(true);

        QName stringQName = new QName(XMLConstants.SCHEMA_URL, "string");
        QName integerQName = new QName(XMLConstants.SCHEMA_URL, "integer");

        ArrayList schemaTypes = new ArrayList();
        schemaTypes.add(XMLConstants.DATE_QNAME);
        schemaTypes.add(stringQName);
        schemaTypes.add(integerQName);

        ((XMLUnionField)p.getDescriptor(Person.class).getMappingForAttributeName("age").getField()).setSchemaTypes(schemaTypes);

        setProject(p);
    }

    protected Object getControlObject() {
        Person person = new Person();
        person.setAge(CONTROL_AGE);
        person.setFirstName(CONTROL_FIRST_NAME);
        person.setLastName(CONTROL_LAST_NAME);
        return person;
    }
}
