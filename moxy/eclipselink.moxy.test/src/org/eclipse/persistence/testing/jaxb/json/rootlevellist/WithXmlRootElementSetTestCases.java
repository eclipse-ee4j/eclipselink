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

import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class WithXmlRootElementSetTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/WithXmlRootElement.json";

    public WithXmlRootElementSetTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {WithXmlRootElementRoot.class});
        setControlJSON(CONTROL_JSON);
    }

    @Override
    protected Set<WithXmlRootElementRoot> getControlObject() {
        Set<WithXmlRootElementRoot> set = new LinkedHashSet<WithXmlRootElementRoot>();

        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        set.add(foo);

        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        set.add(bar);

        return set;
    }

    public void testUnmarshalEmptyList() throws Exception {
        Collection<WithXmlRootElementRoot>  test = (Collection<WithXmlRootElementRoot>) jsonUnmarshaller.unmarshal(new StreamSource(new StringReader("[]")), WithXmlRootElementRoot.class).getValue();
        
        assertEquals(0, test.size());
    }

}