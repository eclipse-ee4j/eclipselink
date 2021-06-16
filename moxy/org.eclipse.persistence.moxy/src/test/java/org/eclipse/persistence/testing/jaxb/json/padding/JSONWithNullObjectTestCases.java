/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Denise Smith - November 2012
package org.eclipse.persistence.testing.jaxb.json.padding;

import jakarta.xml.bind.JAXBException;

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
