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
//     Denise Smith - 2.4 - January 2013
package org.eclipse.persistence.testing.jaxb.xmlelement.nulls;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class NullTestCases extends JAXBWithJSONTestCases{

    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/nulls.xml";
    private final static String XML_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/nulls_write.xml";
    private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/nulls.json";
    private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/xmlelement/nulls_write.json";

    public NullTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setWriteControlDocument(XML_WRITE_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        setWriteControlJSON(JSON_WRITE_RESOURCE);
        setClasses(new Class[]{Root.class, Child.class});
    }

    @Override
    protected Object getControlObject() {
        Root root = new Root();
        root.setString1(null);
        root.setString2(null);
        root.setString3(null);
        root.setChild1(null);
        root.setChild2(null);
        root.setChildList1(null);
        root.setChildList2(null);
        List<Child> childList = new ArrayList<Child>();
        childList.add(null);
        root.setChildList3(childList);
        root.setChildList4(childList);
        root.setStringList1(null);
        root.setStringList2(null);
        List<String> stringList = new ArrayList<String>();
        stringList.add(null);
        root.setStringList3(stringList);
        root.setStringList4(stringList);

        root.setByteArrayList1(null);
        root.setByteArrayList2(null);
        List<byte[]> byteArrayList = new ArrayList<byte[]>();
        byteArrayList.add(null);
        root.setByteArrayList3(byteArrayList);
        root.setByteArrayList4(byteArrayList);

        root.setRefList1(null);
        root.setRefList2(null);
        List<Object> refList = new ArrayList<Object>();
        refList.add(null);
        root.setRefList3(refList);
        root.setRefList4(refList);

        root.setRefComplexList1(null);
        root.setRefComplexList2(null);
        List<Object> refComplexList = new ArrayList<Object>();
        refComplexList.add(null);
        root.setRefComplexList3(refComplexList);
        root.setRefComplexList4(refComplexList);
        return root;
    }

    @Override
    public Object getReadControlObject() {
        Root root = new Root();
        root.setString1(null);  //RI has "" ignores the xsi:nil since nillable = false
        root.setString2(null);
        root.setString3("");

        root.setChild1(new Child()); //ignores the xsi:nil since nillable = false
        root.setChild2(null);
        List<Child> childList = new ArrayList<Child>();
        childList.add(null);
        root.setChildList1(childList);
        root.setChildList2(childList);
        root.setChildList3(childList);
        root.setChildList4(childList);

        List<String> stringList = new ArrayList<String>();
        stringList.add(null);
        root.setStringList1(stringList);
        root.setStringList2(stringList);
        root.setStringList3(stringList);
        root.setStringList4(stringList);


        List<byte[]> byteArrayList = new ArrayList<byte[]>();
        byteArrayList.add(null);
        root.setByteArrayList1(byteArrayList);
        root.setByteArrayList2(byteArrayList);
        root.setByteArrayList3(byteArrayList);
        root.setByteArrayList4(byteArrayList);

        List<Object> refs= new ArrayList<Object>();
        refs.add(null);
        refs.add(null);
        root.setRefList1(refs);
        root.setRefList2(refs);
        root.setRefList3(refs);
        root.setRefList4(refs);

        List<Object> refComplexList = new ArrayList<Object>();
        refComplexList.add(null);
        root.setRefComplexList1(refComplexList);
        root.setRefComplexList2(refComplexList);
        root.setRefComplexList3(refComplexList);
        root.setRefComplexList4(refComplexList);

        return root;
    }

    @Override
    public Object getJSONReadControlObject() {
        Root root = new Root();
        root.setString1(null);
        root.setString2(null);
        root.setString3("");

        root.setChild1(null);
        root.setChild2(null);
        List<Child> childList = new ArrayList<Child>();
        childList.add(null);
        root.setChildList1(null);
        root.setChildList2(null);
        root.setChildList3(childList);
        root.setChildList4(childList);

        List<String> stringList = new ArrayList<String>();
        stringList.add(null);
        root.setStringList1(null);
        root.setStringList2(null);
        root.setStringList3(stringList);
        root.setStringList4(stringList);

        root.setByteArrayList1(null);
        root.setByteArrayList2(null);
        List<byte[]> byteArrayList = new ArrayList<byte[]>();
        byteArrayList.add(null);
        root.setByteArrayList3(byteArrayList);
        root.setByteArrayList4(byteArrayList);

        root.setRefList1(null);
        root.setRefList2(null);
        List<Object> refList = new ArrayList<Object>();
        refList.add(null);
        root.setRefList3(refList);
        root.setRefList4(refList);

        root.setRefComplexList1(null);
        root.setRefComplexList2(null);
        List<Object> refComplexList = new ArrayList<Object>();
        refComplexList.add(null);
        root.setRefComplexList3(refComplexList);
        root.setRefComplexList4(refComplexList);
        return root;
    }


    public void testRoundTrip(){
        //not applicable
    }

}
