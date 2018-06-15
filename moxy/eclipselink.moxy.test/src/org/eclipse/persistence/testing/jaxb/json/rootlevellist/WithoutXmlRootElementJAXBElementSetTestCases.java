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

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Collection;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class WithoutXmlRootElementJAXBElementSetTestCases extends WithoutXmlRootElementJAXBElementTestCases {

    public WithoutXmlRootElementJAXBElementSetTestCases(String name) throws Exception {
        super(name);
    }

    @Override
    public Collection<JAXBElement<WithoutXmlRootElementRoot>> getWriteControlObject() {
        Collection<JAXBElement<WithoutXmlRootElementRoot>> list = new LinkedHashSet<JAXBElement<WithoutXmlRootElementRoot>>(2);

        WithoutXmlRootElementRoot foo = new WithoutXmlRootElementRoot();
        foo.setName("FOO");
        JAXBElement<WithoutXmlRootElementRoot> jbe1 = new JAXBElement<WithoutXmlRootElementRoot>(new QName("roottest1"), WithoutXmlRootElementRoot.class, foo);

        list.add(jbe1);

        WithoutXmlRootElementRoot bar = new WithoutXmlRootElementRoot();
        bar.setName("BAR");
        JAXBElement<WithoutXmlRootElementRoot> jbe2 = new JAXBElement<WithoutXmlRootElementRoot>(new QName("roottest2"), WithoutXmlRootElementRoot.class, bar);

        list.add(jbe2);

        return list;
    }

    @Override
    public Collection<JAXBElement<WithoutXmlRootElementRoot>> getControlObject() {
        Collection<JAXBElement<WithoutXmlRootElementRoot>> list = new ArrayList<JAXBElement<WithoutXmlRootElementRoot>>(2);

        WithoutXmlRootElementRoot foo = new WithoutXmlRootElementRoot();
        foo.setName("FOO");
        JAXBElement<WithoutXmlRootElementRoot> jbe1 = new JAXBElement<WithoutXmlRootElementRoot>(new QName("roottest1"), WithoutXmlRootElementRoot.class, foo);

        list.add(jbe1);

        WithoutXmlRootElementRoot bar = new WithoutXmlRootElementRoot();
        bar.setName("BAR");
        JAXBElement<WithoutXmlRootElementRoot> jbe2 = new JAXBElement<WithoutXmlRootElementRoot>(new QName("roottest2"), WithoutXmlRootElementRoot.class, bar);

        list.add(jbe2);

        return list;
    }

}
