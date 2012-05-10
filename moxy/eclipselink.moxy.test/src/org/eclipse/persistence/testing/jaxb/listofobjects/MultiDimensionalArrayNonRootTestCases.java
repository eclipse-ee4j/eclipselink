/*******************************************************************************
* Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - May 10/2010 - 2.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.jaxb.listofobjects;

import java.io.InputStream;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.JAXBTestCases;

public class MultiDimensionalArrayNonRootTestCases extends JAXBListOfObjectsTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/MultiDimensionalArrayNonRoot.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/listofobjects/MultiDimensionalArrayNonRoot.json";
    
    public MultiDimensionalArrayNonRootTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
        Class[] classes = new Class[1];
        classes[0] = MultiDimensionalArrayRoot.class;
        setClasses(classes);
    }

    @Override
    public List<InputStream> getControlSchemaFiles() {
        List<InputStream> controlSchema = new ArrayList<InputStream>(3);
        controlSchema.add(ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/MultiDimensionalArrayNonRoot1.xsd"));
        controlSchema.add(ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/MultiDimensionalArrayNonRoot2.xsd"));
        controlSchema.add(ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/listofobjects/MultiDimensionalArrayNonRoot3.xsd"));
        return controlSchema;
    }

    @Override
    protected String getNoXsiTypeControlResourceName() {
        return null;
    }

    @Override
    protected Type getTypeToUnmarshalTo() throws Exception {
        return MultiDimensionalArrayRoot.class;
    }

    @Override
    protected Object getControlObject() {
        MultiDimensionalArrayRoot root = new MultiDimensionalArrayRoot();

        int[][][] int3dArray = new int[2][3][2];
        int3dArray[0][0][0] = 1;
        int3dArray[0][0][1] = 2;
        int3dArray[0][1][0] = 3;
        int3dArray[0][1][1] = 4;
        int3dArray[0][2][0] = 5;
        int3dArray[0][2][1] = 6;
        int3dArray[1][0][0] = 7;
        int3dArray[1][0][1] = 8;
        int3dArray[1][1][0] = 9;
        int3dArray[1][1][1] = 10;
        int3dArray[1][2][0] = 11;
        int3dArray[1][2][1] = 12;
        root.setInt3dArray(int3dArray);

        int[][] int2dArray = new int[3][2];
        int2dArray[0][0] = 1;
        int2dArray[0][1] = 2;
        int2dArray[1][0] = 3;
        int2dArray[1][1] = 4;
        int2dArray[2][0] = 5;
        int2dArray[2][1] = 6;
        root.setInt2dArray(int2dArray);

        char[][] char2dArray = new char[2][4];
        char2dArray[0][0] = 'a';
        char2dArray[0][1] = 'b';
        char2dArray[0][2] = 'c';
        char2dArray[0][3] = 'd';
        char2dArray[1][0] = 'e';
        char2dArray[1][1] = 'f';
        char2dArray[1][2] = 'g';
        char2dArray[1][3] = 'h';
        root.setChar2dArray(char2dArray);

        Employee[][] employee2dArray = new Employee[1][2];
        employee2dArray[0][0] = new Employee();
        employee2dArray[0][0].id = 1;
        employee2dArray[0][0].firstName = "Jane";
        employee2dArray[0][0].lastName = "Doe";
        employee2dArray[0][1] = new Employee();
        employee2dArray[0][1].id = 2;
        employee2dArray[0][1].firstName = "John";
        employee2dArray[0][1].lastName = "Smith";
        root.setEmployee2dArray(employee2dArray);

        ClassWithInnerClass.MyInner[][] innerClass2dArray = new ClassWithInnerClass.MyInner[2][1];
        innerClass2dArray[0][0] = new ClassWithInnerClass.MyInner();
        innerClass2dArray[0][0].innerName = "A";
        innerClass2dArray[1][0] = new ClassWithInnerClass.MyInner();
        innerClass2dArray[1][0].innerName = "B";
        root.setInnerClass2dArray(innerClass2dArray);
        
        JAXBElement jaxbElement = new JAXBElement(new QName("urn:example", "root"), Object.class, root);
        return jaxbElement;
    }
    
    @Override    
    public void xmlToObjectTest(Object testObject) throws Exception {
        testObject = new JAXBElement(new QName("urn:example", "root"), Object.class, testObject);
        super.xmlToObjectTest(testObject);
    }
    
    @Override    
    public void jsonToObjectTest(Object testObject) throws Exception {
        testObject = new JAXBElement(new QName("urn:example", "root"), Object.class, testObject);
        super.jsonToObjectTest(testObject);
    }

}