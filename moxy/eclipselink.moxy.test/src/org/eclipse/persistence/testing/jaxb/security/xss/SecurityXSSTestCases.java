/*
 * Copyright (c) 2020 Oracle and/or its affiliates. All rights reserved.
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
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.jaxb.security.xss;

import junit.framework.TestCase;

import javax.xml.bind.*;
import java.io.File;
import java.util.HashMap;

public class SecurityXSSTestCases extends TestCase {

    private static final String XML_DOCUMENT_NESTED_ENTITIES = "org/eclipse/persistence/testing/jaxb/security/xss/xssNestedEntities.xml";
    private static final String XML_DOCUMENT_EXTERNAL_ENTITIES = "org/eclipse/persistence/testing/jaxb/security/xss/xssExternalEntity.xml";
    private static final String XML_DOCUMENT_EXTERNAL_PARAMETER_ENTITIES = "org/eclipse/persistence/testing/jaxb/security/xss/xssExternalParameterEntity.xml";
    private static final Class<?>[] DOMAIN_CLASSES = new Class<?>[]{MyRoot.class};

    private JAXBContext jaxbContext;
    private Unmarshaller unmarshaller;

    public SecurityXSSTestCases(String name) {
        super(name);
    }

    public void testSecurityXSSExternalEntities() {
        unmarshallDocument(XML_DOCUMENT_EXTERNAL_ENTITIES);
    }

    public void testSecurityXSSExternalParameterEntities() {
        unmarshallDocument(XML_DOCUMENT_EXTERNAL_PARAMETER_ENTITIES);
    }

    public void testSecurityXSSNestedEntities() {
        unmarshallDocument(XML_DOCUMENT_NESTED_ENTITIES);
    }

    public void setUp() throws Exception {
        final HashMap<String, Object> contextProperties = new HashMap<>();
        jaxbContext = JAXBContext.newInstance(DOMAIN_CLASSES, contextProperties);
        unmarshaller = jaxbContext.createUnmarshaller();
    }

    private void unmarshallDocument(String fileName) {
        Object testObject = null;
        File file = new File(ClassLoader.getSystemResource(fileName).getFile());
        try {
            testObject = unmarshaller.unmarshal(file);
            fail("javax.xml.bind.UnmarshalException was not occured for " + fileName);
        } catch (UnmarshalException e) {
            assertNotNull(e);
        } catch (Exception e) {
            fail("No expected javax.xml.bind.UnmarshalException was thrown: " + e);
        }
        // the deserialized object variable must be null
        assertNull(testObject);
    }


}
