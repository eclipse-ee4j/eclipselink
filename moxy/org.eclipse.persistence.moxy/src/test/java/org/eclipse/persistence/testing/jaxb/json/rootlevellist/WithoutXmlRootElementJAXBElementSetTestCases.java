/*
 * Copyright (c) 2012, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

// Contributors:
//     Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Collection;

import jakarta.xml.bind.JAXBElement;
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
