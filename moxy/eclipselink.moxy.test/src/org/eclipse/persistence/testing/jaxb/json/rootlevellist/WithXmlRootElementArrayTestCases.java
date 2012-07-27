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
 *     Blaise Doughan - 2.4.1 - July 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class WithXmlRootElementArrayTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/WithXmlRootElement.json";

    public WithXmlRootElementArrayTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {WithXmlRootElementRoot.class});
        setControlJSON(CONTROL_JSON);
    }

    @Override
    protected WithXmlRootElementRoot[] getControlObject() {
        WithXmlRootElementRoot[] array = new WithXmlRootElementRoot[2];

        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        array[0] = foo;

        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        array[1] = bar;

        return array;
    }

    @Override
    public List<WithXmlRootElementRoot> getReadControlObject() {
        WithXmlRootElementRoot[] array = getControlObject();
        List<WithXmlRootElementRoot> list = new ArrayList<WithXmlRootElementRoot>(array.length);
        for(WithXmlRootElementRoot withXmlRootElementRoot : array) {
            list.add(withXmlRootElementRoot);
        }
        return list;
    }

    public void testUnmarshalEmptyList() throws Exception {
        Collection<WithXmlRootElementRoot>  test = (Collection<WithXmlRootElementRoot>) jsonUnmarshaller.unmarshal(new StreamSource(new StringReader("[]")), WithXmlRootElementRoot.class).getValue();
        assertEquals(0, test.size());
    }

}