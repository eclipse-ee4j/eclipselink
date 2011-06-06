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
 * Denise Smith - 2.3
 ******************************************************************************/
package org.eclipse.persistence.testing.jaxb.externalizedmetadata.xmlnametransformer;

import org.eclipse.persistence.oxm.XMLNameTransformer;

public class TransformerWithException implements XMLNameTransformer{
	
	public String transformTypeName(String name) {
		String s = null;
		s.charAt(0);		
		return name;
	}

	public String transformRootElementName(String name) {
		String s = null;
		s.charAt(0);
		return name;
	}
	
	public String transformElementName(String name) {
		String s = null;
		s.charAt(0);
		return name;
	}
	
	public String transformAttributeName(String name) {
		String s = null;
		s.charAt(0);
		return name;
	}

}
