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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json;

import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.bind.JAXBElement;
import javax.xml.transform.stream.StreamSource;

import org.eclipse.persistence.jaxb.JAXBUnmarshaller;

public abstract class JSONWithUnmarshalToClassTestCases extends JSONTestCases{

	public JSONWithUnmarshalToClassTestCases(String name) throws Exception {
		super(name);
	}
	
	public void testJSONToObjectFromInputSourceWithClass(Class unmarshalClass) throws Exception {    	

	    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
	    StreamSource ss = new StreamSource(inputStream);
	    JAXBElement testObject = ((JAXBUnmarshaller)jsonUnmarshaller).unmarshal(ss, unmarshalClass);
	    inputStream.close();
	    jsonToObjectTest(testObject);
    }
  
    public void testJSONToObjectFromReaderWithClass(Class unmarshalClass) throws Exception {    		        
	    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
	    InputStreamReader reader = new InputStreamReader(inputStream);
	    StreamSource ss = new StreamSource(reader);
	    JAXBElement testObject = ((JAXBUnmarshaller)jsonUnmarshaller).unmarshal(ss, unmarshalClass);
	    inputStream.close();

	    jsonToObjectTest(testObject);
	}

}
