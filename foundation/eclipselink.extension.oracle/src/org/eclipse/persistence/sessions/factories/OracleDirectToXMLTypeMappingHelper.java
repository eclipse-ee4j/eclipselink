/*******************************************************************************
 * Copyright (c) 1998, 2007 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0, which accompanies this distribution
 * and is available at http://www.eclipse.org/legal/epl-v10.html.
 *
 * Contributors:
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.sessions.factories;

import org.eclipse.persistence.internal.codegen.NonreflectiveMethodDefinition;
import org.eclipse.persistence.mappings.DatabaseMapping;
import org.eclipse.persistence.mappings.DirectToFieldMapping;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.mappings.xdb.DirectToXMLTypeMapping;
import org.eclipse.persistence.sessions.Project;

public class OracleDirectToXMLTypeMappingHelper extends DirectToXMLTypeMappingHelper {

    /**
     * 
     */
    public void addClassIndicator(XMLDescriptor descriptor) {
    }
    
    /**
     * 
     */
    public void writeShouldreadWholeDocument(NonreflectiveMethodDefinition method, String mappingName, DatabaseMapping mapping) {
        method.addLine(mappingName + ".setShouldReadWholeDocument(" + ((DirectToXMLTypeMapping)mapping).shouldReadWholeDocument() + ");");
    }
    
    /**
     * Invoked from a descriptor is not found.
     */
    public void addXDBDescriptors(String name, Project project) {
        if (project.getDescriptorForAlias(name) == null){
            XMLDescriptor descriptor = new XMLDescriptor();
    
            descriptor.setJavaClass(DirectToXMLTypeMapping.class);
            descriptor.descriptorIsAggregate();
            descriptor.getInheritancePolicy().setParentClass(DirectToFieldMapping.class);
    
            XMLDirectMapping directtofieldmapping = new XMLDirectMapping();
            directtofieldmapping.setAttributeName("shouldReadWholeDocument");
            directtofieldmapping.setGetMethodName("shouldReadWholeDocument");
            directtofieldmapping.setSetMethodName("setShouldReadWholeDocument");
            directtofieldmapping.setXPath("toplink:read-whole-document/text()");
            directtofieldmapping.setNullValue(new Boolean(false));
            descriptor.addMapping(directtofieldmapping);
    
            project.addDescriptor(descriptor);
        }
    }
    
}
