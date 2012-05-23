/*******************************************************************************
 * Copyright (c) 2011, 2012 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *     Denise Smith - 2.4
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.namespaces;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.persistence.jaxb.JAXBContextProperties;
import org.eclipse.persistence.jaxb.MarshallerProperties;
import org.eclipse.persistence.jaxb.UnmarshallerProperties;

public class NamespaceInheritanceSeparatorContextTestCases extends NamespaceInheritanceTestCases{

	private final static String JSON_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/employee_separator.json";
	private final static String JSON_WRITE_RESOURCE = "org/eclipse/persistence/testing/jaxb/json/namespaces/employee_different_separator.json";

	public NamespaceInheritanceSeparatorContextTestCases(String name) throws Exception {
		super(name);
		setControlJSON(JSON_RESOURCE);
		setWriteControlJSON(JSON_WRITE_RESOURCE);
		setClasses(new Class[]{Employee.class});
	}
	
	public Map getProperties(){
		Map props = new HashMap();
		props.put(JAXBContextProperties.JSON_NAMESPACE_SEPARATOR, '#');		
		return props;
	}
	
	public void testProperties() throws Exception{
		assertEquals('#', jsonMarshaller.getProperty(MarshallerProperties.JSON_NAMESPACE_SEPARATOR));
		assertEquals('#', jsonUnmarshaller.getProperty(UnmarshallerProperties.JSON_NAMESPACE_SEPARATOR));
	}
}
