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
package org.eclipse.persistence.testing.jaxb.inheritance.ns;

import org.eclipse.persistence.jaxb.JAXBMarshaller;
import org.eclipse.persistence.jaxb.JAXBUnmarshaller;

public class JAXBInheritanceNSSeparatorTestCases extends JAXBInheritanceNSTestCases {
	public JAXBInheritanceNSSeparatorTestCases(String name) throws Exception {
		super(name);
		setControlJSON("org/eclipse/persistence/testing/jaxb/inheritance/ns/inheritanceNSSeparator.json");
		jaxbMarshaller.setProperty(JAXBMarshaller.JSON_NAMESPACE_SEPARATOR, '*');
		jaxbUnmarshaller.setProperty(JAXBUnmarshaller.JSON_NAMESPACE_SEPARATOR, '*');
	}
	
	public JAXBMarshaller getJSONMarshaller() throws Exception{
		JAXBMarshaller m = super.getJSONMarshaller();
		m.setProperty(JAXBMarshaller.JSON_NAMESPACE_SEPARATOR, '*');
		return m;	
	}
}
