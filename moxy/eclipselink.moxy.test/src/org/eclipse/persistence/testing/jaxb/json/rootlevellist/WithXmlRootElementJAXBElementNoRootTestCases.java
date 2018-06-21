/*
 * Copyright (c) 2012, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Denise Smith - 2.4 - April 2012
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.UUID;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class WithXmlRootElementJAXBElementNoRootTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/WithoutXmlRootElement.json";

    public WithXmlRootElementJAXBElementNoRootTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {WithXmlRootElementRoot.class});
        setControlJSON(CONTROL_JSON);
        jsonMarshaller.setProperty(MarshallerProperties.JSON_INCLUDE_ROOT, false);
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, false);
    }

    public Class getUnmarshalClass(){
        return WithXmlRootElementRoot.class;
    }

    @Override
    protected Collection<JAXBElement<WithXmlRootElementRoot>> getControlObject() {
        Collection<JAXBElement<WithXmlRootElementRoot>> list = new LinkedHashSet<JAXBElement<WithXmlRootElementRoot>>(2);

        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        foo.setUuid(UUID.fromString("8ae03765-ee01-4a81-a0de-a2497a10739f"));
        JAXBElement<WithXmlRootElementRoot> jbe1 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest1"), WithXmlRootElementRoot.class, foo);
        list.add(jbe1);

        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        JAXBElement<WithXmlRootElementRoot> jbe2 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest2"), WithXmlRootElementRoot.class, bar);

        list.add(jbe2);

        return list;
    }

    @Override
    public Object getReadControlObject() {
        List<WithXmlRootElementRoot> list = new ArrayList<WithXmlRootElementRoot>(2);

        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        foo.setUuid(UUID.fromString("8ae03765-ee01-4a81-a0de-a2497a10739f"));
        list.add(foo);

        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        list.add(bar);

        JAXBElement elem = new JAXBElement(new QName(""),WithXmlRootElementRoot.class, list );
        return elem;
    }

}
