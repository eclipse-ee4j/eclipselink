/*
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// Ondrej Cerny
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.linked;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.linked.address.Address;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.linked.author.Author;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.linked.book.Book;

public class PrefixedLinkedNamespacesTestCases extends JAXBWithJSONTestCases{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/book.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/book.json";

    public PrefixedLinkedNamespacesTestCases(String name) throws Exception {
        super(name);
    }

    @Override
    public void setUp() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        super.setUp();
        Type[] types = new Type[0];
        setTypes(types);
    }

    protected Object getControlObject() {
        Book book = new Book();
        book.name = "Nineteen Eighty-Four";
        book.author = new Author();
        book.author.firstName = "George";
        book.author.lastName = "Orwell";
        book.author.address = new Address();
        book.author.address.country = "United Kingdom of Great Britain and Northern Ireland";
        return book;
    }

    public void testSchemaGen() throws Exception {
        // do not test schema generation
    }

    protected Map getProperties() {
        Map<String, Object> properties = new HashMap<String, Object>();
        List<Object> metadataSources = new LinkedList<>();
        metadataSources.add("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/all-prefixed-address.json");
        metadataSources.add("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/all-prefixed-author.json");
        metadataSources.add("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/all-prefixed-article.json");
        metadataSources.add("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/all-prefixed-book.json");
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSources);
        return properties;
    }
}
