/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith - November 2012
package org.eclipse.persistence.testing.jaxb.json.padding;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.oxm.JSONWithPadding;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;
import org.eclipse.persistence.testing.jaxb.json.JSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.numbers.NumberHolder;

public class JSONWithPaddingSimpleListTestCases extends JSONTestCases{

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/paddingList.json";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/padding.xml";
    //private final static String JSON_FORMATTED_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/paddingListFormatted.json";

    public JSONWithPaddingSimpleListTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Simple.class});
        setControlJSON(JSON_RESOURCE);
        //setWriteControlFormattedJSON(JSON_FORMATTED_RESOURCE);
    }

    @Override
    protected Object getControlObject() {
        List<Simple> simples = new ArrayList<Simple>();
        Simple simple = new Simple();
        simple.id = "1111";
        simple.name = "theName";

        Simple simple2 = new Simple();
        simple2.id = "2222";
        simple2.name = "theName2";

        simples.add(simple);
        simples.add(simple2);
        JSONWithPadding test = new JSONWithPadding(simples, "blah");
        return test;
    }

    public boolean isUnmarshalTest (){
        return false;
    }

    public void testJSONMarshalToBuilderResult() throws Exception{
    }

    public void testJSONMarshalToGeneratorResult() throws Exception{
    }

//    public boolean shouldRemoveWhitespaceFromControlDocJSON(){
    //    return false;
    //}
}
