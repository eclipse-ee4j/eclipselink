/*
 * Copyright (c) 1998, 2023 Oracle and/or its affiliates. All rights reserved.
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
//     dclarke - Dynamic Persistence
//       http://wiki.eclipse.org/EclipseLink/Development/Dynamic
//       (https://bugs.eclipse.org/bugs/show_bug.cgi?id=200045)
//     mnorman - tweaks to work from Ant command-line,
//               get database properties from System, etc.
//
//     01/08/2012-2.5 Guy Pelletier
//       - 389090: JPA 2.1 DDL Generation Support
package org.eclipse.persistence.testing.tests.jpa.dynamic;

//javase imports

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.PersistenceConfiguration;
import jakarta.persistence.spi.PersistenceProvider;
import jakarta.persistence.spi.PersistenceUnitInfo;
import jakarta.persistence.spi.ProviderUtil;
import org.eclipse.persistence.config.PersistenceUnitProperties;
import org.eclipse.persistence.internal.jpa.EntityManagerFactoryImpl;
import org.eclipse.persistence.internal.jpa.EntityManagerSetupImpl;
import org.eclipse.persistence.internal.jpa.deployment.SEPersistenceUnitInfo;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.PersistenceContentHandler;
import org.eclipse.persistence.internal.jpa.deployment.xml.parser.XMLExceptionHandler;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.InputStream;
import java.io.StringReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;
import java.util.Map;

import static org.eclipse.persistence.testing.framework.junit.JUnitTestCaseHelper.getDatabaseProperties;

public class DynamicTestHelper {

    public static final String DYNAMIC_PERSISTENCE_NAME = "dynamic";
    static final String DYNAMIC_PERSISTENCE_XML =
        "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
        "<persistence version=\"1.0\" xmlns=\"http://java.sun.com/xml/ns/persistence\" " +
            "xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" " +
            "xsi:schemaLocation=\"http://java.sun.com/xml/ns/persistence " +
            "http://java.sun.com/xml/ns/persistence/persistence_1_0.xsd\">" +
            "<persistence-unit name=\"" + DYNAMIC_PERSISTENCE_NAME +
                "\" transaction-type=\"RESOURCE_LOCAL\">" +
                "<exclude-unlisted-classes>true</exclude-unlisted-classes>" +
            "</persistence-unit>" +
        "</persistence>";

    // custom 'in-memory' URL that doesn't "go" anywhere, doesn't resolve to anything
    static URL dynamicTestUrl = null;
    static {
        try {
            dynamicTestUrl = new URL(null, "inmemory:", new URLStreamHandler() {
                @Override
                protected URLConnection openConnection(URL url) {
                    return new URLConnection(url) {
                        @Override
                        public InputStream getInputStream() {
                            return null;
                        }
                        @Override
                        public void connect() {
                        }
                    };
                }
            });
        }
        catch (MalformedURLException e) {
            // ignore
        }
    }

    public static EntityManagerFactory createEMF(String emName) {
        PersistenceContentHandler myContentHandler = new PersistenceContentHandler();
        try {
            SAXParserFactory spf = SAXParserFactory.newInstance();
            spf.setNamespaceAware(true);
            SAXParser sp = spf.newSAXParser();
            XMLReader xmlReader = sp.getXMLReader();
            XMLExceptionHandler xmlErrorHandler = new XMLExceptionHandler();
            xmlReader.setErrorHandler(xmlErrorHandler);
            xmlReader.setContentHandler(myContentHandler);
            InputSource inputSource = new InputSource(new StringReader(DYNAMIC_PERSISTENCE_XML));
            xmlReader.parse(inputSource);
        }
        catch (Exception e) {
            return null;
        }
        // only ever one
        final SEPersistenceUnitInfo puInfo = myContentHandler.getPersistenceUnits().get(0);
        puInfo.setPersistenceUnitRootUrl(dynamicTestUrl);
        PersistenceProvider provider = new PersistenceProvider() {
            @Override
            public EntityManagerFactory createContainerEntityManagerFactory(
                PersistenceUnitInfo info, Map map) {
                return null;
            }
            @Override
            @SuppressWarnings({"rawtypes", "unchecked"})
            public EntityManagerFactory createEntityManagerFactory(String emName, Map map) {
                if (emName.equals(puInfo.getPersistenceUnitName())) {
                    EntityManagerSetupImpl entityManagerSetupImpl =
                        new EntityManagerSetupImpl(DYNAMIC_PERSISTENCE_NAME,DYNAMIC_PERSISTENCE_NAME);
                    map.put(PersistenceUnitProperties.WEAVING, "static");
                    puInfo.getProperties().put(
                        PersistenceUnitProperties.EXCLUDE_ECLIPSELINK_ORM_FILE, "true");
                    entityManagerSetupImpl.predeploy(puInfo, map);
                    return new EntityManagerFactoryImpl(entityManagerSetupImpl, map);
                }
                else {
                    return null;
                }
            }

            // Not used, added in JPA 3.2
            @Override
            public EntityManagerFactory createEntityManagerFactory(PersistenceConfiguration configuration) {
                return null;
            }

            @Override
            public ProviderUtil getProviderUtil() {
                return null;
            }

            @Override
            public void generateSchema(PersistenceUnitInfo info, Map map) {
            }

            @Override
            public boolean generateSchema(String persistenceUnitName, Map map) {
                // TODO Auto-generated method stub
                return false;
            }
         };
         return provider.createEntityManagerFactory(emName, getDatabaseProperties());
    }

}
