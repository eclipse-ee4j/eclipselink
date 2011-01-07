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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.group;

import java.net.URL;
import javax.xml.namespace.QName;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.schema.XMLSchemaReference;
import org.eclipse.persistence.oxm.schema.XMLSchemaURLReference;
import org.eclipse.persistence.sessions.Project;

public class EmployeeProject extends Project {
    public EmployeeProject() {
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getPeriodDescriptor());
    }

    private XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.group.Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLCompositeObjectMapping periodMapping = new XMLCompositeObjectMapping();
        periodMapping.setAttributeName("_G1");
        periodMapping.setXPath(".");
        periodMapping.setGetMethodName("getG1");
        periodMapping.setSetMethodName("setG1");
        periodMapping.setReferenceClass(org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.Period.class);
        descriptor.addMapping(periodMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/xmlmarshaller/Employee_Group.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaReference.ELEMENT);
        schemaRef.setSchemaContext("/employee");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }

    private XMLDescriptor getPeriodDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.Period.class);

        XMLDirectMapping startDateMapping = new XMLDirectMapping();
        startDateMapping.setAttributeName("_StartDate");
        startDateMapping.setGetMethodName("getStartDate");
        startDateMapping.setSetMethodName("setStartDate");
        QName qname = new QName(XMLConstants.SCHEMA_URL, XMLConstants.DATE);
        XMLField field = new XMLField("startDate/text()");
        field.setSchemaType(qname);
        startDateMapping.setField(field);
        descriptor.addMapping(startDateMapping);

        XMLDirectMapping endDateMapping = new XMLDirectMapping();
        endDateMapping.setAttributeName("_EndDate");
        endDateMapping.setGetMethodName("getEndDate");
        endDateMapping.setSetMethodName("setEndDate");
        qname = new QName(XMLConstants.SCHEMA_URL, XMLConstants.DATE);
        field = new XMLField("endDate/text()");
        field.setSchemaType(qname);
        endDateMapping.setField(field);
        descriptor.addMapping(endDateMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/xmlmarshaller/Employee_Group.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaReference.GROUP);
        schemaRef.setSchemaContext("/G1");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }
}
