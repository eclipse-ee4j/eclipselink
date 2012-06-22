/*******************************************************************************
* Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
* This program and the accompanying materials are made available under the
* terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
* which accompanies this distribution.
* The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
* and the Eclipse Distribution License is available at
* http://www.eclipse.org/org/documents/edl-v10.php.
*
* Contributors:
*     bdoughan - Jan 27/2009 - 1.1 - Initial implementation
******************************************************************************/
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.oppositeproperty;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.DataFactory;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.helper.SDOHelperContext;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;

public class OppositePropertyTestCases extends SDOTestCase {

    private static final String XML_SCHEMA = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/oppositeproperty/OppositeProperty.xsd";

    private JAXBHelperContext jaxbHelperContext;

    public OppositePropertyTestCases(String name) {
        super(name);
    }

    public void setUp() {
        OppositeProject project = new OppositeProject();
        XMLContext xmlContext = new XMLContext(project);
        JAXBContext jaxbContext = new JAXBContext(xmlContext);
        jaxbHelperContext = new JAXBHelperContext(jaxbContext);
        
        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA);
        jaxbHelperContext.getXSDHelper().define(xsd, null);
    }

    public void testOppositePropertySet() {
        DataObject rootDO = jaxbHelperContext.getDataFactory().create("urn:opposite", "root");
        DataObject child1DO = rootDO.createDataObject("child1");
        DataObject child2DO = rootDO.createDataObject("child2");

        Property child2Property = child1DO.getType().getProperty("child2");
        child1DO.set(child2Property, child2DO);
        this.assertEquals(child1DO, child2DO.get("child1"));
        
        Child2 child2 = (Child2) jaxbHelperContext.unwrap(child2DO);
        this.assertNotNull(child2.getChild1());
    }

    public void testOppositePropertyCleared1() {
        DataObject rootDO = jaxbHelperContext.getDataFactory().create("urn:opposite", "root");
        DataObject child1DO = rootDO.createDataObject("child1");
        DataObject child2DO = rootDO.createDataObject("child2");
        DataObject newChild2DO =  rootDO.createDataObject("child2");
        
        Property child2Property = child1DO.getType().getProperty("child2");
        child1DO.set(child2Property, child2DO);
        child1DO.set(child2Property, newChild2DO);

        this.assertNull(child2DO.get("child1"));
        this.assertEquals(child1DO, newChild2DO.get("child1"));

        Child2 child2 = (Child2) jaxbHelperContext.unwrap(child2DO);
        this.assertNull(child2.getChild1());

        Child2 newChild2 = (Child2) jaxbHelperContext.unwrap(newChild2DO);
        this.assertNotNull(newChild2.getChild1());        
    }

    public void testOppositePropertyCleared2() {
        DataObject rootDO = jaxbHelperContext.getDataFactory().create("urn:opposite", "root");
        DataObject child1DO = jaxbHelperContext.getDataFactory().create("urn:opposite", "child1");
        DataObject child2DO = jaxbHelperContext.getDataFactory().create("urn:opposite", "child2");
        DataObject newChild2DO =  jaxbHelperContext.getDataFactory().create("urn:opposite", "child2");

        Property child2Property = child1DO.getType().getProperty("child2");
        child1DO.set(child2Property, child2DO);
        child1DO.set(child2Property, newChild2DO);

        this.assertNull(child2DO.get("child1"));
        this.assertEquals(child1DO, newChild2DO.get("child1"));
        
        Child2 child2 = (Child2) jaxbHelperContext.unwrap(child2DO);
        this.assertNull(child2.getChild1());

        Child2 newChild2 = (Child2) jaxbHelperContext.unwrap(newChild2DO);
        this.assertNotNull(newChild2.getChild1());        
    }

    public void testOppositePropertySetCollectionCaseAdd() {
        DataObject rootDO = jaxbHelperContext.getDataFactory().create("urn:opposite", "root");
        DataObject child1DO = rootDO.createDataObject("child1");
        DataObject child2DO = rootDO.createDataObject("child2");

        Property child2CollectionProperty = child1DO.getType().getProperty("child2collection");
        List list = child1DO.getList(child2CollectionProperty);
        list.add(child2DO);
        this.assertEquals(child1DO, child2DO.get("child1"));
        
        Child2 child2 = (Child2) jaxbHelperContext.unwrap(child2DO);
        this.assertNotNull(child2.getChild1());
    }

    public void testOppositePropertySetCollectionCaseSet() {
        DataObject rootDO = jaxbHelperContext.getDataFactory().create("urn:opposite", "root");
        DataObject child1DO = rootDO.createDataObject("child1");
        DataObject child2DO = rootDO.createDataObject("child2");

        Property child2CollectionProperty = child1DO.getType().getProperty("child2collection");
        List child2Collection = new ArrayList();
        child2Collection.add(child2DO);
        child1DO.setList(child2CollectionProperty, child2Collection);
        
        this.assertEquals(child1DO, child2DO.get("child1"));
        
        Child2 child2 = (Child2) jaxbHelperContext.unwrap(child2DO);
        this.assertNotNull(child2.getChild1());
    }

    public void tearDown() {
    }
}
