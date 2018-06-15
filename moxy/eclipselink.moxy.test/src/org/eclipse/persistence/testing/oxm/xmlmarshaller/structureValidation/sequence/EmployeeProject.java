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
package org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.sequence;

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
        descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.sequence.Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLCompositeObjectMapping periodMapping = new XMLCompositeObjectMapping();
        periodMapping.setAttributeName("_StartDateAndEndDate");
        periodMapping.setXPath(".");
        periodMapping.setGetMethodName("getStartDateAndEndDate");
        periodMapping.setSetMethodName("setStartDateAndEndDate");
        periodMapping.setReferenceClass(Employee.Period.class);
        descriptor.addMapping(periodMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/xmlmarshaller/Employee_Sequence.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaReference.ELEMENT);
        schemaRef.setSchemaContext("/employee");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }

    private XMLDescriptor getPeriodDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.xmlmarshaller.structureValidation.sequence.Employee.Period.class);

        XMLDirectMapping startDateMapping = new XMLDirectMapping();
        startDateMapping.setAttributeName("_StartDate");
        startDateMapping.setGetMethodName("getStartDate");
        startDateMapping.setSetMethodName("setStartDate");
        QName qname = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, XMLConstants.DATE);
        XMLField field = new XMLField("startDate/text()");
        field.setSchemaType(qname);
        startDateMapping.setField(field);
        descriptor.addMapping(startDateMapping);

        XMLDirectMapping endDateMapping = new XMLDirectMapping();
        endDateMapping.setAttributeName("_EndDate");
        endDateMapping.setGetMethodName("getEndDate");
        endDateMapping.setSetMethodName("setEndDate");
        field = new XMLField("endDate/text()");
        field.setSchemaType(qname);
        endDateMapping.setField(field);
        descriptor.addMapping(endDateMapping);

        URL schemaURL = ClassLoader.getSystemResource("org/eclipse/persistence/testing/oxm/xmlmarshaller/Employee_Sequence.xsd");
        XMLSchemaURLReference schemaRef = new XMLSchemaURLReference(schemaURL);
        schemaRef.setType(XMLSchemaReference.ELEMENT);
        schemaRef.setSchemaContext("/employee");
        descriptor.setSchemaReference(schemaRef);

        return descriptor;
    }
}
