/*
 * Copyright (c) 2012, 2025 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - November 2012
package org.eclipse.persistence.testing.jaxb.json.padding;

import org.eclipse.persistence.oxm.exceptions.XMLMarshalException;
import org.eclipse.persistence.oxm.JSONWithPadding;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class JSONWithNullObjectTestCases extends JAXBWithJSONTestCases{

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/padding.json";
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/padding/padding.xml";

    public JSONWithNullObjectTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class<?>[]{Simple.class});
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

    @Override
    public boolean isUnmarshalTest (){
        return false;
    }

    @Override
    public void testJSONMarshalToBuilderResult() throws Exception{
    }

    @Override
    public void testJSONMarshalToGeneratorResult() throws Exception{
    }

    @Override
    public void assertMarshalException(Exception exception) throws Exception {
        Exception nestedException = (Exception) exception.getCause();
        assertTrue(nestedException instanceof XMLMarshalException);
        assertEquals(25011, ((XMLMarshalException)nestedException).getErrorCode());
    }

}
