/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
// mmacivor - April 25/2008 - 1.0M8 - Initial implementation
package org.eclipse.persistence.testing.jaxb.simpledocument;

import java.io.StringReader;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.bind.JAXBElement;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

/**
 * Tests mapping a simple document containing a single date element to a Date object
 * @author mmacivor
 *
 */
public class SimpleDocumentDateTestCases extends JAXBWithJSONTestCases {

        public SimpleDocumentDateTestCases(String name) throws Exception {
            super(name);
            Class[] classes = new Class[1];
            classes[0] = DateObjectFactory.class;
            setClasses(classes);
        }

        @Override
        protected String getControlJSONDocumentContent() {
            return "{\"dateroot\":\"2013-02-20T00:00:00"+TIMEZONE_OFFSET+"\"}";
        }

        public boolean isUnmarshalTest() {
             return false;
        }

        @Override
        protected Document getControlDocument() {
            String contents = "<ns0:dateroot xmlns:ns0=\"myns\">2013-02-20T00:00:00"+TIMEZONE_OFFSET+"</ns0:dateroot>";

            StringReader reader = new StringReader(contents);
            InputSource is = new InputSource(reader);
            Document doc = null;
            try {
                doc = parser.parse(is);
            } catch (Exception e) {
                fail("An error occurred setting up the control document");
            }
            return doc;
        }

        protected Object getControlObject() {
        JAXBElement value = new DateObjectFactory().createDateRoot();

        Calendar cal = Calendar.getInstance();
        cal.clear();
        cal.set(Calendar.YEAR, 2013);
        cal.set(Calendar.MONTH, Calendar.FEBRUARY);
        cal.set(Calendar.DAY_OF_MONTH, 20);

        Date date = cal.getTime();
        value.setValue(date);

        return value;
        }
}
