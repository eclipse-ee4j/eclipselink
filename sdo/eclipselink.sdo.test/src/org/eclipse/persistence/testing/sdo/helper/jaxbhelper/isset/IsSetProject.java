/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.isset;


import java.util.ArrayList;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.platform.xml.XMLSchemaReference;
import org.eclipse.persistence.sessions.Project;

public class IsSetProject extends Project {

    public IsSetProject() {
        super();
        this.addDescriptor(getRootDescriptor());
        this.addDescriptor(getChildDescriptor());
    }
    
    private XMLDescriptor getChildDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Child.class);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("tns", "urn:isset");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);
        
        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/tns:child");
        schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(schemaReference);
        
        XMLDirectMapping mapping = new XMLDirectMapping();
        mapping.setAttributeName("value");
        mapping.setXPath("tns:value/text()");
        xmlDescriptor.addMapping(mapping);
        
        return xmlDescriptor;
    }
    
    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Root.class);
        xmlDescriptor.setDefaultRootElement("tns:root-element");
        
        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/tns:root");
        schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(schemaReference);
        
        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("tns", "urn:isset");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);
        
        XMLCompositeObjectMapping mapping = new XMLCompositeObjectMapping();
        mapping.setReferenceClass(Child.class);
        mapping.setContainerAttributeName("container");
        mapping.setAttributeName("childProperty");
        mapping.setXPath("tns:child");
        xmlDescriptor.addMapping(mapping);
        
        XMLCompositeCollectionMapping mapping2 = new XMLCompositeCollectionMapping();
        mapping2.setReferenceClass(Child.class);
        mapping2.setContainerAttributeName("container");
        mapping2.setAttributeName("childCollectionProperty");
        mapping2.setXPath("tns:child-many");
        mapping2.getContainerPolicy().setContainerClass(ArrayList.class);
        xmlDescriptor.addMapping(mapping2);
        
        return xmlDescriptor;
    }
    
}
