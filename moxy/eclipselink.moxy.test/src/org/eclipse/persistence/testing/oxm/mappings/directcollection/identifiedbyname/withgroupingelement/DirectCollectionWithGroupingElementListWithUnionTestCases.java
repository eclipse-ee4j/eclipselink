/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
package org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withgroupingelement;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLUnionField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.Employee;

public class DirectCollectionWithGroupingElementListWithUnionTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/identifiedbyname/withgroupingelement/DirectCollectionWithGroupingElementListWithUnion.xml";
    private final static int CONTROL_ID = 123;
    private final static BigInteger CONTROL_RESPONSIBILITY1 = new BigInteger("100");
    private final static BigInteger CONTROL_RESPONSIBILITY2 = new BigInteger("200");
    private final static BigInteger CONTROL_RESPONSIBILITY3 = new BigInteger("300");

    public DirectCollectionWithGroupingElementListWithUnionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new DirectCollectionWithGroupingElementIdentifiedByNameIntegerProject();
        p.getDescriptor(Employee.class).removeMappingForAttributeName("responsibilities");

        XMLUnionField field = new XMLUnionField("responsibilities/list/responsibility/text()");
        QName integerQName = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, "integer");
        QName qname = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, "date");
        field.addSchemaType(integerQName);
        field.addSchemaType(qname);
        field.addConversion(XMLConstants.DATE_QNAME, java.util.Date.class);

        XMLCompositeDirectCollectionMapping responsibilitiesMapping = new XMLCompositeDirectCollectionMapping();
        responsibilitiesMapping.setAttributeName("responsibilities");
        responsibilitiesMapping.setField(field);
        responsibilitiesMapping.setUsesSingleNode(true);

        p.getDescriptor(Employee.class).addMapping(responsibilitiesMapping);
        setProject(p);
    }

    protected Object getControlObject() {
        Vector responsibilities = new Vector();
        responsibilities.addElement(CONTROL_RESPONSIBILITY1);
        responsibilities.addElement(CONTROL_RESPONSIBILITY2);
        responsibilities.addElement(CONTROL_RESPONSIBILITY3);

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.MONTH, Calendar.JANUARY);
        cal.set(Calendar.DAY_OF_MONTH, 1);
        cal.set(Calendar.YEAR, 2001);
        Date date = cal.getTime();

        responsibilities.addElement(date);

        Employee employee = new Employee();
        employee.setID(CONTROL_ID);
        employee.setResponsibilities(responsibilities);
        return employee;
    }
}
