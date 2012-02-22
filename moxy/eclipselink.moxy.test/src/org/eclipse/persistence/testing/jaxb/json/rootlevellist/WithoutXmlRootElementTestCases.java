/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.Unmarshaller;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBUnmarshaller;
import org.eclipse.persistence.testing.jaxb.json.JSONTestCases;

public class WithoutXmlRootElementTestCases extends JSONTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/WithoutXmlRootElement.json";

    public WithoutXmlRootElementTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {WithoutXmlRootElementRoot.class});
        setControlJSON(CONTROL_JSON);
    }

    @Override
    protected List<WithoutXmlRootElementRoot> getControlObject() {
        List<WithoutXmlRootElementRoot> list = new ArrayList<WithoutXmlRootElementRoot>(2);

        WithoutXmlRootElementRoot foo = new WithoutXmlRootElementRoot();
        foo.setName("FOO");
        list.add(foo);

        WithoutXmlRootElementRoot bar = new WithoutXmlRootElementRoot();
        bar.setName("BAR");
        list.add(bar);

        return list;
    }

    public void testUnmarshal() throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
        List<WithoutXmlRootElementRoot> test = (List<WithoutXmlRootElementRoot>) jsonUnmarshaller.unmarshal(new StreamSource(inputStream), WithoutXmlRootElementRoot.class).getValue();
        inputStream.close();
        List<WithoutXmlRootElementRoot> control = getControlObject();
        for(int x=0; x<control.size(); x++) {
            assertEquals(control.get(x), test.get(x));
        }
    }

}