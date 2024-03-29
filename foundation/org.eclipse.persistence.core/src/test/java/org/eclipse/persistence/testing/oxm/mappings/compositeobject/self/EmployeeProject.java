/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self;

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.Project;

import javax.xml.namespace.QName;

public class EmployeeProject extends Project {
    public EmployeeProject() {
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getPeriodDescriptor());
    }

    protected XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.Employee.class);
        descriptor.setDefaultRootElement("employee");

        XMLCompositeObjectMapping periodMapping = new XMLCompositeObjectMapping();
        periodMapping.setAttributeName("_StartDateAndEndDate");
        periodMapping.setXPath(".");
        periodMapping.setGetMethodName("getStartDateAndEndDate");
        periodMapping.setSetMethodName("setStartDateAndEndDate");
        periodMapping.setReferenceClass(Employee.Period.class);
        descriptor.addMapping(periodMapping);

        return descriptor;
    }

    private XMLDescriptor getPeriodDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Employee.Period.class);

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

        return descriptor;
    }
}
