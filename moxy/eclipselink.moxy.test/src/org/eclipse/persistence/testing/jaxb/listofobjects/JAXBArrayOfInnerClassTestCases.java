/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//     Denise Smith  December 15, 2009
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.listofobjects.ClassWithInnerClass.MyInner;

public class JAXBArrayOfInnerClassTestCases extends JAXBListOfObjectsTestCases {
    protected final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/innerClassArray.xml";
    protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/innerClassArray.json";
    protected final static String XML_RESOURCE_NO_XSI_TYPE = "org/eclipse/persistence/testing/jaxb/listofobjects/innerClassArray.xml";

    public MyInner[] myArray;

    @Override
    protected Map<String, String> getAdditationalNamespaces() {
        Map<String, String> namespaces = new HashMap<>();
        namespaces.put("listOfObjectsNamespace", "x");
        namespaces.put("examplenamespace", "ns0");
        return namespaces;
    }

    public JAXBArrayOfInnerClassTestCases(String name) throws Exception {
        super(name);
        init();
        initXsiType();
    }

    protected Type getTypeToUnmarshalTo() throws Exception {
        return getClass().getField("myArray").getType();
    }

    public void init() throws Exception {
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Type[] types = new Type[1];
        types[0] = getTypeToUnmarshalTo();
        setTypes(types);
    }

    protected Object getControlObject() {
        MyInner myInner1 = new MyInner();
        myInner1.innerName = "aaa";

        MyInner myInner2= new MyInner();
        myInner2.innerName = "bbb";

        MyInner[] theArray = new MyInner[2];
        theArray[0] = myInner1;
        theArray[1] = myInner2;

        QName qname = new QName("examplenamespace", "root");
        JAXBElement jaxbElement = new JAXBElement(qname, Object.class ,theArray);

        return jaxbElement;
    }

    public List<InputStream> getControlSchemaFiles() {
        InputStream instream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/innerClassArray.xsd");
        List<InputStream> controlSchema = new ArrayList<InputStream>();
        controlSchema.add(instream);
        return controlSchema;
    }

    protected String getNoXsiTypeControlResourceName() {
        return XML_RESOURCE_NO_XSI_TYPE;
    }
}
