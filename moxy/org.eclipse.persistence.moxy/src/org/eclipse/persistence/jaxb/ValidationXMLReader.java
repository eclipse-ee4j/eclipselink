/**
 * ****************************************************************************
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
 * ****************************************************************************
 */
package org.eclipse.persistence.jaxb;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.logging.Level;
import java.util.logging.Logger;

import static org.eclipse.persistence.jaxb.BeanValidationHelper.BEAN_VALIDATION_HELPER;

/**
 * Detects external Bean Validation configuration.
 *
 * Strategy:
 * 1. Parse validation.xml, looking for a constraints-file reference.
 * 2. For each referenceIf  file is found,
 * parses the constraints file and puts all classes declared under <bean class="clazz"> into
 * {@link org.eclipse.persistence.jaxb.BeanValidationHelper#constraintsOnClasses} with value
 * {@link Boolean#TRUE}.
 *
 * This class contains resources-burdening instance fields (e.g. SAXParser) and as such was designed to be instantiated
 * once (make the instance BOUNDED) and have {@link #call()} method called on that instance once.
 *
 * Not suitable for singleton (memory burden). The method #parse() will be invoked only once per class load of this
 * class. After that the instance and all its fields should be made collectible by GC.
 *
 * @author Marcel Valovy - marcelv3612@gmail.com
 * @since 2.6
 */
class ValidationXMLReader implements Callable<Void> {

    public static final String DEFAULT_PACKAGE_QNAME = "default-package";
    public static final String BEAN_QNAME = "bean";
    public static final String CONSTRAINT_MAPPING_QNAME = "constraint-mapping";
    public static final String CLASS_QNAME = "class";
    public static final String PACKAGE_SEPARATOR = ".";

    private static final CountDownLatch latch = new CountDownLatch(1);
    private static final String VALIDATION_XML = "META-INF/validation.xml";
    private static final Logger LOGGER = Logger.getLogger(ValidationXMLReader.class.getName());

    private static volatile boolean firstTime = true;
    private static boolean didSomething = false;
    private static boolean jdkExecutor = false;
    private static boolean possibleResourceVisibilityFail = false;

    private final List<String> constraintsFiles = new ArrayList<>(2);

    private SAXParser saxParser;

    @Override
    public Void call() throws Exception {
        try {
            didSomething = true;
            try {
                parseValidationXML();
            } catch (URISyntaxException | SAXException | IOException e) {
                String msg = "Parsing of validation.xml failed. Exception: " + e.getMessage();
                LOGGER.warning(msg);
            }

            if (!constraintsFiles.isEmpty()) {
                parseConstraintFiles();
            }
        } finally {
            latch.countDown();
        }
        return null;
    }

    static void runAsynchronously() {
        // in case someone is evil and calls this method even when should not
        // "Double-checked locking" idiom
        if (firstTime) {
            synchronized (ValidationXMLReader.class) {
                if (firstTime) {
                    firstTime = false;
                } else {
                    return;
                }
            }
            runAsyncInternal();
        }
    }

    /**
     * Call if you think that you are waiting too long for the {@link #runAsynchronously()} to finish.
     * Someone might have interrupted/killed that thread... or didn't even allow it to be created,
     * aka {@link #didSomething} is false.
     *
     * Note: To run parsing synchronously is force of last resort. If this fails, you should proceed with validation and
     * not account for classes specified in validation.xml - there is high chance that if we cannot read validation.xml
     * successfully, neither will the BV implementation library.
     *
     * @return true if parsing finished successfully, false if exception was thrown
     */
    static boolean runSynchronouslyForced() {
        // someone is evil and calls this method (because parsing is taking too long for some reason)
        // there is 99% chance that this will never be called and everyone will be happy
        firstTime = false;
        try {
            getInstance().call();
        } catch (Throwable t) {
            return false;
        }
        return true;
    }

    /**
     * @return latch
     */
    static CountDownLatch getLatch() {
        return latch;
    }

    private static void runAsyncInternal() {
        Callable<Void> callable = getInstance();

        Crate.Tuple<ExecutorService, Boolean> crate = Concurrent.getManagedSingleThreadedExecutorService();

        jdkExecutor = !crate.getPayload2();
        ExecutorService executor = crate.getPayload1();
        executor.submit(callable);
        executor.shutdown();
    }

    /**
     * Avoid singleton.
     * Use retrieved object as BOUNDED, i.e. with local scope, so that gc can do its job. You do NOT need to store
     * this object anywhere. Its only purpose is to scan validation.xml once and add it to cache on enum {@link org
     * .eclipse.persistence.jaxb.BeanValidationHelper}.
     */
    private static ValidationXMLReader getInstance() {
        return new ValidationXMLReader();
    }

    private ValidationXMLReader() {
        try {
            SAXParserFactory factory = SAXParserFactory.newInstance();
            saxParser = factory.newSAXParser();
        } catch (ParserConfigurationException | SAXException e) {
            if (LOGGER.isLoggable(Level.INFO)) {
                String msg = "ValidationXMLReader initialization failed. Exception: " + e.getMessage();
                LOGGER.info(msg);
            }
        }
    }

    /**
     * @return true if someone is just not letting the async thread do anything or if we possibly failed due to using
     * jdk executor and not having access to resources managed by container.
     */
    static boolean asyncAttemptFailed() {
        return !didSomething || (possibleResourceVisibilityFail &&
                Thread.currentThread().getContextClassLoader().getResource(VALIDATION_XML) != null);
    }

    private void parseConstraintFiles() {
        final class ConstrainedClassesDetector extends DefaultHandler {

            private boolean defaultPackageElement = false;
            private String defaultPackage = "";

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
                        BEAN_VALIDATION_HELPER.putConstrainedClass(clazz);
                    } catch (ClassNotFoundException e) {
                        String errMsg = "Loading found class failed. Exception: " + e.getMessage();
                        LOGGER.warning(errMsg);
                    }
                }
            }

            public void characters(char ch[], int start, int length) throws SAXException {

                if (defaultPackageElement) {
                    defaultPackage = new String(ch, start, length);
                    defaultPackageElement = false;
                }
            }
        };

        for (String file : constraintsFiles) {
            parseConstraintFile(file, new ConstrainedClassesDetector());
        }
    }

    private void parseValidationXML() throws SAXException, IOException, URISyntaxException {
        URL validationXml = Thread.currentThread().getContextClassLoader().getResource(VALIDATION_XML);
        if (validationXml != null) {
            saxParser.parse(new File(validationXml.toURI()), validationHandler);
        } else if (jdkExecutor) {
            possibleResourceVisibilityFail = true;
        }
    }

    /**
     * Parse constraints file (referenced in validation.xml). Add all classes declared under <bean
     * class="clazz"> to {@link org.eclipse.persistence.jaxb.BeanValidationHelper#constraintsOnClasses} with value
     * {@link Boolean#TRUE}.
     */
    private void parseConstraintFile(String constraintsFile, DefaultHandler referencedFileHandler) {
        URL constraintsXml = Thread.currentThread().getContextClassLoader().getResource(constraintsFile);
        try {
            //noinspection ConstantConditions
            saxParser.parse(new File(constraintsXml.toURI()), referencedFileHandler);
        } catch (SAXException | IOException | URISyntaxException | NullPointerException e) {
            String msg = "Loading of custom constraints file: " + constraintsFile + "failed. Exception: " + e
                    .getMessage();
            LOGGER.warning(msg);
        }
    }

    private final DefaultHandler validationHandler = new DefaultHandler() {

        private boolean constraintsFileElement = false;

        public void startElement(String uri, String localName, String qName,
                                 Attributes attributes) throws SAXException {

            if (qName.equalsIgnoreCase(CONSTRAINT_MAPPING_QNAME)) {
                constraintsFileElement = true;
            }
        }

        public void characters(char ch[], int start, int length) throws SAXException {

            if (constraintsFileElement) {
                constraintsFiles.add(new String(ch, start, length));
                constraintsFileElement = false;
            }
        }
    };

}
