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
 *     Denise Smith - August 14/2009
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.jaxbelement.simple;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Map;

import javax.xml.bind.JAXBElement;
import javax.xml.namespace.QName;

import org.eclipse.persistence.testing.jaxb.jaxbelement.JAXBElementTestCases;

public class JAXBElementCharacterTestCases extends JAXBElementTestCases {
    private final static String XML_RESOURCE = "org/eclipse/persistence/testing/jaxb/jaxbelement/simple/character.xml";

	public JAXBElementCharacterTestCases(String name) throws Exception {
		super(name);
		setControlDocument(XML_RESOURCE);	
		setTargetClass(Character.class);		
	}

	public Class[] getClasses(){
    	Class[] classes = new Class[1];
        classes[0] = Character.class;
        return classes;
    }
	
	protected Object getControlObject() {		
		Character character = new Character('s');
		JAXBElement<Character> jbe = new JAXBElement<Character>(new QName("a", "b"),Character.class, character); 			
		return jbe;		
	}
					
	public void testSchemaGen() throws Exception{
		super.testSchemaGen(new ArrayList<InputStream>());
	}

}
