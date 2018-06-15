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
//  - rbarkhouse - 27 November 2012 - 2.4 - Initial implementation
package org.eclipse.persistence.testing.jaxb.jaxbcontext;

import java.net.URL;
import java.util.ArrayList;

import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.eclipse.persistence.jaxb.JAXBContext;
import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.oxm.annotations.XmlPath;

public class CreateByXPathTests extends junit.framework.TestCase {

    private JAXBContext eCtx;
    private TestBean controlObject;

    @Override
    public String getName() {
        return "JAXB Context createByXPath Tests: " + super.getName();
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

    // @XmlPath("companion") TestBean companion;
    public void testCreateComposite() throws JAXBException {
        TestBean o;

        o = eCtx.createByXPath(getControlObject(), "companion", null, TestBean.class);
        assertNotNull(o);

        o = eCtx.createByXPath(getControlObject(), "sub-bean[1]/companion", null, TestBean.class);
        assertNotNull(o);

        o = eCtx.createByXPath(getControlObject(), "sub-bean/companion", null, TestBean.class);
        assertNotNull(o);
    }

    // @XmlPath("sub-bean") TestBean[] subBean;
    public void testCreateArrayMember() throws JAXBException {
        TestBean o;

        o = eCtx.createByXPath(getControlObject(), "sub-bean", null, TestBean.class);
        assertNotNull(o);

        o = eCtx.createByXPath(getControlObject(), "sub-bean[1]/sub-bean", null, TestBean.class);
        assertNotNull(o);

        o = eCtx.createByXPath(getControlObject(), "sub-bean[1]/sub-bean[1]", null, TestBean.class);
        assertNotNull(o);

        o = eCtx.createByXPath(getControlObject(), "sub-bean/sub-bean", null, TestBean.class);
        assertNotNull(o);
    }

    // @XmlPath("rejected") ArrayList<TestBean> rejected;
    public void testCreateListMember() throws JAXBException {
        TestBean o;

        o = eCtx.createByXPath(getControlObject(), "rejected", null, TestBean.class);
        assertNotNull(o);

        o = eCtx.createByXPath(getControlObject(), "sub-bean[2]/sub-bean[1]/rejected", null, TestBean.class);
        assertNotNull(o);

        o = eCtx.createByXPath(getControlObject(), "sub-bean/sub-bean/rejected", null, TestBean.class);
        assertNotNull(o);
    }

}
