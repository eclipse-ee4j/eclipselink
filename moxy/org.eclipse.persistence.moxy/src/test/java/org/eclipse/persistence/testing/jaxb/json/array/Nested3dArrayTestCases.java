/*
 * Copyright (c) 2019, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Radek Felcman - 2.7.5 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.array;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONTestCases;

/**
 * Tests marshall/unmarshal of 3d array (int[]).
 *
 * @author Radek Felcman
 *
 */
public class Nested3dArrayTestCases extends JSONTestCases {

    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/array/nested_3d_array.json";

    public Nested3dArrayTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[]{Root3DArray.class});
        setControlJSON(JSON_RESOURCE);
    }

    public void setUp() throws Exception{
        super.setUp();
        jsonMarshaller.setProperty(MarshallerProperties.JSON_DISABLE_NESTED_ARRAY_NAME, true);
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
    }

    protected Object getControlObject() {
        Root3DArray root3DArray = new Root3DArray();
        Integer[][][] int3DArray = {{{1, 2}, {3, 4}, {5, 6}}, {{7, 8}, {9, 10}, {11,12}}};
        root3DArray.setArray3d(int3DArray);
        return root3DArray;
    }

}
