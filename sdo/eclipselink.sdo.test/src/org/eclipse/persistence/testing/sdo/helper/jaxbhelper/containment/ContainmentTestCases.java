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
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.containment;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.mappings.Child1;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.mappings.Child2;
import org.eclipse.persistence.testing.sdo.helper.jaxbhelper.mappings.MappingsProject;

import commonj.sdo.DataObject;
import commonj.sdo.Property;
import commonj.sdo.Type;
import commonj.sdo.helper.XMLDocument;

public class ContainmentTestCases extends SDOTestCase {
    private static final String XML_SCHEMA = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/containment/containment.xsd";

    private JAXBHelperContext jaxbHelperContext;

    public ContainmentTestCases(String name) {
        super(name);
    }

    public void setUp() {
        ContainmentProject project = new ContainmentProject();
        XMLContext xmlContext = new XMLContext(project);
        JAXBContext jaxbContext = new JAXBContext(xmlContext);
        jaxbHelperContext = new JAXBHelperContext(jaxbContext);

        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA);
        jaxbHelperContext.getXSDHelper().define(xsd, null);
    }

    public void test1() throws IOException {
        DataObject root1 = jaxbHelperContext.getDataFactory().create("urn:containment", "root");
        DataObject root2 = jaxbHelperContext.getDataFactory().create("urn:containment", "root");

        DataObject child1 = jaxbHelperContext.getDataFactory().create("urn:containment", "child");
        DataObject child2 = jaxbHelperContext.getDataFactory().create("urn:containment", "child");

        assertTrue(child1.getContainer() == null);
        assertTrue(child2.getContainer() == null);

        root1.set("child", child1);
        assertTrue(child1.getContainer() == root1);

        root1.unset("child");
        assertTrue(child1.getContainer() == null);

        List list = new ArrayList();
        list.add(child1);
        list.add(child2);
        root2.setList("child-many", list);

        assertTrue(child1.getContainer() == root2);
        assertTrue(child2.getContainer() == root2);

        root2.getList("child-many").remove(child2);
        assertTrue(child2.getContainer() == null);

        root2.unset("child-many");
        assertTrue(child1.getContainer() == null);

        list = new ArrayList();
        list.add(child1);
        list.add(child2);
        root2.setList("child-many", list);

        root1.set("child", child1);
        assertFalse(root2.getList("child-many").contains(child1));
        assertTrue(child1.getContainer() == root1);
    }

    public void testMove1() {
        Type rootType = jaxbHelperContext.getTypeHelper().getType("urn:containment", "root");
        Property childProperty = rootType.getProperty("child");

        DataObject root1DO = jaxbHelperContext.getDataFactory().create(rootType);
        DataObject childDO = root1DO.createDataObject(childProperty);
        assertNotNull(root1DO.get(childProperty));

        DataObject root2DO = jaxbHelperContext.getDataFactory().create(rootType);
        assertNull(root2DO.get(childProperty));

        root2DO.set(childProperty, root1DO.get(childProperty));
        assertNotNull(root2DO.get(childProperty));
        assertNull(root1DO.get(childProperty));
    }

    public void testMove2() {
        Type rootType = jaxbHelperContext.getTypeHelper().getType("urn:containment", "root");
        Property childProperty = rootType.getProperty("child");

        Root root1 = new Root();
        Child child = new Child();
        root1.setChildProperty(child);
        assertNotNull(root1.getChildProperty());

        Root root2 = new Root();
        assertNull(root2.getChildProperty());

        DataObject root1DO = jaxbHelperContext.wrap(root1);
        DataObject root2DO = jaxbHelperContext.wrap(root2);

        root2DO.set(childProperty, root1DO.get(childProperty));
        assertNotNull(root2DO.get(childProperty));
        assertNull(root1DO.get(childProperty));
    }

    public void testCollectionMove1() {
        Type rootType = jaxbHelperContext.getTypeHelper().getType("urn:containment", "root");
        Property childManyProperty = rootType.getProperty("child-many");

        DataObject root1DO = jaxbHelperContext.getDataFactory().create(rootType);
        DataObject childDO = root1DO.createDataObject(childManyProperty);
        assertFalse(root1DO.getList(childManyProperty).isEmpty());

        DataObject root2DO = jaxbHelperContext.getDataFactory().create(rootType);
        assertTrue(root2DO.getList(childManyProperty).isEmpty());

        root2DO.getList(childManyProperty).add(childDO);
        assertTrue(root1DO.getList(childManyProperty).isEmpty());
        assertFalse(root2DO.getList(childManyProperty).isEmpty());
    }

    public void testCollectionMove2() {
        Type rootType = jaxbHelperContext.getTypeHelper().getType("urn:containment", "root");
        Property childManyProperty = rootType.getProperty("child-many");

        Root root1 = new Root();
        Child child = new Child();
        ArrayList children = new ArrayList(1);
        children.add(child);
        root1.setChildCollectionProperty(children);
        assertFalse(root1.getChildCollectionProperty().isEmpty());

        Root root2 = new Root();
        root2.setChildCollectionProperty(new ArrayList(1));
        assertTrue(root2.getChildCollectionProperty().isEmpty());

        DataObject root1DO = jaxbHelperContext.wrap(root1);
        DataObject root2DO = jaxbHelperContext.wrap(root2);
        DataObject childDO = (DataObject) root1DO.getList(childManyProperty).get(0);

        root2DO.getList(childManyProperty).add(childDO);
        assertTrue(root1DO.getList(childManyProperty).isEmpty());
        assertFalse(root2DO.getList(childManyProperty).isEmpty());
    }

    public void testGetContainer() {
        Root root = new Root();
        Child child = new Child();
        root.setChildProperty(child);

        Type rootType = jaxbHelperContext.getTypeHelper().getType("urn:containment", "root");
        Property childProperty = rootType.getProperty("child");

        DataObject rootDO = jaxbHelperContext.wrap(root);
        DataObject childDO = rootDO.getDataObject(childProperty);
        
        DataObject containerDO = childDO.getContainer();
        assertNotNull(containerDO);
        assertSame(rootDO, containerDO);

        Property containmentProperty = childDO.getContainmentProperty();
        assertNotNull(containmentProperty);
        assertSame(childProperty, containmentProperty);
    }

    public void testGetManyContainer() {
        Root root = new Root();
        Child child = new Child();
        ArrayList children = new ArrayList(1);
        children.add(child);
        root.setChildCollectionProperty(children);

        Type rootType = jaxbHelperContext.getTypeHelper().getType("urn:containment", "root");
        Property childManyProperty = rootType.getProperty("child-many");

        DataObject rootDO = jaxbHelperContext.wrap(root);
        DataObject childDO = (DataObject) rootDO.getList(childManyProperty).get(0);

        DataObject containerDO = childDO.getContainer();
        assertNotNull(containerDO);
        assertSame(rootDO, containerDO);

        Property containmentProperty = childDO.getContainmentProperty();
        assertNotNull(containmentProperty);
        assertSame(childManyProperty, containmentProperty);
    }

    public void tearDown() {
    }

}
