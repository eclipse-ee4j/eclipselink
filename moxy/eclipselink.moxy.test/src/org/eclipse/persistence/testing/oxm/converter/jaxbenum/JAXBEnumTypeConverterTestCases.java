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

package org.eclipse.persistence.testing.oxm.converter.jaxbenum;

import junit.textui.TestRunner;

import org.eclipse.persistence.exceptions.IntegrityException;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.oxm.XMLContext;
import org.eclipse.persistence.sessions.Project;
import org.eclipse.persistence.testing.oxm.OXTestCase;
import org.eclipse.persistence.testing.oxm.converter.typesafeenum.Employee;
import org.eclipse.persistence.testing.oxm.converter.typesafeenum.TypeSafeEnumConverterProject;
import org.eclipse.persistence.testing.oxm.mappings.XMLMappingTestCases;
import org.w3c.dom.Document;

public class JAXBEnumTypeConverterTestCases extends OXTestCase {

	public JAXBEnumTypeConverterTestCases(String name) throws Exception {
		super(name);
	}

	public void testConverterClassNotFound() throws Exception {
		 try{
		     Project proj = new JAXBEnumTypeConverterProject();
		     proj.convertClassNamesToClasses(this.getClass().getClassLoader());
			 XMLContext xmlContext = new XMLContext(proj);
		 }catch(ValidationException validationException){
		     Throwable nestedException = validationException.getInternalException();
		     if(nestedException instanceof ClassNotFoundException){
		         return;
		     }
		 }
		 fail("An ClassNotFoundException should have occurred.");
	 }

	public static void main(String[] args) {
		String[] arguments = {
				"-c",
				"org.eclipse.persistence.testing.oxm.converter.jaxbenum.JAXBEnumTypeConverterTestCases" };
		TestRunner.main(arguments);
	}

}
