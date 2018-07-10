/*
 * Copyright (c) 2011, 2018 Oracle and/or its affiliates. All rights reserved.
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
// Matt MacIvor - 2.3
package org.eclipse.persistence.testing.jaxb.readonly;


import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class ReadAndWriteOnlyTestCases extends JAXBWithJSONTestCases {

    public static final String READ_CONTROL_DOC = "org/eclipse/persistence/testing/jaxb/readonly/readcontrol.xml";
    public static final String WRITE_CONTROL_DOC = "org/eclipse/persistence/testing/jaxb/readonly/writecontrol.xml";
    public static final String JSON_READ_CONTROL_DOC = "org/eclipse/persistence/testing/jaxb/readonly/readcontrol.json";
    public static final String JSON_WRITE_CONTROL_DOC = "org/eclipse/persistence/testing/jaxb/readonly/writecontrol.json";

    public ReadAndWriteOnlyTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {RootObject.class});
        setControlDocument(READ_CONTROL_DOC);
        setWriteControlDocument(WRITE_CONTROL_DOC);
        setControlJSON(JSON_READ_CONTROL_DOC);
        setWriteControlJSON(JSON_WRITE_CONTROL_DOC);
    }

    @Override
    protected Object getControlObject() {
        return null;
    }

    @Override
    public Object getReadControlObject() {
        RootObject obj = new RootObject();
        obj.setReadOnlyString("readOnlyString");
        obj.setReadOnlyStringArray(new String[]{"string1", "string2", "string3"});
        return obj;
    }

    @Override
    public Object getWriteControlObject() {
        RootObject obj = new RootObject();
        obj.writeOnlyString = "writeOnlyString";
        obj.writeOnlyStringArray = new String[]{"string1", "string2", "string3"};
        return obj;
    }

    @Override
    public void testRoundTrip() throws Exception{
    }
}
