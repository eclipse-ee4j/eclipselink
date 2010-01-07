/*******************************************************************************
 * Copyright (c) 1998, 2010 Oracle. All rights reserved.
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

import org.eclipse.persistence.oxm.XMLConstants;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Attr;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class SelfNoRefClassKeepAsElementNSTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/norefclass/SelfNoRefKeepAsElementNS.xml";
    
    public SelfNoRefClassKeepAsElementNSTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new CompositeObjectSelfNoRefClassNSProject();
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
	    	Element rootElem = doc.createElementNS("namespace1", "ns0:root");
	    	rootElem.setAttributeNS(XMLConstants.XMLNS_URL, "xmlns:ns0", "namespace1");	    	
	    	doc.appendChild(rootElem);
	    	
	    	Element addressElement = doc.createElementNS("namespace1", "ns0:address");
	    	Element streetElement = doc.createElementNS("namespace1", "ns0:street");
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
