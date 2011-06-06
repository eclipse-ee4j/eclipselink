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
*     bdoughan - March 11/2010 - 2.0.2 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositecollection.norefclass;

import java.util.ArrayList;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeCollectionMapping;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.sessions.Project;

public class CustomerProject extends Project {

    public CustomerProject() {
        this.addDescriptor(getCustomerDescriptor());
        this.addDescriptor(getAddressDescriptor());
    }

    private XMLDescriptor getCustomerDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Customer.class);
        descriptor.setDefaultRootElement("customer");

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.setDefaultNamespaceURI("urn:customer");
        descriptor.setNamespaceResolver(nsResolver);

        XMLCompositeCollectionMapping addressMapping = new XMLCompositeCollectionMapping();
        addressMapping.setAttributeName("address");
        addressMapping.setXPath("address");
        addressMapping.getContainerPolicy().setContainerClass(ArrayList.class);
        descriptor.addMapping(addressMapping);

        return descriptor;
    }

    private XMLDescriptor getAddressDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Address.class);

        NamespaceResolver nsResolver = new NamespaceResolver();
        nsResolver.setDefaultNamespaceURI("urn:customer");
        descriptor.setNamespaceResolver(nsResolver);

        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/address-type");
        schemaReference.setType(XMLSchemaClassPathReference.COMPLEX_TYPE);
        descriptor.setSchemaReference(schemaReference);

        return descriptor;
    }
}