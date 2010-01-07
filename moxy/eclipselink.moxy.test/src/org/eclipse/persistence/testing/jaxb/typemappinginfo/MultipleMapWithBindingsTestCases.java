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
 *     Denise Smith - December 16, 2009
 ******************************************************************************/  
package org.eclipse.persistence.testing.jaxb.typemappinginfo;

import java.io.ByteArrayInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.xml.transform.Source;
import javax.xml.transform.dom.DOMSource;

import org.eclipse.persistence.jaxb.JAXBContextFactory;
import org.w3c.dom.Document;

public class MultipleMapWithBindingsTestCases extends MultipleMapTestCases{

	public MultipleMapWithBindingsTestCases(String name) throws Exception {
		super(name);		
	}
	
    protected Map<String, Object> getProperties() throws Exception{
        String pkg = "";
	        
        HashMap<String, Source> overrides = new HashMap<String, Source>();
        overrides.put(pkg, getXmlSchemaOxm(pkg));
        Map<String, Object> properties = new HashMap<String, Object>();
        properties.put(JAXBContextFactory.ECLIPSELINK_OXM_XML_KEY, overrides);
        return properties;
    }
	    
    private Source getXmlSchemaOxm(String defaultTns) throws Exception {
        String oxm = 
        "<xml-bindings xmlns='http://www.eclipse.org/eclipselink/xsds/persistence/oxm'>" +
	        "<xml-schema namespace='" + defaultTns + "'/>" + 
	        "<java-types></java-types>" + 
        "</xml-bindings>";
	    Document doc = parser.parse(new ByteArrayInputStream(oxm.getBytes()));        
	    return new DOMSource(doc.getDocumentElement());
	}

}
