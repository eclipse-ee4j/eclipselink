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
package org.eclipse.persistence.testing.oxm.mappings.directtofield.union;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import javax.xml.namespace.QName;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLUnionField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class UnionDateTimeToDateTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directtofield/union/UnionDateTimeToDate.xml";
    private final static String CONTROL_AGE = "10";
    private final static String CONTROL_FIRST_NAME = "Jane";
    private final static String CONTROL_LAST_NAME = "Doe";

    public UnionDateTimeToDateTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new SimpleUnionProject();

        XMLDirectMapping anniversaryMapping = new XMLDirectMapping();
        anniversaryMapping.setAttributeName("anniversaryDate");

        XMLUnionField field = new XMLUnionField("anniversary/text()");

        field.addConversion(XMLConstants.DATE_QNAME, java.util.Date.class);
        field.addConversion(XMLConstants.TIME_QNAME, java.util.Date.class);
        field.addSchemaType(XMLConstants.DATE_QNAME);
        field.addSchemaType(XMLConstants.TIME_QNAME);
        anniversaryMapping.setField(field);

        p.getDescriptor(Person.class).addMapping(anniversaryMapping);
        setProject(p);

    }

    protected Object getControlObject() {
        Person person = new Person();
        person.setAge(new BigInteger(CONTROL_AGE));

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, 2001);
        Date date = cal.getTime();

        person.setAnniversaryDate(date);

        person.setFirstName(CONTROL_FIRST_NAME);
        person.setLastName(CONTROL_LAST_NAME);
        return person;
    }
}
