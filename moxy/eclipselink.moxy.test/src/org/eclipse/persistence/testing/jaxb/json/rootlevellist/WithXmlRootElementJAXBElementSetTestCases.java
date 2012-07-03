/*******************************************************************************
 * Copyright (c) 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4 - April 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

public class WithXmlRootElementJAXBElementSetTestCases extends WithXmlRootElementJAXBElementTestCases {

    public WithXmlRootElementJAXBElementSetTestCases(String name) throws Exception {
        super(name);
    }

    @Override
    protected Collection<JAXBElement<WithXmlRootElementRoot>> getControlObject() {
        Collection<JAXBElement<WithXmlRootElementRoot>> set = new ArrayList<JAXBElement<WithXmlRootElementRoot>>(2);

        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        JAXBElement<WithXmlRootElementRoot> jbe1 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest1"), WithXmlRootElementRoot.class, foo);
        set.add(jbe1);

        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        JAXBElement<WithXmlRootElementRoot> jbe2 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest2"), WithXmlRootElementRoot.class, bar);

        set.add(jbe2);

        return set;
    }

    @Override
    public Collection<JAXBElement<WithXmlRootElementRoot>> getWriteControlObject() {
    	Collection<JAXBElement<WithXmlRootElementRoot>> set = new LinkedHashSet<JAXBElement<WithXmlRootElementRoot>>(2);

        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        JAXBElement<WithXmlRootElementRoot> jbe1 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest1"), WithXmlRootElementRoot.class, foo);
        set.add(jbe1);

        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        JAXBElement<WithXmlRootElementRoot> jbe2 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest2"), WithXmlRootElementRoot.class, bar);

        set.add(jbe2);

        return set;
    }
}