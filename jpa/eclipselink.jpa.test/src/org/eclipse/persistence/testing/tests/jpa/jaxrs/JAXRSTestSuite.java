/*******************************************************************************
 * Copyright (c) 2011 Oracle. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Praba Vijayaratnam - 2.3 - initial implementation
 ******************************************************************************/
package org.eclipse.persistence.testing.tests.jpa.jaxrs;

import org.eclipse.persistence.sessions.DatabaseSession;
import org.eclipse.persistence.testing.framework.junit.JUnitTestCase;
import org.eclipse.persistence.testing.models.jpa.jaxrs.JAXRSPopulator;
import org.eclipse.persistence.testing.models.jpa.jaxrs.JAXRSTableCreator;

import junit.framework.Test;
import junit.framework.TestSuite;

public class JAXRSTestSuite extends TestSuite {

	public static Test suite() {
		TestSuite suite = new TestSuite("JAX-RS Test Suite");
		suite.addTestSuite(org.eclipse.persistence.testing.tests.jpa.jaxrs.DefaultJAXBContextTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.tests.jpa.jaxrs.ContextResolverTestCases.class);
		suite.addTestSuite(org.eclipse.persistence.testing.tests.jpa.jaxrs.MessageBodyReaderWriterTestCases.class);
		return suite;
	}

}
