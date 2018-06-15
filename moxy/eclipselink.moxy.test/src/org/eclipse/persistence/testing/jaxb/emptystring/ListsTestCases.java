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
//     Denise Smith - 2.3
package org.eclipse.persistence.testing.jaxb.emptystring;

import java.io.InputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ListsTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/emptystring/listsTests.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/emptystring/listsTests.json";
    private final static String XML_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/emptystring/listsTestsWrite.xml";

    public ListsTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlDocument(XML_RESOURCE_WRITE);
        Class[] classes = new Class[1];
        classes[0] = ListsTestObject.class;
        setClasses(classes);
    }

    protected Object getControlObject() {
        ListsTestObject testObject = new ListsTestObject();
        ArrayList<String> strings = new ArrayList<String>();
        strings.add("");
        testObject.setStrings(strings);

        ArrayList<BigDecimal> bigDecimals = new ArrayList<BigDecimal>();
        bigDecimals.add(null);
        testObject.setBigDecimals(bigDecimals);

        ArrayList<Integer> integers = new ArrayList<Integer>();
        integers.add(0);
        testObject.setIntegers(integers);

        return testObject;
    }

    public void testSchemaGen() throws Exception {
        List<InputStream> controlSchemas = new ArrayList<InputStream>();
        InputStream is = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/emptystring/listsTests.xsd");
        controlSchemas.add(is);
        super.testSchemaGen(controlSchemas);
    }


}
