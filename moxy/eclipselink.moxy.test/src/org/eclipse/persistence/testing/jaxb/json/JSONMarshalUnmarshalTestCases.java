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
import java.io.Reader;
import java.net.URL;

import org.xml.sax.InputSource;

public abstract class JSONMarshalUnmarshalTestCases extends JSONTestCases{

	public JSONMarshalUnmarshalTestCases(String name) {
		super(name);
	}
	

	protected void compareStrings(String testName, String testString) {
	    log(testName);
		log("Expected (With All Whitespace Removed):");
		String expectedString = loadFileToString(getWriteControlJSON()).replaceAll("[ \b\t\n\r ]", "");
		log(expectedString);
		log("\nActual (With All Whitespace Removed):");
		testString = testString.replaceAll("[ \b\t\n\r]", "");
		log(testString);
		assertEquals(expectedString, testString);
	}
		 	
	public void testJSONUnmarshalFromInputStream() throws Exception {
	    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
		Object testObject = jsonUnmarshaller.unmarshal(inputStream);
		inputStream.close();
		jsonToObjectTest(testObject);
	}

	public void testJSONUnmarshalFromInputSource() throws Exception {	
         InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
		 InputSource inputSource = new InputSource(inputStream);
		 Object testObject = jsonUnmarshaller.unmarshal(inputSource);
		 inputStream.close();
		 jsonToObjectTest(testObject);
    }

    public void testJSONUnmarshalFromReader() throws Exception {
	    InputStream inputStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(controlJSONLocation);
	    Reader reader = new InputStreamReader(inputStream);
		Object testObject = jsonUnmarshaller.unmarshal(reader);
		reader.close();
		inputStream.close();
		jsonToObjectTest(testObject);
	}

	public void testJSONUnmarshalFromURL() throws Exception {
        URL url = getJSONURL();
        Object testObject = jsonUnmarshaller.unmarshal(url);
        jsonToObjectTest(testObject);
    }

	protected URL getJSONURL() {	    	
	    return Thread.currentThread().getContextClassLoader().getResource(controlJSONLocation);
	}
}
