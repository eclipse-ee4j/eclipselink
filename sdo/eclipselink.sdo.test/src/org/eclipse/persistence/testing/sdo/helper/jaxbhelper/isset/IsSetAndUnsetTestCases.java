/*******************************************************************************
* Copyright (c) 1998, 2010 Oracle. All rights reserved.
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
package org.eclipse.persistence.testing.sdo.helper.jaxbhelper.isset;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sdo.helper.jaxb.JAXBHelperContext;
import org.eclipse.persistence.testing.sdo.SDOTestCase;

import commonj.sdo.DataObject;
import commonj.sdo.Property;

public class IsSetAndUnsetTestCases extends SDOTestCase {
    private static final String XML_SCHEMA = "org/eclipse/persistence/testing/sdo/helper/jaxbhelper/isset/isset.xsd";

    private JAXBHelperContext jaxbHelperContext;

    public IsSetAndUnsetTestCases(String name) {
        super(name);
    }

    public void setUp() {
        IsSetProject project = new IsSetProject();
        XMLContext xmlContext = new XMLContext(project);
        JAXBContext jaxbContext = new JAXBContext(xmlContext);
        jaxbHelperContext = new JAXBHelperContext(jaxbContext);

        InputStream xsd = Thread.currentThread().getContextClassLoader().getResourceAsStream(XML_SCHEMA);
        jaxbHelperContext.getXSDHelper().define(xsd, null);
    }

    public void test1() throws IOException {
        DataObject root1 = jaxbHelperContext.getDataFactory().create("urn:isset", "root");

        DataObject child1 = jaxbHelperContext.getDataFactory().create("urn:isset", "child");
        DataObject child2 = jaxbHelperContext.getDataFactory().create("urn:isset", "child");

        Property childProp = root1.getType().getProperty("child");
        Property childManyProp = root1.getType().getProperty("child-many");

        assertFalse(root1.isSet(childProp));
        assertFalse(root1.isSet(childManyProp));

        ArrayList list = new ArrayList();
        root1.setList(childManyProp, list);

        //empty list means isSet = false
        assertFalse(root1.isSet(childManyProp));
        root1.getList(childManyProp).add(child1);
        assertTrue(root1.isSet(childManyProp));

        root1.getList(childManyProp).add(child2);
        root1.unset(childManyProp);

        assertTrue(root1.getList(childManyProp).size() == 0);
        assertFalse(root1.isSet(childManyProp));

        root1.set(childProp, child2);
        assertTrue(root1.isSet(childProp));
        root1.set(childProp, null);
        assertFalse(root1.isSet(childProp));
        root1.set(childProp, child2);
        root1.unset(childProp);
        assertFalse(root1.isSet(childProp));
        assertTrue(root1.get(childProp) == null);

    }

    public void test2() throws IOException {
        Root rootPOJO = new Root();
        DataObject root1 = jaxbHelperContext.wrap(rootPOJO);

        Child child1POJO = new Child();
        DataObject child1 = jaxbHelperContext.wrap(child1POJO);

        Child child2POJO = new Child();
        DataObject child2 = jaxbHelperContext.wrap(child2POJO);

        Property childProp = root1.getType().getProperty("child");
        Property childManyProp = root1.getType().getProperty("child-many");

        assertFalse(root1.isSet(childProp));
        assertFalse(root1.isSet(childManyProp));

        ArrayList list = new ArrayList();
        root1.setList(childManyProp, list);

        //empty list means isSet = false
        assertFalse(root1.isSet(childManyProp));
        root1.getList(childManyProp).add(child1);
        assertTrue(root1.isSet(childManyProp));

        root1.getList(childManyProp).add(child2);
        root1.unset(childManyProp);

        assertTrue(root1.getList(childManyProp).size() == 0);
        assertFalse(root1.isSet(childManyProp));

        root1.set(childProp, child2);
        assertTrue(root1.isSet(childProp));
        root1.set(childProp, null);
        assertFalse(root1.isSet(childProp));
        root1.set(childProp, child2);
        root1.unset(childProp);
        assertFalse(root1.isSet(childProp));
        assertTrue(root1.get(childProp) == null);

    }

    public void tearDown() {
    }

}
