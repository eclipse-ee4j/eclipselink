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
 *     Oracle - initial API and implementation from Oracle TopLink
 ******************************************************************************/  
package org.eclipse.persistence.testing.oxm.schemareference;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.persistence.oxm.schema.*;
import java.io.InputStream;
import org.w3c.dom.Document;

public class SchemaReferenceErrorHandlerTests extends org.eclipse.persistence.testing.oxm.XMLTestCase {
  private XMLSchemaReference schemaRef;
	private DocumentBuilder parser;
	
  static boolean testIgnorePassed;
  public SchemaReferenceErrorHandlerTests(String name) {
    super(name);
  }
  public void setUp() throws Exception {
    java.net.URL schemaUrl = ClassLoader.getSystemClassLoader().getResource("org/eclipse/persistence/testing/oxm/schemareference/employee.xsd");
    schemaRef = new XMLSchemaURLReference(schemaUrl);
		DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
		builderFactory.setIgnoringElementContentWhitespace(true);
		parser = builderFactory.newDocumentBuilder();
  }
  //Test our ability to use a custom error handler to ignore a simple error
  public void testIgnoreOneError() throws Exception {
    InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/schemareference/employee_warn.xml");
    Document doc = parser.parse(stream);
    boolean isValid = schemaRef.isValid(doc, new CustomErrorHandler());
    testIgnorePassed = isValid;
    assertTrue("Failed to ignore validation errors", isValid);
  }
  //Test our ability to ignore some errors but not others
  public void testIgnoreSomeErrors() throws Exception {
    InputStream stream = ClassLoader.getSystemResourceAsStream("org/eclipse/persistence/testing/oxm/schemareference/employee_warn2.xml");
    Document doc = parser.parse(stream);
    CustomErrorHandler handler = new CustomErrorHandler();
    boolean isValid = schemaRef.isValid(doc, handler);
    testIgnorePassed = handler.ignoredError();
    assertTrue("Was unable to ignore some errors and fail on others", 
((!isValid) && testIgnorePassed));
  }
}    


