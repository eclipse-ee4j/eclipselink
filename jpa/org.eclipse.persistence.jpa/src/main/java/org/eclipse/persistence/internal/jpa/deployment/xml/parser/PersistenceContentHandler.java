/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     06/16/2009-2.0 Guy Pelletier
//       - 277039: JPA 2.0 Cache Usage Settings
package org.eclipse.persistence.internal.jpa.deployment.xml.parser;

import jakarta.persistence.PersistenceUnitTransactionType;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.jdbc.DataSourceImpl;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PersistenceContentHandler implements ContentHandler {
    private static final String NS_URI = "https://jakarta.ee/xml/ns/persistence";
    private static final String NAMESPACE_URI = "http://xmlns.jcp.org/xml/ns/persistence";
    private static final String NAMESPACE_URI_OLD = "http://java.sun.com/xml/ns/persistence";
    private static final String ELEMENT_PERSISTENCE = "persistence";
    private static final String ELEMENT_PERSISTENCE_UNIT = "persistence-unit";
    private static final String ELEMENT_PROVIDER = "provider";
    private static final String ELEMENT_QUALIFIER = "qualifier";
    private static final String ELEMENT_SCOPE = "scope";
    private static final String ELEMENT_JTA_DATA_SOURCE = "jta-data-source";
    private static final String ELEMENT_NON_JTA_DATA_SOURCE = "non-jta-data-source";
    private static final String ELEMENT_MAPPING_FILE = "mapping-file";
    private static final String ELEMENT_JAR_FILE = "jar-file";
    private static final String ELEMENT_CLASS = "class";
    private static final String ELEMENT_EXCLUDE_UNLISTED_CLASSES = "exclude-unlisted-classes";
    private static final String ELEMENT_CACHING = "shared-cache-mode";
    private static final String ELEMENT_VALIDATION_MODE = "validation-mode";
    private static final String ELEMENT_PROPERTY = "property";
    private static final String ATTRIBUTE_NAME = "name";
    private static final String ATTRIBUTE_VALUE = "value";
    private static final String ATTRIBUTE_TRANSACTION_TYPE = "transaction-type";
    private static final String ATTRIBUTE_VERSION = "version";

    // elements not being explicitly handled
    private static final String ELEMENT_PROPERTIES = "properties";
    private static final String ELEMENT_DESCRIPTION = "description";

    private SEPersistenceUnitInfo persistenceUnitInfo;
    private List<SEPersistenceUnitInfo> persistenceUnits;
    private StringBuilder stringBuilder;
    private String version;
    private boolean readCharacters = false;

    public PersistenceContentHandler() {
        super();
        stringBuilder = new StringBuilder();
        persistenceUnits = new ArrayList<>();
    }

   public List<SEPersistenceUnitInfo> getPersistenceUnits() {
        return persistenceUnits;
    }

    @Override
    public void setDocumentLocator(Locator locator) {
        // no-op
    }

    @Override
    public void startDocument() throws SAXException {
        // no-op
    }

    @Override
    public void endDocument() throws SAXException {
        // no-op
    }

    @Override
    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        // no-op
    }

    @Override
    public void endPrefixMapping(String prefix) throws SAXException {
        // no-op
    }

    @Override
    public void startElement(String namespaceURI, String localName, String qName, Attributes attrs) throws SAXException {
        if (NS_URI.equals(namespaceURI) || NAMESPACE_URI.equals(namespaceURI) || NAMESPACE_URI_OLD.equals(namespaceURI)) {
            if (ELEMENT_PERSISTENCE_UNIT.equals(localName)) {
                persistenceUnitInfo = new SEPersistenceUnitInfo();
                persistenceUnitInfo.setPersistenceXMLSchemaVersion(version);
                persistenceUnitInfo.setPersistenceUnitName(attrs.getValue(ATTRIBUTE_NAME));
                String transactionType = attrs.getValue(ATTRIBUTE_TRANSACTION_TYPE);
                if (transactionType != null) {
                    try {
                        persistenceUnitInfo.setTransactionType(PersistenceUnitTransactionType.valueOf(transactionType));
                    } catch (IllegalArgumentException iae) {
                        throw new SAXParseException("Unsupported value '%s' in %s attribute".formatted(transactionType, ATTRIBUTE_TRANSACTION_TYPE), null, iae);
                    }
                }
            } else if (ELEMENT_PROPERTY.equals(localName)) {
                persistenceUnitInfo.getProperties().setProperty(attrs.getValue(ATTRIBUTE_NAME), attrs.getValue(ATTRIBUTE_VALUE));
            } else if (ELEMENT_PERSISTENCE.equals(localName)) {
                version = attrs.getValue(ATTRIBUTE_VERSION);
            } else {
                // just read everything else and handle it in endElement
                readCharacters = true;
            }
        }
    }

    @Override
    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (NS_URI.equals(namespaceURI) || NAMESPACE_URI.equals(namespaceURI) || NAMESPACE_URI_OLD.equals(namespaceURI)) {
            String content = stringBuilder.toString().trim();
            stringBuilder.delete(0, stringBuilder.length());
            readCharacters = false;
            switch (localName) {
                case ELEMENT_PROVIDER:
                    persistenceUnitInfo.setPersistenceProviderClassName(content);
                    break;
                case ELEMENT_JTA_DATA_SOURCE:
                    // Create a dummy DataSource that will
                    // throw an exception on access
                    persistenceUnitInfo.setJtaDataSource(new DataSourceImpl(content, null, null, null));
                    break;
                case ELEMENT_NON_JTA_DATA_SOURCE:
                    // Create a dummy DataSource that will
                    // throw an exception on access
                    persistenceUnitInfo.setNonJtaDataSource(new DataSourceImpl(content, null, null, null));
                    break;
                case ELEMENT_MAPPING_FILE:
                    persistenceUnitInfo.getMappingFileNames().add(content);
                    break;
                case ELEMENT_JAR_FILE:
                    persistenceUnitInfo.getJarFiles().add(content);
                    break;
                case ELEMENT_CLASS:
                    persistenceUnitInfo.getManagedClassNames().add(content);
                    break;
                case ELEMENT_EXCLUDE_UNLISTED_CLASSES:
                    // default <exclude-unlisted-classes/>  to true as well (an empty content)
                    persistenceUnitInfo.setExcludeUnlistedClasses("true".equals(content) || "1".equals(content) || content.isEmpty());
                    break;
                case ELEMENT_CACHING:
                    try {
                        persistenceUnitInfo.setSharedCacheMode(content);
                    } catch (IllegalArgumentException iae) {
                        throw new SAXParseException("Unsupported value '%s' in {%s}%s".formatted(content, namespaceURI, localName), null, iae);
                    }
                    break;
                case ELEMENT_VALIDATION_MODE:
                    try {
                        persistenceUnitInfo.setValidationMode(content);
                    } catch (IllegalArgumentException iae) {
                        throw new SAXParseException("Unsupported value '%s' in {%s}%s".formatted(content, namespaceURI, localName), null, iae);
                    }
                    break;
                case ELEMENT_PERSISTENCE_UNIT:
                    if (persistenceUnitInfo != null){
                        persistenceUnits.add(persistenceUnitInfo);
                        persistenceUnitInfo = null;
                    }
                    break;
                case ELEMENT_QUALIFIER:
                    List<String> qualifiers = Arrays.stream(content.split(",")).map(String::trim).toList();
                    persistenceUnitInfo.setQualifierAnnotationNames(qualifiers);
                    break;
                case ELEMENT_SCOPE:
                    persistenceUnitInfo.setScopeAnnotationName(content);
                    break;
                case ELEMENT_PERSISTENCE, ELEMENT_DESCRIPTION, ELEMENT_PROPERTIES, ELEMENT_PROPERTY:
                    // ignored elements
                    break;
                default:
                    throw new SAXParseException("Unhandled element: {" + namespaceURI + "}" + localName, null);
            }
        }
    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        if (readCharacters) {
            stringBuilder.append(ch, start, length);
        }
    }

    @Override
    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        // no-op
    }

    @Override
    public void processingInstruction(String target, String data) throws SAXException {
        // no-op
    }

    @Override
    public void skippedEntity(String name) throws SAXException {
        // no-op
    }
}
