/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directcollection.union;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import javax.xml.namespace.QName;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLUnionField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class UnionDateTimeToDateTestCases extends XMLMappingTestCases {

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/union/UnionDateTimeToDate.xml";
    private final static String CONTROL_ITEM = "10";
        private final static String CONTROL_FIRST_NAME = "Jane";
        private final static String CONTROL_LAST_NAME = "Doe";

    public UnionDateTimeToDateTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
                Project p = new SimpleUnionProject();

                XMLCompositeDirectCollectionMapping mapping = (XMLCompositeDirectCollectionMapping) p.getDescriptor(Person.class).getMappingForAttributeName("items");
                XMLUnionField field = new XMLUnionField("item/text()");

                QName stringQName = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, XMLConstants.STRING);

          field.addConversion(XMLConstants.DATE_QNAME, java.util.Date.class);
        field.addConversion(XMLConstants.TIME_QNAME, java.util.Date.class);
                field.addSchemaType(XMLConstants.DATE_QNAME);
        field.addSchemaType(XMLConstants.TIME_QNAME);
                field.addSchemaType(stringQName);
                mapping.setField(field);

                setProject(p);
    }

    protected Object getControlObject() {
        Person person = new Person();

                Calendar cal = Calendar.getInstance();
                cal.clear();
                cal.set(Calendar.MONTH, Calendar.JANUARY);
                cal.set(Calendar.DAY_OF_MONTH, 1);
                cal.set(Calendar.YEAR, 2001);
                Date date = cal.getTime();

                person.addItem(CONTROL_ITEM);
                person.addItem(date);

                person.setFirstName(CONTROL_FIRST_NAME);
        person.setLastName(CONTROL_LAST_NAME);
        return person;
    }
}
