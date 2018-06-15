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
package org.eclipse.persistence.testing.oxm.mappings.directcollection.union.xmlattribute;

import javax.xml.namespace.QName;
import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.NamespaceResolver;
import org.eclipse.persistence.oxm.XMLDescriptor;
import org.eclipse.persistence.oxm.XMLUnionField;
import org.eclipse.persistence.oxm.mappings.XMLDirectMapping;
import org.eclipse.persistence.oxm.mappings.XMLCompositeDirectCollectionMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.directcollection.union.Person;

public class SimpleUnionXMLAttributeProject extends Project {
    public SimpleUnionXMLAttributeProject() {
        addDescriptor(getPersonDescriptor());
    }

    private XMLDescriptor getPersonDescriptor() {
        XMLDescriptor descriptor = new XMLDescriptor();
        descriptor.setJavaClass(Person.class);
        descriptor.setDefaultRootElement("person");
        NamespaceResolver resolver = new NamespaceResolver();
        resolver.put(XMLConstants.SCHEMA_INSTANCE_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_INSTANCE_NS_URI);
        resolver.put(XMLConstants.SCHEMA_PREFIX, javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        descriptor.setNamespaceResolver(resolver);

        XMLCompositeDirectCollectionMapping itemsMapping = new XMLCompositeDirectCollectionMapping();
        itemsMapping.setAttributeName("items");
        itemsMapping.setGetMethodName("getItems");
        itemsMapping.setSetMethodName("setItems");
        itemsMapping.useCollectionClass(java.util.ArrayList.class);
        XMLUnionField field = new XMLUnionField("items/@item");
        QName dateQname = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, "date");
        QName integerQName = new QName(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI, "integer");
        field.addSchemaType(dateQname);
        field.addSchemaType(integerQName);
        itemsMapping.setField(field);
        descriptor.addMapping(itemsMapping);

        XMLDirectMapping firstNameMapping = new XMLDirectMapping();
        firstNameMapping.setAttributeName("firstName");
        firstNameMapping.setXPath("first-name/text()");
        descriptor.addMapping(firstNameMapping);

        XMLDirectMapping lastNameMapping = new XMLDirectMapping();
        lastNameMapping.setAttributeName("lastName");
        lastNameMapping.setXPath("last-name/text()");
        descriptor.addMapping(lastNameMapping);

        return descriptor;
    }
}
