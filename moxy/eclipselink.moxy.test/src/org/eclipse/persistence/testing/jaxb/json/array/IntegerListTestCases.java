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

import java.util.ArrayList;
import java.util.List;

/**
 * Tests marshall/unmarshal List of Integer (List<Integer>).
 *
 * @author Radek Felcman
 *
 */
public class IntegerListTestCases extends JSONTestCases {

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/array/integer_array.json";

    public IntegerListTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Integer.class});
        setControlJSON(JSON_RESOURCE);
    }

    public void setUp() throws Exception{
        super.setUp();
        jsonMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
    }

    protected Object getControlObject() {
        List<Integer> integerList = new ArrayList<>();
        integerList.add(-78);
        integerList.add(0);
        integerList.add(33);
        integerList.add(600);
        return integerList;
    }

}
