/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
// mmacivor - April 25/2008 - 1.0M8 - Initial implementation
package org.eclipse.persistence.testing.jaxb.simpledocument;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

import jakarta.xml.bind.JAXBElement;

/**
 * Tests mapping a simple document containing a single base64 element to a Byte Array
 * @author mmacivor
 *
 */
public class SimpleDocumentByteArrayTestCases extends JAXBWithJSONTestCases {
        private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/simpledocument/bytearrayroot.xml";
        private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/simpledocument/bytearrayroot.json";

        public SimpleDocumentByteArrayTestCases(String name) throws Exception {
            super(name);
            setControlDocument(XML_RESOURCE);
            setControlJSON(JSON_RESOURCE);
            Class[] classes = new Class[1];
            classes[0] = ByteArrayObjectFactory.class;
            setClasses(classes);
        }

        @Override
        protected Object getControlObject() {
            JAXBElement value = new ByteArrayObjectFactory().createBase64Root();
            value.setValue(new Byte[]{(byte) 1, (byte) 2, (byte) 3, (byte) 4, (byte) 5, (byte) 6, (byte) 7});
            return value;
        }

        protected void compareObjectArrays(Object controlValue, Object testValue){
             Byte[] controlBytes = (Byte[])controlValue;
             Byte[] testBytes = (Byte[])testValue;
             assertEquals(controlBytes.length, testBytes.length);
             for(int i = 0; i < controlBytes.length; i++) {
                 assertEquals(controlBytes[i].byteValue(), testBytes[i].byteValue());
             }
        }
}
