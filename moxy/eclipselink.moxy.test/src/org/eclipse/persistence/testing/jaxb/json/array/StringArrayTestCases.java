/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Radek Felcman - 2.7.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.array;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONTestCases;
import org.eclipse.persistence.testing.jaxb.json.type.model.Person;

/**
 * Tests marshall/unmarshal of plain String array (String[]).
 *
 * @author Radek Felcman
 *
 */
public class StringArrayTestCases extends JSONTestCases {

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/array/string_array.json";

    public StringArrayTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{String.class});
        setControlJSON(JSON_RESOURCE);
    }

    public void setUp() throws Exception{
        super.setUp();
        jsonMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
    }

    protected Object getControlObject() {
        String[] stringArray = {"aaa", "bbb", "ccc", null};
        return stringArray;
    }

}
