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
 *     Denise Smith - September 2011 - 2.4 
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.norootelement;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;
   
public class IncludeRootTrueWithXMLRootElementTestCases extends IncludeRootFalseWithXMLRootElementTestCases{
	
	protected final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/norootelement/addressWithRoot.json";
	
	public IncludeRootTrueWithXMLRootElementTestCases(String name) throws Exception {
		super(name);
		setControlJSON(JSON_RESOURCE);
		setClasses(new Class[]{AddressWithRootElement.class});		
	}
	
	public void setUp() throws Exception{
		super.setUp();
	    jsonMarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT, true);	    	   
	    jsonUnmarshaller.setProperty(org.eclipse.persistence.jaxb.JAXBContext.JSON_INCLUDE_ROOT, true);
  	}
	
	public Object getReadControlObject() {	
		QName name = new QName("addressWithRootElement");
		JAXBElement jbe = new JAXBElement<AddressWithRootElement>(name, AddressWithRootElement.class, (AddressWithRootElement)getControlObject());
		return jbe;
	}
}
