/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations.xmlpath.schematype;

import java.util.Date;

import javax.xml.datatype.DatatypeConfigurationException;
import javax.xml.datatype.DatatypeFactory;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class SchemaTypeTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/schematype.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/xmlpath/schematype.json";

    public SchemaTypeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
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

}