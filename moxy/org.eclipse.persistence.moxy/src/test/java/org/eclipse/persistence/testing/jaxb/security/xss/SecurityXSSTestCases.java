/*
 * Copyright (c) 2020, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Oracle - initial API and implementation
package org.eclipse.persistence.testing.jaxb.security.xss;

import junit.framework.TestCase;

import jakarta.xml.bind.*;
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
            fail("jakarta.xml.bind.UnmarshalException was not occured for " + fileName);
        } catch (UnmarshalException e) {
            assertNotNull(e);
        } catch (Exception e) {
            fail("No expected jakarta.xml.bind.UnmarshalException was thrown: " + e);
        }
        // the deserialized object variable must be null
        assertNull(testObject);
    }


}
