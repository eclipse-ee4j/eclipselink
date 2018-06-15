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

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.linked.address.Address;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.linked.article.Article;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.linked.author.Author;
import org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlschema.namespace.linked.book.Book;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.xml.sax.SAXException;

/**
 * Tests marshaling of model consisting of four name-spaces, three of them reference other name-spaces.
 *
 * <p>
 * The tests use external meta-data sources to define various configurations of the name-spaces.
 * <p>
 * The model is defined so there are two possible root elements, {@link Book} and {@link Article}.
 * Both of them reference {@link Author} which references {@link Address}.
 * All classes reside in separate XML schema name-spaces.
 */
@RunWith(Parameterized.class)
public class LinkedNamespacesTestCases {
    private static final String JAXB_FACTORY_KEY = "javax.xml.bind.context.factory";
    private static final String MOXY_JAXB_FACTORY = "org.eclipse.persistence.jaxb.JAXBContextFactory";
    private static String jaxbFactoryBackup;

    @Parameters(name="{index}: {0}")
    public static Collection<String> parameters() throws Exception {
        List<String> parameters = new LinkedList<>();
        parameters.add("all-prefixed");
        parameters.add("no-prefix");
        parameters.add("all-default");
        parameters.add("one-default");
        parameters.add("one-prefix");
        parameters.add("clashing-prefix");
        return parameters;
    }

    @BeforeClass
    public static void setJAXBProvider() {
        jaxbFactoryBackup = System.getProperty(JAXB_FACTORY_KEY);
        System.setProperty(JAXB_FACTORY_KEY, MOXY_JAXB_FACTORY);
    }

    @AfterClass
    public static void unsetJAXBProvider() {
        if (jaxbFactoryBackup == null) {
            System.clearProperty(JAXB_FACTORY_KEY);
        } else {
            System.setProperty(JAXB_FACTORY_KEY, jaxbFactoryBackup);
        }
    }

    private final String metadataVariant;

    public LinkedNamespacesTestCases(String metadataVariant) {
        super();
        this.metadataVariant = metadataVariant;
    }

    /**
     * Tests that marshaling of {@link Article} object results in valid XML.
     *
     * @throws Exception
     */
    @Test
    public void testArticleValidateOutput() throws Exception {
        final JAXBContext ctx = createContext();
        final Marshaller m = ctx.createMarshaller();
        final StringWriter writer = new StringWriter();
        final StringBuffer buff = writer.getBuffer();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(createArticle(), writer);
        createArticleValidator().validate(
                new StreamSource(new StringReader(buff.toString())));
    }

    /**
     * Tests that marshaling of {@link Book} object results in valid XML.
     *
     * @throws Exception
     */
    @Test
    public void testBookValidateOutput() throws Exception {
        final JAXBContext ctx = createContext();
        final Marshaller m = ctx.createMarshaller();
        final StringWriter writer = new StringWriter();
        final StringBuffer buff = writer.getBuffer();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(createBook(), writer);
        createBookValidator().validate(
                new StreamSource(new StringReader(buff.toString())));
    }

    /**
     * Tests that reading a marshaled {@link Article} object results in an object equal to the original.
     *
     * @throws Exception
     */
    @Test
    public void testArticleRoundTrip() throws Exception {
        final Article data = createArticle();
        final JAXBContext ctx = createContext();
        final Marshaller m = ctx.createMarshaller();
        final StringWriter writer = new StringWriter();
        final StringBuffer buff = writer.getBuffer();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(data, writer);
        final Unmarshaller um = ctx.createUnmarshaller();
        Object result = um.unmarshal(new StringReader(buff.toString()));
        assertEquals(data, result);
    }

    /**
     * Tests that reading a marshaled {@link Book} object results in an object equal to the original.
     *
     * @throws Exception
     */
    @Test
    public void testBookRoundTrip() throws Exception {
        final Book data = createBook();
        final JAXBContext ctx = createContext();
        final Marshaller m = ctx.createMarshaller();
        final StringWriter writer = new StringWriter();
        final StringBuffer buff = writer.getBuffer();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        m.marshal(data, writer);
        final Unmarshaller um = ctx.createUnmarshaller();
        Object result = um.unmarshal(new StringReader(buff.toString()));
        assertEquals(data, result);
    }

    private JAXBContext createContext() throws JAXBException {
        Map<String, Object> properties = new HashMap<String, Object>();
        List<Object> metadataSources = new LinkedList<>();
        metadataSources.add("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/" + metadataVariant + "-address.json");
        metadataSources.add("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/" + metadataVariant + "-author.json");
        metadataSources.add("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/" + metadataVariant + "-article.json");
        metadataSources.add("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/" + metadataVariant + "-book.json");
        properties.put(JAXBContextProperties.OXM_METADATA_SOURCE, metadataSources);
        return JAXBContext.newInstance(new Class[] {}, properties);
    }

    private Article createArticle() {
        Article article = new Article();
        article.name = "Fossils Shed New Light on Human-Gorilla Split";
        article.author = new Author();
        article.author.firstName = "Charles Q.";
        article.author.lastName = "Choi";
        article.author.address = new Address();
        article.author.address.country = "United States of America";
        return article;
    }

    private Book createBook() {
        Book book = new Book();
        book.name = "Nineteen Eighty-Four";
        book.author = new Author();
        book.author.firstName = "George";
        book.author.lastName = "Orwell";
        book.author.address = new Address();
        book.author.address.country = "United Kingdom of Great Britain and Northern Ireland";
        return book;
    }

    public Validator createArticleValidator() throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema articleSchema = factory.newSchema(Thread.currentThread().getContextClassLoader().getResource("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/article.xsd"));
        return articleSchema.newValidator();
    }

    public Validator createBookValidator() throws SAXException {
        SchemaFactory factory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
        Schema articleSchema = factory.newSchema(Thread.currentThread().getContextClassLoader().getResource("org/eclipse/persistence/testing/jaxb/externalizedmetadata/xmlschema/namespace/linked/book.xsd"));
        return articleSchema.newValidator();
    }
}
