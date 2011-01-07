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
package org.eclipse.persistence.tools.workbench.test.mappingsplugin;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

import org.eclipse.persistence.tools.workbench.mappingsmodel.ProblemConstants;
import org.eclipse.persistence.tools.workbench.mappingsplugin.ProblemsBundle;
import org.eclipse.persistence.tools.workbench.utility.ClassTools;

public class ProblemsBundleTests extends TestCase {

	public static Test suite() {
		return new TestSuite(ProblemsBundleTests.class);
	}

	public ProblemsBundleTests(String name) {
		super(name);
	}

	/**
	 * make sure all the entries in the bundle have corresponding constants
	 */
	public void testBundle() {
		Map problemConstants = this.buildProblemConstants();

		Collection missingProblemConstants = new ArrayList();
		ProblemsBundle bundle = new ProblemsBundle();
		Object[][] entries = bundle.getContents();
		for (int i = 0; i < entries.length; i++) {
			Object[] entry = entries[i];
			String problemNumber =(String) entry[0];
			assertEquals(problemNumber, 4, problemNumber.length());
			if (problemConstants.get(problemNumber) == null) {
				missingProblemConstants.add(problemNumber);
			}
		}
		assertTrue("The following entries in the ProblemsBundle do not have corresponding constants in ProblemConstants: "
				+ missingProblemConstants, missingProblemConstants.isEmpty());
	}

	/**
	 * return a map: constant value => constant name
	 * e.g. "0100" => "PROJECT_CACHES_QUERY_STATEMENTS_WITHOUT_BINDING_PARAMETERS"
	 */
	private Map buildProblemConstants() {
		Field[] fields = ProblemConstants.class.getDeclaredFields();
		Map problemConstants = new HashMap(fields.length);
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			int mod = field.getModifiers();
			String fieldName = field.getName();
			assertTrue(fieldName, Modifier.isPublic(mod));
			assertTrue(fieldName, Modifier.isStatic(mod));
			assertTrue(fieldName, Modifier.isFinal(mod));
			assertEquals(fieldName, String.class, field.getType());
			String fieldValue = (String) ClassTools.getStaticFieldValue(ProblemConstants.class, fieldName);
			assertEquals(fieldName, 4, fieldValue.length());
			// check for duplicate numbers
			assertNull(fieldName, problemConstants.put(fieldValue, fieldName));
		}
		return problemConstants;
	}


	/**
	 * make sure all the constants have corresponding entries in the bundle
	 */
	public void testConstants() {
		Map problemMessages = this.buildProblemMessages();

		Collection missingProblemMessages = new ArrayList();
		Field[] fields = ProblemConstants.class.getDeclaredFields();
		for (int i = 0; i < fields.length; i++) {
			Field field = fields[i];
			String fieldName = field.getName();
			String problemNumber = (String) ClassTools.getStaticFieldValue(ProblemConstants.class, fieldName);
			if (problemMessages.get(problemNumber) == null) {
				missingProblemMessages.add(problemNumber);
			}
		}
		assertTrue("The following constants in ProblemConstants do not have corresponding entries in the ProblemsBundle: "
				+ missingProblemMessages, missingProblemMessages.isEmpty());
	}

	/**
	 * return a map: message number => message text
	 * e.g. "0100" => "The project caches all statments by default for queries, but does not bind all parameters."
	 */
	private Map buildProblemMessages() {
		Map problemMessages = new HashMap();
		ProblemsBundle bundle = new ProblemsBundle();
		Object[][] entries = bundle.getContents();
		for (int i = 0; i < entries.length; i++) {
			Object[] entry = entries[i];
			String problemNumber = (String) entry[0];
			Object problemMessage = entry[1];
			// check for duplicate numbers
			assertNull(problemNumber, problemMessages.put(problemNumber, problemMessage));
		}
		return problemMessages;
	}

}
