/*******************************************************************************
 * Copyright (c) 1998, 2013 Oracle and/or its affiliates. All rights reserved.
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
package org.eclipse.persistence.platform.xml.jaxp;

import java.util.Iterator;
import javax.xml.namespace.NamespaceContext;
import org.eclipse.persistence.exceptions.ValidationException;
import org.eclipse.persistence.platform.xml.XMLNamespaceResolver;

/**
 * <p><b>Purpose</b>: Wrap a org.eclipse.persistence.platform.xml.XMLNamespaceResolver 
 * and expose it as a javax.xml.namespace.NamespaceContext.</p> 
 */

public class JAXPNamespaceContext implements NamespaceContext {
	
	private XMLNamespaceResolver xmlNamespaceResolver;

	public JAXPNamespaceContext(XMLNamespaceResolver xmlNamespaceResolver) {
		this.xmlNamespaceResolver = xmlNamespaceResolver;
	}
	
	public String getNamespaceURI(String prefix) {
		return xmlNamespaceResolver.resolveNamespacePrefix(prefix); 
	}

	public String getPrefix(String namespaceURI) {
        throw ValidationException.operationNotSupported("getPrefix");
	}

	public Iterator getPrefixes(String namespaceURI) {
        throw ValidationException.operationNotSupported("getPrefixes");
	}
	
}
