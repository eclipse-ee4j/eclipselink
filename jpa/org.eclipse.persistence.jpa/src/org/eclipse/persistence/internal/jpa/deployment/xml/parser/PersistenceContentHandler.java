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
 *     Oracle - initial API and implementation from Oracle TopLink
 *     06/16/2009-2.0 Guy Pelletier 
 *       - 277039: JPA 2.0 Cache Usage Settings
 ******************************************************************************/  
package org.eclipse.persistence.internal.jpa.deployment.xml.parser;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;

import java.util.Vector;
import javax.persistence.spi.PersistenceUnitTransactionType;

import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.jdbc.DataSourceImpl;

public class PersistenceContentHandler implements ContentHandler {
    private static final String NAMESPACE_URI = "http://java.sun.com/xml/ns/persistence";
    private static final String ELEMENT_PERSISTENCE_UNIT = "persistence-unit";
    private static final String ELEMENT_PROVIDER = "provider";
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

    private SEPersistenceUnitInfo persistenceUnitInfo;
    private Vector<SEPersistenceUnitInfo> persistenceUnits;
    private StringBuffer stringBuffer;
    private boolean readCharacters = false;

    public PersistenceContentHandler() {
        super();
        stringBuffer = new StringBuffer();
        persistenceUnits = new Vector();
    }

   public Vector<SEPersistenceUnitInfo> getPersistenceUnits() {
        return persistenceUnits;
    }

    public void setDocumentLocator(Locator locator) {
    }

    public void startDocument() throws SAXException {
    }

    public void endDocument() throws SAXException {
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
    }

    public void endPrefixMapping(String prefix) throws SAXException {
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (NAMESPACE_URI.equals(namespaceURI)) {
            if (ELEMENT_PERSISTENCE_UNIT.equals(localName)) {
                persistenceUnitInfo = new SEPersistenceUnitInfo();
                persistenceUnitInfo.setPersistenceUnitName(atts.getValue(ATTRIBUTE_NAME));
                String transactionType = atts.getValue(ATTRIBUTE_TRANSACTION_TYPE);
                if(transactionType != null) {
                    persistenceUnitInfo.setTransactionType(PersistenceUnitTransactionType.valueOf(transactionType));
                }
                return;
            } else if (ELEMENT_PROPERTY.equals(localName)) {
                String name = atts.getValue(ATTRIBUTE_NAME);
                String value = atts.getValue(ATTRIBUTE_VALUE);
                persistenceUnitInfo.getProperties().setProperty(name, value);
            } else if (ELEMENT_PROVIDER.equals(localName)) {
                readCharacters = true;
                return;
            } else if (ELEMENT_JTA_DATA_SOURCE.equals(localName)) {
                readCharacters = true;
                return;
            } else if (ELEMENT_NON_JTA_DATA_SOURCE.equals(localName)) {
                readCharacters = true;
                return;
            } else if (ELEMENT_MAPPING_FILE.equals(localName)) {
                readCharacters = true;
                return;
            } else if (ELEMENT_JAR_FILE.equals(localName)) {
                readCharacters = true;
                return;
            } else if (ELEMENT_EXCLUDE_UNLISTED_CLASSES.equals(localName)) {
                readCharacters = true;
                return;
            } else if (ELEMENT_CACHING.equals(localName)) {
                readCharacters = true;
                return;
            } else if (ELEMENT_VALIDATION_MODE.equals(localName)) {
                readCharacters = true;
                return;
            } else if (ELEMENT_CLASS.equals(localName)) {
                readCharacters = true;
                return;
            }
        }
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        String string = stringBuffer.toString().trim();
        stringBuffer.delete(0, stringBuffer.length());
        readCharacters = false;

        if (NAMESPACE_URI.equals(namespaceURI)) {
            if (ELEMENT_PROVIDER.equals(localName)) {
                persistenceUnitInfo.setPersistenceProviderClassName(string);
                return;
            } else if (ELEMENT_JTA_DATA_SOURCE.equals(localName)) {
                persistenceUnitInfo.setJtaDataSource(
                    // Create a dummy DataSource that will 
                    // throw an exception on access
                    new DataSourceImpl(string, null, null, null));
                return;
            } else if (ELEMENT_NON_JTA_DATA_SOURCE.equals(localName)) {
                persistenceUnitInfo.setNonJtaDataSource(
                    // Create a dummy DataSource that will 
                    // throw an exception on access
                    new DataSourceImpl(string, null, null, null));
                return;
            } else if (ELEMENT_MAPPING_FILE.equals(localName)) {
                persistenceUnitInfo.getMappingFileNames().add(string);
                return;
            } else if (ELEMENT_JAR_FILE.equals(localName)) {
                persistenceUnitInfo.getJarFiles().add(string);
                return;
            } else if (ELEMENT_CLASS.equals(localName)) {
                persistenceUnitInfo.getManagedClassNames().add(string);
                return;
            } else if (ELEMENT_EXCLUDE_UNLISTED_CLASSES.equals(localName)) {
                if (string.equals("true") || string.equals("1")){
                    persistenceUnitInfo.setExcludeUnlistedClasses(true);
                } else {
                    persistenceUnitInfo.setExcludeUnlistedClasses(false);
                }
                return;
            } else if (ELEMENT_CACHING.equals(localName)) {
                persistenceUnitInfo.setSharedCacheMode(string);
            } else if (ELEMENT_VALIDATION_MODE.equals(localName)) {
                persistenceUnitInfo.setValidationMode(string);
            } else if (ELEMENT_PERSISTENCE_UNIT.equals(localName)) {
                if (persistenceUnitInfo != null){
                    persistenceUnits.add(persistenceUnitInfo);
                    persistenceUnitInfo = null;
                }
            } 
        }
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (readCharacters) {
            stringBuffer.append(ch, start, length);
        }
    }

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
    }

    public void processingInstruction(String target, String data) throws SAXException {
    }

    public void skippedEntity(String name) throws SAXException {
    }
}
