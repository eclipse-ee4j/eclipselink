/*
 * Copyright (c) 2014, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.eclipse.persistence.testing.jaxb.xmladapter.enumeration;

import java.util.ArrayList;
import java.util.Arrays;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

/**
 * This test represents scenario when more complicated generic XmlAdapter is used.
 * <p>
 * Example: GenericSuitsAdapterWithT&lt;T extends Enum> extends XmlAdapter&lt;String, T>
 * </p>
 */
public class AdapterEnumMoreGenericTestCases extends JAXBWithJSONTestCases{
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/enum.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmladapter/enum.json";

    public AdapterEnumMoreGenericTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = {MoreGenericEnumRoot.class};
        setClasses(classes);
    }

    protected Object getControlObject() {
        MoreGenericEnumRoot emp = new MoreGenericEnumRoot();
        emp.multi = new ArrayList<Byte>();
        emp.multi.add((byte)1);
        emp.multi.add((byte)3);
        emp.multi.add((byte)1);

        emp.single = (byte)2;

        emp.cardSuit = MoreGenericCardSuit.DIAMOND;
        emp.cardSuits = Arrays.asList(MoreGenericCardSuit.CLUB, MoreGenericCardSuit.HEART, MoreGenericCardSuit.SPADE);
        return emp;
    }
}
