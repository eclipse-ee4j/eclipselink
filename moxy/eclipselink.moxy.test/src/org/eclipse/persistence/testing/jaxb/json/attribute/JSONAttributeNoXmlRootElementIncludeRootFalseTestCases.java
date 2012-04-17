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
 *     Denise Smith - 2.4 - April 2012
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.json.attribute;

import org.eclipse.persistence.jaxb.JAXBMarshaller;

public class JSONAttributeNoXmlRootElementIncludeRootFalseTestCases extends JSONAttributeNoXmlRootElementTestCases{

	public JSONAttributeNoXmlRootElementIncludeRootFalseTestCases(String name) throws Exception {
		super(name);
		jaxbMarshaller.setProperty(JAXBMarshaller.JSON_INCLUDE_ROOT, false);
	
	}

}
