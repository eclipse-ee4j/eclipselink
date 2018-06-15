/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
 */

// Contributors:
//  - rbarkhouse - 06 November 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class SetByXPathTests extends junit.framework.TestCase {

    private JAXBContext eCtx;
    private TestBean controlObject;

    private final String CHANGED_VALUE = "CHANGED_VALUE";

    public String getName() {
        return "JAXB Context setByXPath Tests: " + super.getName();
    }

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        eCtx = (JAXBContext) JAXBContextFactory.createContext(new Class[] { TestBean.class }, null);
    }

    private TestBean getControlObject() {
        if (controlObject == null) {
            controlObject = TestBean.example();
        }
        return controlObject;
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        controlObject = TestBean.example();
    }

    // @XmlPath("info/desc/text()") String description;
    public void testSetDirect() throws JAXBException {
        eCtx.setValueByXPath(getControlObject(), "info/desc/text()", null, CHANGED_VALUE);
        assertEquals(getControlObject().description, CHANGED_VALUE);

        eCtx.setValueByXPath(getControlObject(), "companion/info/desc/text()", null, CHANGED_VALUE);
        assertEquals(getControlObject().companion.description, CHANGED_VALUE);

        eCtx.setValueByXPath(getControlObject(), "sub-bean[1]/info/desc/text()", null, CHANGED_VALUE);
        assertEquals(getControlObject().subBean[0].description, CHANGED_VALUE);
    }

    // @XmlPath("companion") TestBean companion;
    public void testSetComposite() throws JAXBException {
        TestBean o = new TestBean();
        o.description = CHANGED_VALUE;

        eCtx.setValueByXPath(getControlObject(), "companion", null, o);
        assertEquals(getControlObject().companion.description, CHANGED_VALUE);

        eCtx.setValueByXPath(getControlObject(), "sub-bean[2]/companion", null, o);
        assertEquals(getControlObject().subBean[1].companion.description, CHANGED_VALUE);
    }

    // @XmlPath("info/name/text()") String[] name;
    public void testSetPrimitiveArray() throws JAXBException {
        eCtx.setValueByXPath(getControlObject(), "info/name[1]/text()", null, CHANGED_VALUE);
        assertEquals(getControlObject().name[0], CHANGED_VALUE);

        eCtx.setValueByXPath(getControlObject(), "sub-bean[2]/info/name[1]/text()", null, CHANGED_VALUE);
        assertEquals(getControlObject().subBean[1].name[0], CHANGED_VALUE);
    }

    // @XmlPath("info/roles/text()") ArrayList<String> roles;
    public void testSetPrimitiveList() throws JAXBException {
        ArrayList<String> list = new ArrayList<String>();
        list.add(CHANGED_VALUE);

        eCtx.setValueByXPath(getControlObject(), "info/roles/text()", null, list);
        assertEquals(getControlObject().roles, list);

        eCtx.setValueByXPath(getControlObject(), "sub-bean[2]/info/roles/text()", null, list);
        assertEquals(getControlObject().subBean[1].roles, list);
    }

    // @XmlPath("sub-bean") TestBean[] subBean;
    public void testSetArray() throws JAXBException {
        TestBean o = new TestBean();
        o.description = CHANGED_VALUE;

        TestBean[] array = new TestBean[1];
        array[0] = o;

        eCtx.setValueByXPath(getControlObject(), "sub-bean", null, array);
        assertEquals(getControlObject().subBean[0], array[0]);
    }

    // @XmlPath("rejected") ArrayList<TestBean> rejected;
    public void testSetList() throws JAXBException {
        TestBean o = new TestBean();
        o.description = CHANGED_VALUE;

        ArrayList<TestBean> list = new ArrayList<TestBean>();
        list.add(o);

        eCtx.setValueByXPath(getControlObject(), "rejected", null, list);
        assertEquals(getControlObject().rejected.get(0), o);

        eCtx.setValueByXPath(getControlObject(), "sub-bean[2]/sub-bean[1]/rejected", null, list);
        assertEquals(getControlObject().subBean[1].subBean[0].rejected.get(0), o);
    }

    // @XmlPath("info/coords[1]/text()") String lat;
    // @XmlPath("info/coords[2]/text()") String lon;
    public void testSetPrimitivePositional() throws JAXBException {
        eCtx.setValueByXPath(getControlObject(), "info/coords[1]/text()", null, CHANGED_VALUE);
        assertEquals(getControlObject().lat, CHANGED_VALUE);
        eCtx.setValueByXPath(getControlObject(), "info/coords[2]/text()", null, CHANGED_VALUE);
        assertEquals(getControlObject().lon, CHANGED_VALUE);

        eCtx.setValueByXPath(getControlObject(), "sub-bean[2]/sub-bean[1]/info/coords[1]/text()", null, CHANGED_VALUE);
        assertEquals(getControlObject().subBean[1].subBean[0].lat, CHANGED_VALUE);
        eCtx.setValueByXPath(getControlObject(), "sub-bean[2]/sub-bean[1]/info/coords[2]/text()", null, CHANGED_VALUE);
        assertEquals(getControlObject().subBean[1].subBean[0].lon, CHANGED_VALUE);
    }

}
