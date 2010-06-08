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
package org.eclipse.persistence.testing.oxm.xmlconversionmanager;

import org.eclipse.persistence.oxm.XMLField;
import org.eclipse.persistence.oxm.*;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.descriptors.*;
import org.eclipse.persistence.sessions.Project;

public class DatatypeProject extends Project {

    private NamespaceResolver namespaceResolver;

    public DatatypeProject() {
        super();

        namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
        namespaceResolver.put("prefix", "mynamespaceuri");

        addDescriptor(getDatatypeEmployeeDescriptor());
    }
    
    private XMLDescriptor getDatatypeEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(DatatypeEmployee.class);
        descriptor.setNamespaceResolver(namespaceResolver);
        descriptor.setDefaultRootElement("prefix:emp");

        XMLDirectMapping nameMapping = new XMLDirectMapping();
        nameMapping.setAttributeName("name");
        nameMapping.setXPath("prefix:name/text()");
        descriptor.addMapping(nameMapping);

        XMLDirectMapping deptNumberMapping = new XMLDirectMapping();
        deptNumberMapping.setAttributeName("deptNumber");
        deptNumberMapping.setXPath("prefix:dept-number/text()");
        descriptor.addMapping(deptNumberMapping);

        XMLDirectMapping birthDateMapping = new XMLDirectMapping();
        birthDateMapping.setAttributeName("birthDate");
        birthDateMapping.setXPath("prefix:birth-date/text()");
        descriptor.addMapping(birthDateMapping);

        XMLDirectMapping hireDateMapping = new XMLDirectMapping();
        hireDateMapping.setAttributeName("hireDate");
        hireDateMapping.setXPath("prefix:hire-date/text()");
        descriptor.addMapping(hireDateMapping);

        XMLDirectMapping vacationTakenMapping = new XMLDirectMapping();
        vacationTakenMapping.setAttributeName("vacationTaken");
        vacationTakenMapping.setXPath("prefix:vacation-taken/text()");
        descriptor.addMapping(vacationTakenMapping);

        return descriptor;
    }

}
