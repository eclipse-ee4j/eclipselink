/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.oppositeproperty;

import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCollectionReferenceMapping;
import org.eclipse.persistence.oxm.mappings.XMLObjectReferenceMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.oxm.schema.XMLSchemaClassPathReference;
import org.eclipse.persistence.platform.xml.XMLSchemaReference;

public class OppositeProject extends Project {

    public OppositeProject() {
        super();
        this.addDescriptor(getRootDescriptor());
        this.addDescriptor(getChild1Descriptor());
        this.addDescriptor(getChild2Descriptor());
    }

    private XMLDescriptor getChild1Descriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Child1.class);
        xmlDescriptor.addPrimaryKeyFieldName("@id");

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("tns", "urn:opposite");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);

        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/tns:child1");
        schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(schemaReference);

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        xmlDescriptor.addMapping(idMapping);

        XMLObjectReferenceMapping child2Mapping = new XMLObjectReferenceMapping();
        child2Mapping.setReferenceClass(Child2.class);
        child2Mapping.setAttributeName("child2");
        child2Mapping.addSourceToTargetKeyFieldAssociation("tns:child2/text()", "@id");
        xmlDescriptor.addMapping(child2Mapping);

        XMLCollectionReferenceMapping child2CollectionMapping = new XMLCollectionReferenceMapping();
        child2CollectionMapping.setReferenceClass(Child2.class);
        child2CollectionMapping.setAttributeName("child2Collection");
        child2CollectionMapping.addSourceToTargetKeyFieldAssociation("tns:child2collection/text()", "@id");
        xmlDescriptor.addMapping(child2CollectionMapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getChild2Descriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Child2.class);
        xmlDescriptor.addPrimaryKeyFieldName("@id");

        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/tns:child2");
        schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(schemaReference);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("tns", "urn:opposite");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);

        XMLDirectMapping idMapping = new XMLDirectMapping();
        idMapping.setAttributeName("id");
        idMapping.setXPath("@id");
        xmlDescriptor.addMapping(idMapping);

        XMLObjectReferenceMapping child1Mapping = new XMLObjectReferenceMapping();
        child1Mapping.setReferenceClass(Child1.class);
        child1Mapping.setAttributeName("child1");
        child1Mapping.addSourceToTargetKeyFieldAssociation("tns:child1/text()", "@id");
        xmlDescriptor.addMapping(child1Mapping);

        return xmlDescriptor;
    }

    private XMLDescriptor getRootDescriptor() {
        XMLDescriptor xmlDescriptor = new XMLDescriptor();
        xmlDescriptor.setJavaClass(Root.class);

        XMLSchemaClassPathReference schemaReference = new XMLSchemaClassPathReference();
        schemaReference.setSchemaContext("/tns:root");
        schemaReference.setType(XMLSchemaReference.COMPLEX_TYPE);
        xmlDescriptor.setSchemaReference(schemaReference);

        NamespaceResolver namespaceResolver = new NamespaceResolver();
        namespaceResolver.put("tns", "urn:opposite");
        xmlDescriptor.setNamespaceResolver(namespaceResolver);

        XMLCompositeObjectMapping child1Mapping = new XMLCompositeObjectMapping();
        child1Mapping.setAttributeName("child1");
        child1Mapping.setXPath("tns:child1");
        xmlDescriptor.addMapping(child1Mapping);

        XMLCompositeObjectMapping child2Mapping = new XMLCompositeObjectMapping();
        child2Mapping.setAttributeName("child2");
        child2Mapping.setXPath("tns:child2");
        xmlDescriptor.addMapping(child2Mapping);

        return xmlDescriptor;
    }

}
