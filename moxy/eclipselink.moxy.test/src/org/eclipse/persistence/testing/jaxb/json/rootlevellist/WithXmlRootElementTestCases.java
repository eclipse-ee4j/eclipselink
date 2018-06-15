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
//     Blaise Doughan - 2.4 - initial implementation
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class WithXmlRootElementTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/WithXmlRootElement.json";
    private static final String CONTROL_JSON_FORMATTED = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/WithXmlRootElementFormatted.json";

    public WithXmlRootElementTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {WithXmlRootElementRoot.class});
        setControlJSON(CONTROL_JSON);
    }

    @Override
    protected List<WithXmlRootElementRoot> getControlObject() {
        List<WithXmlRootElementRoot> list = new ArrayList<WithXmlRootElementRoot>(2);

        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        foo.setUuid(UUID.fromString("8ae03765-ee01-4a81-a0de-a2497a10739f"));
        list.add(foo);

        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        list.add(bar);

        return list;
    }

    public void testUnmarshalEmptyList() throws Exception {
        List<WithXmlRootElementRoot>  test = (List<WithXmlRootElementRoot>) jsonUnmarshaller.unmarshal(new StreamSource(new StringReader("[]")), WithXmlRootElementRoot.class).getValue();
        assertEquals(0, test.size());
    }

    protected boolean shouldRemoveWhitespaceFromControlDocJSON(){
        return false;
    }

    public String getWriteControlJSONFormatted(){
        return CONTROL_JSON_FORMATTED;
    }
}
