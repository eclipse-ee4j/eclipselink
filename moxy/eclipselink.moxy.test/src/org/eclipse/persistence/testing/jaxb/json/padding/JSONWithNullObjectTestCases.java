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

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.JSONWithPadding;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;
import org.eclipse.persistence.testing.jaxb.json.numbers.NumberHolder;

public class JSONWithNullObjectTestCases extends JAXBWithJSONTestCases{

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/padding.json";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/padding.xml";

    public JSONWithNullObjectTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Simple.class});
        setControlJSON(JSON_RESOURCE);
        setControlDocument(XML_RESOURCE);
        expectsMarshalException = true;
    }

    @Override
    protected Object getControlObject() {
        //Sample sample = new Sample();
        //sample.id = "1111";
        //sample.name = "theName";

        JSONWithPadding test = new JSONWithPadding(null, "blah");
        return test;
    }

    public boolean isUnmarshalTest (){
        return false;
    }

    public void testJSONMarshalToBuilderResult() throws Exception{
    }

    public void testJSONMarshalToGeneratorResult() throws Exception{
    }

    @Override
    public void assertMarshalException(Exception exception) throws Exception {
        Exception nestedException = (Exception) exception.getCause();
        assertTrue(nestedException instanceof XMLMarshalException);
        assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
    }

}
