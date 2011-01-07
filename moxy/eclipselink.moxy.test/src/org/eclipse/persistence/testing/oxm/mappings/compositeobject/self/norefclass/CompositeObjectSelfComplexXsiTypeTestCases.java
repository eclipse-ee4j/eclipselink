/*******************************************************************************
 * Copyright (c) 1998, 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Denise Smith - September 21 /2009
 ******************************************************************************/
package org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass;

import org.eclipse.persistence.oxm.XMLRoot;
import org.eclipse.persistence.oxm.mappings.UnmarshalKeepAsElementPolicy;
import org.eclipse.persistence.oxm.mappings.XMLCompositeObjectMapping;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;

public class CompositeObjectSelfComplexXsiTypeTestCases extends XMLMappingTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/oxm/mappings/compositeobject/self/norefclass/SelfNoRefComplexXsiType.xml";
    
    public CompositeObjectSelfComplexXsiTypeTestCases(String name) throws Exception {
        super(name);
        setControlDocument(XML_RESOURCE);
        Project p = new CompositeObjectSelfNoRefClassNSProject();
        XMLCompositeObjectMapping mapping = ((XMLCompositeObjectMapping)p.getDescriptor(Root.class).getMappingForAttributeName("theObject"));
       
        mapping.setKeepAsElementPolicy(UnmarshalKeepAsElementPolicy.KEEP_UNKNOWN_AS_ELEMENT);
        setProject(p);
    }
    
    public Object getWriteControlObject() {    
     	Root theRoot = new Root();
    	
    	Address address = new Address();
    	address.setStreet("myStreet");
    	theRoot.setTheObject(address);
    	
    	return theRoot;
    }

    
    protected Object getControlObject() {
    	XMLRoot xmlRoot = new XMLRoot();
    	xmlRoot.setLocalName("root");
    	xmlRoot.setNamespaceURI("namespace1");
    	Address address = new Address();
    	address.setStreet("myStreet");
    	xmlRoot.setObject(address);
    	    
    	return xmlRoot;
    		
    }
    
    public static void main(String[] args) {
        junit.textui.TestRunner.main(new String[] { "-c", "org.eclipse.persistence.testing.oxm.mappings.compositeobject.self.norefclass.CompositeObjectSelfComplexXsiTypeTestCases" });
    }

}