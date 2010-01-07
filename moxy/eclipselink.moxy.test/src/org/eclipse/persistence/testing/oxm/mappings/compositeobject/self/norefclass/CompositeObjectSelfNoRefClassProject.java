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
 *     Denise Smith - May 8/2009 
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.Address;
import org.eclipse.persistence.testing.oxm.xmlmarshaller.setschemas.SetSchemasTestCases;

public class CompositeObjectSelfNoRefClassProject extends Project {
    public CompositeObjectSelfNoRefClassProject() {
        addDescriptor(getEmployeeDescriptor());
        addDescriptor(getAddressDescriptor());  
    }

    protected XMLDescriptor getEmployeeDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Root.class);
        descriptor.setDefaultRootElement("root");

        XMLCompositeObjectMapping theObjectMapping = new XMLCompositeObjectMapping();
        theObjectMapping.setAttributeName("theObject");
        theObjectMapping.setXPath(".");                
        descriptor.addMapping(theObjectMapping);
        
        return descriptor;
    }
    
    protected XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);
        descriptor.setDefaultRootElement("address");

        XMLDirectMapping streetMapping = new XMLDirectMapping();
        streetMapping.setAttributeName("street");
        streetMapping.setXPath("street/text()");                
        descriptor.addMapping(streetMapping);
        
        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
		schemaReference.setSchemaContext("/addressType");
		schemaReference.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
		descriptor.setSchemaReference(schemaReference);
        
        return descriptor;
    }
}
