/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith  July 05, 2009 - Initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.annotations;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.internal.jaxb.JaxbClassLoader;
import org.eclipse.persistence.jaxb.compiler.Generator;
import org.eclipse.persistence.jaxb.compiler.Property;
import org.eclipse.persistence.jaxb.compiler.TypeInfo;
import org.eclipse.persistence.jaxb.javamodel.JavaClass;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelImpl;
import org.eclipse.persistence.jaxb.javamodel.reflection.JavaModelInputImpl;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.jaxb.JAXBWithJSONTestCases;

public class PropertyTypeTestCases extends JAXBWithJSONTestCases {

    private static final String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/propertyType.xml";
    private static final String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/annotations/propertyType.json";

    public PropertyTypeTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] { TestObject.class });
        setControlDocument(XML_RESOURCE);
        setControlJSON(JSON_RESOURCE);
    }

    public Object getControlObject() {

        TestObject testObject = new TestObject();

        String s = new String("123456789");
        byte[] bytes = s.getBytes();
            
        testObject.byteArrayTest = bytes;

        testObject.byteArrayListTest = new ArrayList<byte[]>();
        testObject.byteArrayListTest.add(bytes);
        testObject.byteArrayListTest.add(bytes);
        
        testObject.booleanTest = Boolean.TRUE;

        testObject.booleanListTest = new ArrayList<Object>();
        testObject.booleanListTest.add(Boolean.FALSE);
        testObject.booleanListTest.add(Boolean.TRUE);

        QName rootQName = new QName("root");
        JAXBElement jaxbElement = new JAXBElement<Object>(rootQName,Object.class, testObject);
        return jaxbElement;
    }

    public void testPropertyTypes() throws Exception {
        JaxbClassLoader classLoader = new JaxbClassLoader(Thread.currentThread().getContextClassLoader());
        Generator generator = new Generator(new JavaModelInputImpl(new Class[] { TestObject.class }, new JavaModelImpl(this.classLoader)));

        Project proj = generator.generateProject();
        TypeInfo info = generator.getAnnotationsProcessor().getTypeInfo().get("org.eclipse.persistence.testing.jaxb.annotations.TestObject");

        Property booleanProp = info.getProperties().get("booleanTest");
        JavaClass type = booleanProp.getType();
        assertEquals("java.lang.Boolean", type.getRawName());

        Property booleanListProp = info.getProperties().get("booleanListTest");
        type = booleanListProp.getType();
        assertEquals("java.util.List", type.getRawName());

        Property byteArrayProp = info.getProperties().get("byteArrayTest");
        type = byteArrayProp.getType();
        assertEquals("java.lang.String", type.getRawName());

        Property byteArrayListProp = info.getProperties().get(
                "byteArrayListTest");
        type = byteArrayListProp.getType();
        assertEquals("java.util.List", type.getRawName());
        assertEquals("java.lang.String", byteArrayListProp.getGenericType().getRawName());
    }

    public void testSchemaGen() throws Exception {
    	InputStream controlInputStream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/jaxb/annotations/propertyType.xsd");
    	
    	List<InputStream> controlSchemas = new ArrayList<InputStream>();    	
    	controlSchemas.add(controlInputStream);
    	testSchemaGen(controlSchemas);
    }    
}
