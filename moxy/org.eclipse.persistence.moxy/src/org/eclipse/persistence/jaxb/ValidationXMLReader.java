/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 * <p/>
 * Contributors:
 *      Marcel Valovy - initial API and implementation
 *      Dmitry Kornilov - BeanValidationHelper refactoring
 ******************************************************************************/
package org.eclipse.persistence.jaxb;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.eclipse.persistence.exceptions.BeanValidationException;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetContextClassLoader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Parses validation.xml, scanning for constraints file reference. If found,
 * it will parse the constraints file and put all classes declared under &lt;bean class="clazz"&gt; into
 * {@link org.eclipse.persistence.jaxb.BeanValidationHelper#constraintsOnClasses} with value
 * {@link Boolean#TRUE}.
 */
public class ValidationXMLReader implements Callable<Map<Class<?>, Boolean>> {

    public static final String DEFAULT_PACKAGE_QNAME = "default-package";
    public static final String BEAN_QNAME = "bean";
    public static final String CONSTRAINT_MAPPING_QNAME = "constraint-mapping";
    public static final String CLASS_QNAME = "class";
    public static final String PACKAGE_SEPARATOR = ".";

    private static final String VALIDATION_XML = "META-INF/validation.xml";
    private static final Logger LOGGER = Logger.getLogger(ValidationXMLReader.class.getName());

    private final List<String> constraintsFiles = new ArrayList<>(2);

    private Map<Class<?>, Boolean> constraintsOnClasses = new HashMap<>();

    // Created lazily
    private SAXParser saxParser;

    /**
     * Parses validation.xml.
     * @return returns a map with classes found in validation.xml as keys and true as a value. Never returns null.
     */
    @Override
    public Map<Class<?>, Boolean> call() throws Exception {
        try {
            parseValidationXML();
        } catch (URISyntaxException | SAXException | IOException e) {
            String msg = "Parsing of validation.xml failed. Exception: " + e.getMessage();
            LOGGER.warning(msg);
        }

        if (!constraintsFiles.isEmpty()) {
            parseConstraintFiles();
        }
        return constraintsOnClasses;
    }

    /**
     * Checks if validation.xml exists.
     */
    public static boolean isValidationXmlPresent() {
        final URL url = Thread.currentThread().getContextClassLoader().getResource(VALIDATION_XML);
        return url != null;
    }

    private void parseConstraintFiles() {
        final DefaultHandler referencedFileHandler = new DefaultHandler() {

            private boolean defaultPackageElement = false;
            private String defaultPackage = "";

            @Override
            public void startElement(String uri, String localName, String qName,
                                     Attributes attributes) throws SAXException {

                if (qName.equalsIgnoreCase(DEFAULT_PACKAGE_QNAME)) {
                    defaultPackageElement = true;
                } else if (qName.equalsIgnoreCase(BEAN_QNAME)) {
                    String className = defaultPackage + PACKAGE_SEPARATOR + attributes.getValue(CLASS_QNAME);
                    if (LOGGER.isLoggable(Level.INFO)) {
                        String msg = "Detected external constraints on class " + className;
                        LOGGER.info(msg);
                    }
                    try {
                        Class<?> clazz = ReflectionUtils.forName(className);
                        constraintsOnClasses.put(clazz, Boolean.TRUE);
                    } catch (ClassNotFoundException e) {
                        String errMsg = "Loading found class failed. Exception: " + e.getMessage();
                        LOGGER.warning(errMsg);
                    }
                }
            }

            @Override
            public void characters(char ch[], int start, int length) throws SAXException {
                if (defaultPackageElement) {
                    defaultPackage = new String(ch, start, length);
                    defaultPackageElement = false;
                }
            }
        };

        for (String file : constraintsFiles) {
            parseConstraintFile(file, referencedFileHandler);
        }
    }

    /**
     * Lazy getter for SAX parser.
     */
    private SAXParser getSaxParser() {
        if (saxParser == null) {
            try {
                SAXParserFactory factory = SAXParserFactory.newInstance();
                saxParser = factory.newSAXParser();
            } catch (ParserConfigurationException | SAXException e) {
                String msg = "ValidationXMLReader initialization failed. Exception: " + e.getMessage();
                LOGGER.severe(msg);
                throw new BeanValidationException(msg, e);
            }
        }
        return saxParser;
    }

    private void parseValidationXML() throws SAXException, IOException, URISyntaxException {
        URL validationXml = Thread.currentThread().getContextClassLoader().getResource(VALIDATION_XML);
        if (validationXml != null) {
            getSaxParser().parse(new File(validationXml.toURI()), validationHandler);
        }
    }

    /**
     * Parse constraints file referenced in validation.xml. Add all classes declared under <bean
     * class="clazz"> to {@link org.eclipse.persistence.jaxb.BeanValidationHelper#constraintsOnClasses} with value
     * {@link Boolean#TRUE}.
     */
    private void parseConstraintFile(String constraintsFile, DefaultHandler referencedFileHandler) {
        URL constraintsXml = Thread.currentThread().getContextClassLoader().getResource(constraintsFile);
        try {
            //noinspection ConstantConditions
            getSaxParser().parse(new File(constraintsXml.toURI()), referencedFileHandler);
        } catch (SAXException | IOException | URISyntaxException | NullPointerException e) {
            String msg = "Loading of custom constraints file: " + constraintsFile + "failed. Exception: " + e
                    .getMessage();
            LOGGER.warning(msg);
        }
    }

    private final DefaultHandler validationHandler = new DefaultHandler() {

        private boolean constraintsFileElement = false;

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {

            if (qName.equalsIgnoreCase(CONSTRAINT_MAPPING_QNAME)) {
                constraintsFileElement = true;
            }
        }

        @Override
        public void characters(char ch[], int start, int length) throws SAXException {

            if (constraintsFileElement) {
                constraintsFiles.add(new String(ch, start, length));
                constraintsFileElement = false;
            }
        }
    };

}
