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
//     Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class WithXmlRootElementJAXBElementTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/JAXBElement.json";

    public WithXmlRootElementJAXBElementTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {WithXmlRootElementRoot.class});
        setControlJSON(CONTROL_JSON);
    }

    @Override
    protected Collection<JAXBElement<WithXmlRootElementRoot>> getControlObject() {
        Collection<JAXBElement<WithXmlRootElementRoot>> list = new ArrayList<JAXBElement<WithXmlRootElementRoot>>(2);

        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        JAXBElement<WithXmlRootElementRoot> jbe1 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest1"), WithXmlRootElementRoot.class, foo);
        list.add(jbe1);

        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        JAXBElement<WithXmlRootElementRoot> jbe2 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest2"), WithXmlRootElementRoot.class, bar);

        list.add(jbe2);

        return list;
    }

    @Override
    public Collection<JAXBElement<WithXmlRootElementRoot>> getWriteControlObject() {
        Collection<JAXBElement<WithXmlRootElementRoot>> list = new LinkedHashSet<JAXBElement<WithXmlRootElementRoot>>(2);

        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        JAXBElement<WithXmlRootElementRoot> jbe1 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest1"), WithXmlRootElementRoot.class, foo);
        list.add(jbe1);

        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        JAXBElement<WithXmlRootElementRoot> jbe2 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest2"), WithXmlRootElementRoot.class, bar);

        list.add(jbe2);

        return list;
    }

    @Override
    public Class getUnmarshalClass(){
        return WithXmlRootElementRoot.class;
    }


    @Override
    public Object getReadControlObject() {
         JAXBElement jbe = new JAXBElement(new QName(""), WithXmlRootElementRoot.class, getControlObject());
         return jbe;
    }


    public void testUnmarshalEmptyList() throws Exception {
        List<WithXmlRootElementRoot>  test = (List<WithXmlRootElementRoot>) jsonUnmarshaller.unmarshal(new StreamSource(new StringReader("[]")), WithXmlRootElementRoot.class).getValue();
        assertEquals(0, test.size());
    }

}
