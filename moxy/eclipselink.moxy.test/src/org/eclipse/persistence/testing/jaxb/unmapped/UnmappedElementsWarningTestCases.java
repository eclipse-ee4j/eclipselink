/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     dminsky - 2.6.0
package org.eclipse.persistence.testing.jaxb.unmapped;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.ValidationEvent;
import javax.xml.bind.ValidationEventHandler;
import javax.xml.transform.stream.StreamSource;

import junit.textui.TestRunner;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.oxm.MediaType;
import org.eclipse.persistence.oxm.XMLUnmarshaller;
import org.eclipse.persistence.testing.oxm.OXTestCase;

/**
 * Test case for EclipseLink Bug 452584
 */
public class UnmappedElementsWarningTestCases extends OXTestCase{

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/unmapped/BulkUnmapped.xml";

    private JAXBContext jaxbContext;

    public UnmappedElementsWarningTestCases(String name) {
       super(name);
    }

    public static void main(String[] args) {
        TestRunner.run(UnmappedElementsWarningTestCases.class);
    }

    public void setUp() throws Exception {
        super.setUp();
        Class[] classes = new Class[1];
        classes[0] = BulkUnmapped.class;
        jaxbContext = JAXBContextFactory.createContext(classes, null, Thread.currentThread().getContextClassLoader());
    }

    public void testUnmarshalMultipleUnmappedElementsNoValidation() throws JAXBException, IOException {
        JAXBUnmarshaller unm = (JAXBUnmarshaller) jaxbContext.createUnmarshaller();
        InputStream inputStream = null;
        try {
            inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
            BulkUnmapped unmarshalledObject = (BulkUnmapped) unm.unmarshal(inputStream);
            assertTrue("Valid document was not unmarshalled correctly", unmarshalledObject != null);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public void testUnmarshalMultipleUnmappedElementsWithValidation() throws JAXBException, IOException {
        JAXBUnmarshaller unm = (JAXBUnmarshaller) jaxbContext.createUnmarshaller();
        InputStream inputStream = null;
        try {
            inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);

            // Add a validation handler for verification
            CustomValidationEventHandler handler = new CustomValidationEventHandler();
            unm.setEventHandler(handler);
            BulkUnmapped unmarshalledObject = (BulkUnmapped) unm.unmarshal(inputStream);

            assertTrue("Valid document was not unmarshalled correctly", unmarshalledObject != null);
            assertTrue("Validation errors should be non-zero", handler.getValidationErrors() > 0);
        } finally {
            if (inputStream != null) {
                inputStream.close();
            }
        }
    }

    public void testUnmarshalMultipleUnmappedElementsValidationSwitching() throws JAXBException, IOException {
       JAXBUnmarshaller unm = (JAXBUnmarshaller) jaxbContext.createUnmarshaller();
       InputStream inputStream = null;

       // 1 Unmarshal without validation
       // default is not to warn on unmapped element with all defaults
       try {
           inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
           XMLUnmarshaller xmlUnmarshaller = (XMLUnmarshaller) unm.getXMLUnmarshaller();
           boolean shouldWarnOnUnmapped = xmlUnmarshaller.shouldWarnOnUnmappedElement();
           BulkUnmapped unmarshalledObject = (BulkUnmapped) unm.unmarshal(inputStream);

           assertFalse("Should not warn on unmapped element with default validation", shouldWarnOnUnmapped);
           assertTrue("Valid document was not unmarshalled correctly", unmarshalledObject != null);
       } finally {
           if (inputStream != null) {
               inputStream.close();
           }
       }

       // 2 Unmarshal with validation (same unmarshaller)
       try {
           inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
           XMLUnmarshaller xmlUnmarshaller = (XMLUnmarshaller) unm.getXMLUnmarshaller();
           CustomValidationEventHandler handler = new CustomValidationEventHandler();
           unm.setEventHandler(handler);
           boolean shouldWarnOnUnmapped = xmlUnmarshaller.shouldWarnOnUnmappedElement();
           BulkUnmapped unmarshalledObject = (BulkUnmapped) unm.unmarshal(inputStream);

           assertTrue("Valid document was not unmarshalled correctly", unmarshalledObject != null);
           assertTrue("Should warn on unmapped element with non-default validation", shouldWarnOnUnmapped);
           assertTrue("Validation errors should be non-zero", handler.getValidationErrors() > 0);
       } finally {
           if (inputStream != null) {
               inputStream.close();
           }
       }

       // 3 Disable validation and unmarshal (same unmarshaller)
       try {
           inputStream = ClassLoader.getSystemResourceAsStream(XML_RESOURCE);
           unm.setEventHandler(null);
           XMLUnmarshaller xmlUnmarshaller = (XMLUnmarshaller) unm.getXMLUnmarshaller();
           boolean shouldWarnOnUnmapped = xmlUnmarshaller.shouldWarnOnUnmappedElement();
           BulkUnmapped unmarshalledObject = (BulkUnmapped) unm.unmarshal(inputStream);

           assertTrue("Valid document was not unmarshalled correctly", unmarshalledObject != null);
           assertFalse("Should not warn on unmapped element with default validation", shouldWarnOnUnmapped);
       } finally {
           if (inputStream != null) {
               inputStream.close();
           }
       }
    }

    private class CustomValidationEventHandler implements ValidationEventHandler {
        private int validationErrors = 0;

        @Override
        public boolean handleEvent(ValidationEvent event) {
            validationErrors++;
            return true;
        }

        public int getValidationErrors() {
            return this.validationErrors;
        }
    }

}
