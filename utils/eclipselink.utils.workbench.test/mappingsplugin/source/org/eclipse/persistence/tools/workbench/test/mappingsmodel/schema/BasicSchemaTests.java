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
 *     Oracle - initial API and implementation from Oracle TopLink
******************************************************************************/
package org.eclipse.persistence.tools.workbench.test.mappingsmodel.schema;

import junit.framework.Test;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.project.xml.MWOXProject;
import org.eclipse.persistence.tools.workbench.mappingsmodel.schema.MWXmlSchema;
import org.eclipse.persistence.tools.workbench.test.mappingsmodel.MappingsModelTestTools;

public class BasicSchemaTests 
	extends SchemaTests
{
	
	public static Test suite() {
		return new TestSuite(BasicSchemaTests.class);
	}
	
	public BasicSchemaTests(String name) {
		super(name);
	}
	
	public void testLoadSchemaFromBadUrl() {
		MWXmlSchema schema = MWXmlSchema.createFromUrl(new MWOXProject("Test Xml Project", MappingsModelTestTools.buildSPIManager()).getSchemaRepository(), "BAD_URL", "file:/foobar.xsd");
		
		try {
			schema.reload();
			fail("XSDException not thrown.");
		}
		catch (RuntimeException e) {}//expected
		catch (Exception ex) {
			fail("XSDException not thrown.  OK if using XDK 9.0.4.");
		}
	}
	
	public void testLoadPoorlyFormedSchema() {
		try {
			this.loadSchema("PoorlyFormedSchema");
			assertTrue("XSDException not thrown.", false);
		}
		catch (RuntimeException xsde) {/* expected */}
		catch (Exception e) {
			assertTrue("XSDException not thrown.  OK if using XDK 9.0.4.", false);
		}
	}
	
	public void testLoadInvalidSchema() {
		try {
			this.loadSchema("InvalidSchema");
				// schema should be invalid due to undefined "foo" root element.
			assertTrue("XSDException not thrown.", false);
		}
		catch (RuntimeException xsde) {/* expected */}
		catch (Exception e) {
			assertTrue("XSDException not thrown.  OK if using XDK 9.0.4.", false);
		}
	}
	
//	public void testLoadNonSpecSchema() {
//		try {
//			this.loadSchema("NonSpecSchema");
//				// schema should be against spec due to included schema with different target namespace
//			assertTrue("XSDException not thrown.", false);
//		}
//		catch (RuntimeException xsde) {/* expected */}
//		catch (Exception e) {
//			assertTrue("XSDException not thrown.  OK if using XDK 9.0.4.", false);
//		}
//	}
}
