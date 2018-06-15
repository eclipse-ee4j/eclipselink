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
//     Blaise Doughan - 2.3 - initial implementation
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.schematype;

import java.io.StringReader;
import java.util.Date;
import java.util.TimeZone;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class SchemaTypeTestCases extends JAXBWithJSONTestCases {

    public SchemaTypeTestCases(String name) throws Exception {
        super(name);
        setTypes(new Class[] {Root.class});
    }

    @Override
    protected Object getControlObject() {
        try {
            DatatypeFactory df = DatatypeFactory.newInstance();
            Date date1 = df.newXMLGregorianCalendar("1975-02-21").toGregorianCalendar().getTime();
            Date date2 = df.newXMLGregorianCalendar("08:30:00").toGregorianCalendar().getTime();
            Date date3 = df.newXMLGregorianCalendar("17:00:00").toGregorianCalendar().getTime();

            Root root = new Root();
            root.setSingleDate(date1);
            root.getDateList().add(date2);
            root.getDateList().add(date3);
            return root;
        } catch (DatatypeConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    protected String getControlJSONDocumentContent() {
        return "{\"root\":{\n" +
                "    \"date\":{\n" +
                "        \"list\":[\"08:30:00"+TIMEZONE_OFFSET+"\",\"17:00:00"+TIMEZONE_OFFSET+"\"]\n" +
                "\t},\n" +
                "     \"single\":{\n" +
                "        \"date\":\"1975-02-21\"\n" +
                "    }\n" +
                "}}";
    }

    public boolean isUnmarshalTest() {
         return false;
    }

    @Override
    protected Document getControlDocument() {
        String contents = "<root>" +
                "<date>" +
                "<list>08:30:00"+TIMEZONE_OFFSET+"</list>" +
                "<list>17:00:00"+TIMEZONE_OFFSET+"</list>" +
                "</date>" +
                "<single>" +
                "<date>1975-02-21</date>" +
                "</single>" +
                "</root>";

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

}
