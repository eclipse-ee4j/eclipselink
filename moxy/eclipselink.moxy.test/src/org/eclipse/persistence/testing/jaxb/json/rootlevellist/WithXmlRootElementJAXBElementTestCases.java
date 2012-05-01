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
 *     Denise Smith - 2.4 - April 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.rootlevellist;

import java.io.StringReader;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.testing.jaxb.json.JSONMarshalUnmarshalTestCases;

public class WithXmlRootElementJAXBElementTestCases extends JSONMarshalUnmarshalTestCases {

    private static final String CONTROL_JSON = "org/eclipse/persistence/testing/jaxb/json/rootlevellist/JAXBElement.json";
    
    public WithXmlRootElementJAXBElementTestCases(String name) throws Exception {
        super(name);
        setClasses(new Class[] {WithXmlRootElementRoot.class});
        setControlJSON(CONTROL_JSON);
    }
  
    @Override
    protected Collection<JAXBElement<WithXmlRootElementRoot>> getControlObject() {
        Collection<JAXBElement<WithXmlRootElementRoot>> list = new LinkedHashSet<JAXBElement<WithXmlRootElementRoot>>(2);

        WithXmlRootElementRoot foo = new WithXmlRootElementRoot();
        foo.setName("FOO");
        JAXBElement<WithXmlRootElementRoot> jbe1 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest1"), WithXmlRootElementRoot.class, foo);
        list.add(jbe1);

        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        JAXBElement<WithXmlRootElementRoot> jbe2 = new JAXBElement<WithXmlRootElementRoot>(new QName("roottest2"), WithXmlRootElementRoot.class, bar);

        list.add(jbe2);

        return list;
    }
    
    @Override
    public Class getUnmarshalClass(){
    	return WithXmlRootElementRoot.class;
    }
    
    
    @Override
	public Object getReadControlObject() {   
         JAXBElement jbe = new JAXBElement(new QName(""), WithXmlRootElementRoot.class, getControlObject());
         return jbe;
    }


    public void testUnmarshalEmptyList() throws Exception {    	
        List<WithXmlRootElementRoot>  test = (List<WithXmlRootElementRoot>) jsonUnmarshaller.unmarshal(new StreamSource(new StringReader("[]")), WithXmlRootElementRoot.class).getValue();
        assertEquals(0, test.size());
    }

}