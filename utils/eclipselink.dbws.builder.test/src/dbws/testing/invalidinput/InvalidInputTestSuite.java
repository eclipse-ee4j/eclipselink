/*******************************************************************************
 * Copyright (c) 2012 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     David McCann - April 12, 2012 - 2.4 - Initial implementation
 ******************************************************************************/
package dbws.testing.invalidinput;

//javase imports
import static org.junit.Assert.assertTrue;

import java.util.List;

import javax.wsdl.WSDLException;

import org.junit.BeforeClass;
import org.junit.Test;

import dbws.testing.DBWSTestSuite;

/**
 * Tests exception handling for an invalid input.
 *
 */
public class InvalidInputTestSuite extends DBWSTestSuite {
	private static String WARNING_MSG_1 = "WARNING: No tables were found matching the following:  [%.TABLETYPE_INVALID]";
	private static String WARNING_MSG_2 = "WARNING: No procedures were found matching the following:  [CREATE_SOMETHING]";

	@BeforeClass
    public static void setUp() throws WSDLException {
        DBWS_BUILDER_XML_USERNAME =
            "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
            "<dbws-builder xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\">" +
              "<properties>" +
                  "<property name=\"projectName\">InvalidInput</property>" +
                  "<property name=\"logLevel\">off</property>" +
                  "<property name=\"username\">";
          DBWS_BUILDER_XML_PASSWORD =
                  "</property><property name=\"password\">";
          DBWS_BUILDER_XML_URL =
                  "</property><property name=\"url\">";
          DBWS_BUILDER_XML_DRIVER =
                  "</property><property name=\"driver\">";
          DBWS_BUILDER_XML_PLATFORM =
                  "</property><property name=\"platformClassname\">";
          DBWS_BUILDER_XML_MAIN =
                  "</property>" +
              "</properties>" +
              "<procedure " +
		          "name=\"Blah\" " +
		          "procedurePattern=\"CREATE_SOMETHING\" " +
		      "/>" +
              "<table " +
	              "schemaPattern=\"%\" " +
	              "tableNamePattern=\"TABLETYPE_INVALID\" " +
	          "/>" +
            "</dbws-builder>";
          builder = null;
          DBWSTestSuite.setUp(".", true);
    }

    @Test
    public void testWarningLogs() {
		assertTrue("No WARNINGs logged", dbwsLogger.hasWarnings());
		List<String> warnings = dbwsLogger.getWarnings();
		assertTrue("Expected [2] WARNING, but was [" + warnings.size() + "]", warnings.size() == 2);
		assertTrue("Expected WARNING message '" + WARNING_MSG_1 + "', but was '" + warnings.get(0) + "'", WARNING_MSG_1.equals(warnings.get(0)));   
		assertTrue("Expected WARNING message '" + WARNING_MSG_2 + "', but was '" + warnings.get(1) + "'", WARNING_MSG_2.equals(warnings.get(1)));   
    }
}