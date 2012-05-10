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
import java.util.List;

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
        list.add(foo);

        WithXmlRootElementRoot bar = new WithXmlRootElementRoot();
        bar.setName("BAR");
        list.add(bar);
        
        JAXBElement elem = new JAXBElement(new QName(""),WithXmlRootElementRoot.class, list );
    	return elem;
    }

}