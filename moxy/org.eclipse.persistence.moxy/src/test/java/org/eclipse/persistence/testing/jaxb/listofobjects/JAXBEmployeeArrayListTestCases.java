/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
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
//     Denise Smith  June 05, 2009 - Initial implementation
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JAXBEmployeeArrayListTestCases extends JAXBEmployeeListTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/employeeArrayList.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/employeeArrayList.json";
    private final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/employeeArrayListNoXsiType.xml";

    public JAXBEmployeeArrayListTestCases(String name) throws Exception {
        super(name);
    }

    public List<InputStream> getControlSchemaFiles(){

        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/arrayListEmployee.xsd");

        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);
        return controlSchema;
    }


    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        Type[] types = new Type[1];
        types[0] = getTypeToUnmarshalTo();
        setTypes(types);
    }

    protected Type getTypeToUnmarshalTo() throws Exception {
        Field fld = ListofObjects.class.getField("empArrayList");
        return fld.getGenericType();
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }
}
