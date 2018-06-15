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
//     Matt MacIvor - 2.4.1 - initial implementation
package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class AdapterEnumTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/enum.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/enum.json";

    public AdapterEnumTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = {EnumRoot.class};
        setClasses(classes);
    }

    protected Object getControlObject() {
        EnumRoot emp = new EnumRoot();
        emp.multi = new ArrayList<Byte>();
        emp.multi.add((byte)1);
        emp.multi.add((byte)3);
        emp.multi.add((byte)1);

        emp.single = (byte)2;

        emp.cardSuit = CardSuit.DIAMOND;
        emp.cardSuits = Arrays.asList(CardSuit.CLUB, CardSuit.HEART, CardSuit.SPADE);
        return emp;
    }
}
