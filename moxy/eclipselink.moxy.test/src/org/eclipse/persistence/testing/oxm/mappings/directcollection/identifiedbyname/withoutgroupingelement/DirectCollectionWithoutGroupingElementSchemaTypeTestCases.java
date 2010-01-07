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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.identifiedbyname.withoutgroupingelement;

import java.util.Vector;
import java.util.Calendar;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.Employee;

public class DirectCollectionWithoutGroupingElementSchemaTypeTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/directcollection/identifiedbyname/withoutgroupingelement/DirectCollectionWithoutGroupingElementSchemaType.xml";
    private final static int CONTROL_ID = 123;

    public DirectCollectionWithoutGroupingElementSchemaTypeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new DirectCollectionWithoutGroupingElementIdentifiedByNameProject();
        XMLCompositeDirectCollectionMapping mapping = (XMLCompositeDirectCollectionMapping)p.getDescriptor(Employee.class).getMappingForAttributeName("responsibilities");
        mapping.setAttributeElementClass(java.util.Calendar.class);
        ((XMLField)mapping.getField()).setSchemaType(XMLConstants.DATE_QNAME);
        setProject(p);
    }

    protected Object getControlObject() {
        Vector responsibilities = new Vector();
        Calendar cal1 = Calendar.getInstance();
        cal1.clear();
        cal1.set(Calendar.MONTH, Calendar.JANUARY);
        cal1.set(Calendar.DAY_OF_MONTH, 1);
        cal1.set(Calendar.YEAR, 2001);

        Calendar cal2 = Calendar.getInstance();
        cal2.clear();
        cal2.set(Calendar.MONTH, Calendar.DECEMBER);
        cal2.set(Calendar.DAY_OF_MONTH, 27);
        cal2.set(Calendar.YEAR, 2004);

        Calendar cal3 = Calendar.getInstance();
        cal3.clear();
        cal3.set(Calendar.MONTH, Calendar.MAY);
        cal3.set(Calendar.DAY_OF_MONTH, 6);
        cal3.set(Calendar.YEAR, 2002);

        responsibilities.addElement(cal1);
        responsibilities.addElement(cal2);
        responsibilities.addElement(cal3);

        Employee employee = new Employee();
        employee.setID(CONTROL_ID);
        employee.setResponsibilities(responsibilities);
        return employee;
    }
}
