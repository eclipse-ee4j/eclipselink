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
 *     Blaise Doughan - 2.4 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;
public class WithoutXmlRootElementTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/WithoutXmlRootElement.json";
    private static final String CONTROL_JSON_FORMATTED = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/WithoutXmlRootElementFormatted.json";

    public WithoutXmlRootElementTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {WithoutXmlRootElementRoot.class});
        setControlJSON(CONTROL_JSON);
        jsonUnmarshaller.setProperty(UnmarshallerProperties.JSON_INCLUDE_ROOT, Boolean.FALSE);
    }

    @Override
    public Class getUnmarshalClass(){
    	return WithoutXmlRootElementRoot.class;
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

    @Override
	public Object getReadControlObject() {
    	JAXBElement elem = new JAXBElement(new QName(""),WithoutXmlRootElementRoot.class, getControlObject() );
    	
    	return elem;
    }
    
    public void testUnmarshal() throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
        List<WithoutXmlRootElementRoot>  test = (List<WithoutXmlRootElementRoot>) jsonUnmarshaller.unmarshal(new StreamSource(inputStream), WithoutXmlRootElementRoot.class).getValue();
        inputStream.close();
        List<WithoutXmlRootElementRoot> control = getControlObject();
        for(int x=0; x<control.size(); x++) {
            assertEquals(control.get(x), test.get(x));
        }
    }

    public void testUnmarshalEmptyList() throws Exception {
        List<WithoutXmlRootElementRoot>  test = (List<WithoutXmlRootElementRoot>) jsonUnmarshaller.unmarshal(new StreamSource(new StringReader("[]")), WithoutXmlRootElementRoot.class).getValue();
        assertEquals(0, test.size());
    }

    protected boolean shouldRemoveWhitespaceFromControlDocJSON(){
		return false;
	}
    
    public String getWriteControlJSONFormatted(){
    	return CONTROL_JSON_FORMATTED;
    }
}