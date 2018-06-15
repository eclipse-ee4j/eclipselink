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
//  - rbarkhouse - 01 November 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.util.Collections;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;

public class GetByXPathTests extends junit.framework.TestCase {

    private JAXBContext eCtx;
    private TestBean controlObject;

    public String getName() {
        return "JAXB Context getByXPath Tests: " + super.getName();
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

    // @XmlPath("info/desc/text()") String description;
    public void testGetDirect() throws JAXBException {
        String o;

        o = eCtx.getValueByXPath(getControlObject(), "info/desc/text()", null, String.class);
        assertEquals(getControlObject().description, o);

        o = eCtx.getValueByXPath(getControlObject(), "companion/info/desc/text()", null, String.class);
        assertEquals(getControlObject().companion.description, o);

        o = eCtx.getValueByXPath(getControlObject(), "sub-bean[1]/info/desc/text()", null, String.class);
        assertEquals(getControlObject().subBean[0].description, o);

        o = eCtx.getValueByXPath(getControlObject(), "info/desc", null, String.class);
        assertNull(o);
    }

    // @XmlPath("companion") TestBean companion;
    public void testGetComposite() throws JAXBException {
        TestBean o;

        o = eCtx.getValueByXPath(getControlObject(), "companion", null, TestBean.class);
        assertEquals(getControlObject().companion, o);

        o = eCtx.getValueByXPath(getControlObject(), "sub-bean[2]/companion", null, TestBean.class);
        assertEquals(getControlObject().subBean[1].companion, o);
    }

    // @XmlPath("info/name/text()") String[] name;
    public void testGetPrimitiveArray() throws JAXBException {
        String o;

        o = eCtx.getValueByXPath(getControlObject(), "info/name[1]/text()", null, String.class);
        assertEquals(getControlObject().name[0], o);

        o = eCtx.getValueByXPath(getControlObject(), "sub-bean[2]/info/name[1]/text()", null, String.class);
        assertEquals(getControlObject().subBean[1].name[0], o);
    }

    // @XmlPath("info/roles/text()") ArrayList<String> roles;
    public void testGetPrimitiveList() throws JAXBException {
        List o;

        o = eCtx.getValueByXPath(getControlObject(), "info/roles/text()", null, List.class);
        assertEquals(getControlObject().roles, o);

        o = eCtx.getValueByXPath(getControlObject(), "sub-bean[2]/info/roles/text()", null, List.class);
        assertEquals(getControlObject().subBean[1].roles, o);
    }

    // @XmlPath("sub-bean") TestBean[] subBean;
    public void testGetArray() throws JAXBException {
        TestBean o;

        o = eCtx.getValueByXPath(getControlObject(), "sub-bean[2]", null, TestBean.class);
        assertEquals(getControlObject().subBean[1], o);

        o = eCtx.getValueByXPath(getControlObject(), "sub-bean[1]/sub-bean[1]", null, TestBean.class);
        assertEquals(getControlObject().subBean[0].subBean[0], o);

        o = eCtx.getValueByXPath(getControlObject(), "sub-bean[3]", null, TestBean.class);
        assertNull(o);
    }

    // @XmlPath("rejected") ArrayList<TestBean> rejected;
    public void testGetList() throws JAXBException {
        List o;

        o = eCtx.getValueByXPath(getControlObject(), "rejected", null, List.class);
        assertEquals(getControlObject().rejected, o);

        o = eCtx.getValueByXPath(getControlObject(), "sub-bean[2]/sub-bean[1]/rejected", null, List.class);
        assertEquals(getControlObject().subBean[1].subBean[0].rejected, o);

        o = eCtx.getValueByXPath(getControlObject(), "sub-bean[1]/rejected", null, List.class);
        assertEquals(Collections.EMPTY_LIST, o);
    }

    // @XmlPath("info/coords[1]/text()") String lat;
    // @XmlPath("info/coords[2]/text()") String lon;
    public void testGetPrimitivePositional() throws JAXBException {
        String o;

        o = eCtx.getValueByXPath(getControlObject(), "info/coords[1]/text()", null, String.class);
        assertEquals(getControlObject().lat, o);
        o = eCtx.getValueByXPath(getControlObject(), "info/coords[2]/text()", null, String.class);
        assertEquals(getControlObject().lon, o);

        o = eCtx.getValueByXPath(getControlObject(), "sub-bean[2]/sub-bean[1]/info/coords[1]/text()", null, String.class);
        assertEquals(getControlObject().subBean[1].subBean[0].lat, o);
        o = eCtx.getValueByXPath(getControlObject(), "sub-bean[2]/sub-bean[1]/info/coords[2]/text()", null, String.class);
        assertEquals(getControlObject().subBean[1].subBean[0].lon, o);

        o = eCtx.getValueByXPath(getControlObject(), "info/coords/text()", null, String.class);
        assertNull(o);
    }

}
