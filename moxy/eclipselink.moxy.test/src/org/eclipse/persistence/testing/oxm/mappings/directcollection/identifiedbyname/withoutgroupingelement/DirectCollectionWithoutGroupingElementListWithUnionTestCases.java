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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement;

import java.math.BigInteger;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;
import javax.xml.namespace.QName;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.XMLUnionField;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.Employee;

public class DirectCollectionWithoutGroupingElementListWithUnionTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/identifiedbyname/withoutgroupingelement/DirectCollectionWithoutGroupingElementListWithUnion.xml";
    private final static BigInteger CONTROL_RESPONSIBILITY1 = new BigInteger("10");
    private final static BigInteger CONTROL_RESPONSIBILITY2 = new BigInteger("20");
    private final static BigInteger CONTROL_RESPONSIBILITY3 = new BigInteger("30");

    public DirectCollectionWithoutGroupingElementListWithUnionTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new DirectCollectionWithoutGroupingElementIdentifiedByNameProject();
        p.getDescriptor(Employee.class).removeMappingForAttributeName("responsibilities");

        XMLUnionField field = new XMLUnionField("responsibility/text()");
        QName integerQName = new QName(XMLConstants.SCHEMA_URL, "integer");
        QName qname = new QName(XMLConstants.SCHEMA_URL, "date");
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
        employee.setID(123);
        employee.setResponsibilities(responsibilities);
        return employee;
    }
}
