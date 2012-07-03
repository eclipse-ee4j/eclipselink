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

import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.UnmarshallerProperties;
import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;
public class WithoutXmlRootElementSetTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/WithoutXmlRootElement.json";

    public WithoutXmlRootElementSetTestCases(String name) throws Exception {
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
    protected Set<WithoutXmlRootElementRoot> getControlObject() {
    	Set<WithoutXmlRootElementRoot> set = new LinkedHashSet<WithoutXmlRootElementRoot>();

        WithoutXmlRootElementRoot foo = new WithoutXmlRootElementRoot();
        foo.setName("FOO");
        set.add(foo);

        WithoutXmlRootElementRoot bar = new WithoutXmlRootElementRoot();
        bar.setName("BAR");
        set.add(bar);

        return set;
    }

    @Override
	public Object getReadControlObject() {
    	JAXBElement elem = new JAXBElement(new QName(""),WithoutXmlRootElementRoot.class, new ArrayList<WithoutXmlRootElementRoot>(getControlObject()) );
    	
    	return elem;
    }
    
    public void testUnmarshal() throws Exception {
        InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
        Collection<WithoutXmlRootElementRoot>  test = (Collection<WithoutXmlRootElementRoot>) jsonUnmarshaller.unmarshal(new StreamSource(inputStream), WithoutXmlRootElementRoot.class).getValue();
        Iterator testIter = test.iterator();
        inputStream.close();
        Collection<WithoutXmlRootElementRoot> control = getControlObject();
        Iterator<WithoutXmlRootElementRoot> controlIter = control.iterator();
        assertTrue(test.size() == control.size());
        while(controlIter.hasNext()){
        	assertEquals(controlIter.next(), testIter.next());
        }        
    }

    public void testUnmarshalEmptyList() throws Exception {
        Collection<WithoutXmlRootElementRoot>  test = (Collection<WithoutXmlRootElementRoot>) jsonUnmarshaller.unmarshal(new StreamSource(new StringReader("[]")), WithoutXmlRootElementRoot.class).getValue();
        assertEquals(0, test.size());
    }
}