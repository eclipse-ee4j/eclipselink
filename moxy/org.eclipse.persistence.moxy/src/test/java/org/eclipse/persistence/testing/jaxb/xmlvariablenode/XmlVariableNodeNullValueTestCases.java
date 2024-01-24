/*
 * Copyright (c) 1998, 2024 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith - May 2013
package org.eclipse.persistence.testing.jaxb.xmlvariablenode;

import java.util.ArrayList;

import org.eclipse.persistence.exceptions.XMLMarshalException;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class XmlVariableNodeNullValueTestCases extends JAXBWithJSONTestCases{
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootNull.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlvariablenode/rootNull.json";

    public XmlVariableNodeNullValueTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setClasses(new Class<?>[]{Root.class});
        expectsMarshalException = true;
    }

    @Override
    public boolean isUnmarshalTest(){
        return false;
    }

    @Override
    protected Object getControlObject() {
        Root r = new Root();
        r.name = "theRootName";
        r.things = new ArrayList<Thing>();
        Thing thing1 = new Thing();
        thing1.thingName = null;
        thing1.thingValue = "thingavalue";

        Thing thing2 = new Thing();
        thing2.thingName = "thingb";
        thing2.thingValue = "thingbvalue";

        Thing thing3 = new Thing();
        thing3.thingName = "thingc";
        thing3.thingValue = "thingcvalue";
        r.things.add(thing1);
        r.things.add(thing2);
        r.things.add(thing3);
        return r;
    }

    @Override
    public void assertMarshalException(Exception exception) throws Exception {
        Throwable nested = exception.getCause();// getLinkedException();
        assertTrue(nested instanceof XMLMarshalException);
        if(((XMLMarshalException)nested).getErrorCode() == XMLMarshalException.NULL_VALUE_NOT_ALLOWED_FOR_VARIABLE){
            return;
        }
        fail("Expected exception did not occur.");
    }
}
