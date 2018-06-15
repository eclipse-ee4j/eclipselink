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
//      Marcel Valovy - initial API and implementation
//      Dmitry Kornilov - BeanValidationHelper refactoring
//      Miroslav Kos - BeanValidationHelper refactoring
package org.eclipse.persistence.jaxb;

import java.io.IOException;
import java.io.InputStream;
import java.security.AccessController;
import java.security.PrivilegedActionException;
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
import org.eclipse.persistence.internal.helper.XMLHelper;
import org.eclipse.persistence.internal.security.PrivilegedAccessHelper;
import org.eclipse.persistence.internal.security.PrivilegedGetContextClassLoader;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Detects external Bean Validation configuration.
 * <p>
 * Strategy:<br>
 * 1. Parse validation.xml, looking for a constraints-file reference.<br>
 * 2. For each reference, if file is found, parses the constraints file and puts all classes declared under
 * {@literal <bean class="clazz">} into
 * {@link org.eclipse.persistence.jaxb.BeanValidationHelper#constraintsOnClasses}
 * with value {@link Boolean#TRUE}.
 * <p>
 * This class contains resources-burdening instance fields (e.g. SAXParser) and as such was designed to be instantiated
 * once (make the instance BOUNDED) and have {@link #call()} method called on that instance once.
 * <p>
 * Not suitable for singleton (memory burden). The method #parse() will be invoked only once per class load of this
 * class. After that the instance and all its fields should be made collectible by GC.
 *
 * @author Marcel Valovy
 * @author Dmitry Kornilov
 * @author Miroslav Kos
 * @since 2.6
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
        parseValidationXML(VALIDATION_XML, validationHandler);

        if (!constraintsFiles.isEmpty()) {
            parseConstraintFiles();
        }
        return constraintsOnClasses;
    }

    /**
     * Checks if validation.xml exists.
     */
    public static boolean isValidationXmlPresent() {
        try {
            return getThreadContextClassLoader().getResource(VALIDATION_XML) != null;
        } catch (PrivilegedActionException ignored) {
            LOGGER.log(Level.WARNING, "Loading of " + VALIDATION_XML + " file failed. ", ignored);
            return false;
        }
    }

    private void parseConstraintFiles() {
        final class ConstrainedClassesDetector extends DefaultHandler {

            private boolean defaultPackageElement = false;
            private String defaultPackage = "";

            @Override
            public void startElement(String uri, String localName, String qName,
                                     Attributes attributes) throws SAXException {

                if (DEFAULT_PACKAGE_QNAME.equalsIgnoreCase(qName)) {
                    defaultPackageElement = true;
                } else if (BEAN_QNAME.equalsIgnoreCase(qName)) {
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
        }

        // Parse constraints file referenced in validation.xml. Add all classes declared under <bean class="clazz"> to
        // org.eclipse.persistence.jaxb.BeanValidationHelper#constraintsOnClasses with value Boolean#TRUE.
        for (String file : constraintsFiles) {
            parseValidationXML(file, new ConstrainedClassesDetector());
        }
    }

    /**
     * Lazy getter for SAX parser.
     */
    private SAXParser getSaxParser() {
        if (saxParser == null) {
            try {
                SAXParserFactory factory = XMLHelper.createParserFactory(false);
                saxParser = factory.newSAXParser();
            } catch (ParserConfigurationException | SAXException e) {
                String msg = "ValidationXMLReader initialization failed. Exception: " + e.getMessage();
                LOGGER.severe(msg);
                throw new BeanValidationException(msg, e);
            }
        }
        return saxParser;
    }

    private void parseValidationXML(String constraintsFilePath, DefaultHandler handler) {
        try (InputStream validationXml = getThreadContextClassLoader().getResourceAsStream(constraintsFilePath)) {
            if (validationXml != null) {
                getSaxParser().parse(validationXml, handler);
            }
        } catch (PrivilegedActionException | SAXException | IOException ignored) {
            LOGGER.log(Level.WARNING, "Parsing of validation.xml failed.", ignored);
        }
    }

    private static ClassLoader getThreadContextClassLoader() throws PrivilegedActionException {
        if (PrivilegedAccessHelper.shouldUsePrivilegedAccess()) {
            return AccessController.doPrivileged(new PrivilegedGetContextClassLoader(Thread.currentThread()));
        } else {
            return Thread.currentThread().getContextClassLoader();
        }
    }

    private final DefaultHandler validationHandler = new DefaultHandler() {

        private boolean constraintsFileElement = false;

        @Override
        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {

            if (CONSTRAINT_MAPPING_QNAME.equalsIgnoreCase(qName)) {
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
