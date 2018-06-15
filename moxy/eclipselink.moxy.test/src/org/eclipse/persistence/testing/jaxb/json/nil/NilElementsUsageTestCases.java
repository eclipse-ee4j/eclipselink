/*
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Radek Felcman - April 2018 - 2.7.2
package org.eclipse.persistence.testing.jaxb.json.nil;

import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

import java.util.ArrayList;

public class NilElementsUsageTestCases extends JSONMarshalUnmarshalTestCases{
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/nil/nilElements.json";
    private final static String JSON_RESOURCE_WRITE = "org/eclipse/persistence/testing/jaxb/json/nil/nilElementsWrite.json";

    public NilElementsUsageTestCases(String name) throws Exception {
        super(name);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_RESOURCE_WRITE);
        setClasses(new Class[]{MaskFormat.class, MaskFormatEntry.class});
    }

    protected Object getControlObject() {
        MaskFormat maskFormat = new MaskFormat();

        MaskFormatEntry maskFormatEntry1 = new MaskFormatEntry();
        ArrayList<String> list1 = new ArrayList<>();
        list1.add("111");
        list1.add("222");
        list1.add("333");
        maskFormatEntry1.setArrayList(list1);
        maskFormatEntry1.setField1("ABC");

        MaskFormatEntry maskFormatEntry2 = new MaskFormatEntry();
        ArrayList<String> list2 = new ArrayList<>();
        list2.add("aaa");
        list2.add("bbb");
        list2.add("ccc");
        maskFormatEntry2.setArrayList(list2);
        maskFormatEntry2.setField1("xyz");

        ArrayList<MaskFormatEntry> maskFormatEntries = new ArrayList<>();
        maskFormatEntries.add(maskFormatEntry1);
        maskFormatEntries.add(maskFormatEntry2);
        maskFormat.setFormatEntries(maskFormatEntries);

        return maskFormat;
    }


}
