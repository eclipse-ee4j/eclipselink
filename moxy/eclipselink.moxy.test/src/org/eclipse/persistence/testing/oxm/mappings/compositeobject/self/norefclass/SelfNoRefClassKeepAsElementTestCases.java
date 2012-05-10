/*******************************************************************************
 * Copyright (c) 1998, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the 
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0 
 * which accompanies this distribution. 
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at 
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - May 8/2009 
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass;

import java.util.Calendar;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SelfNoRefClassKeepAsElementTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/norefclass/SelfNoRefKeepAsElement.xml";
    
    public SelfNoRefClassKeepAsElementTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new CompositeObjectSelfNoRefClassProject();
        XMLCompositeObjectMapping mapping = ((XMLCompositeObjectMapping)p.getDescriptor(Root.class).getMappingForAttributeName("theObject"));
        mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
        setProject(p);
    }   

    protected Object getControlObject() {
    	Root theRoot = new Root();
    	try{
	    	DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
	    	factory.setNamespaceAware(true);
	    	DocumentBuilder builder = factory.newDocumentBuilder();
	    
	    	Document doc = builder.newDocument();
	    	Element rootElem = doc.createElement("root");
	    	doc.appendChild(rootElem);
	    	
	    	Element addressElement = doc.createElement("address");
	    	Element streetElement = doc.createElement("street");
	    	streetElement.setTextContent("myStreet");
	    	addressElement.appendChild(streetElement);
	    		    	
	    	rootElem.appendChild(addressElement);
	    		      
	        theRoot.setTheObject(rootElem);
	    	
    	}catch(Exception e){
    		fail(e.getMessage());
    	}
    	return theRoot;
    }

}
