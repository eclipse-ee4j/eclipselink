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
package org.eclipse.persistence.platform.xml.xdk;

import org.eclipse.persistence.platform.xml.XMLNamespaceResolver;
import oracle.xml.parser.v2.NSResolver;

/**
 * <p><b>Purpose</b>: Wrap a org.eclipse.persistence.platform.xml.XMLNamespaceResolver 
 * and expose it as a oracle.xml.parser.v2.NSResolver.</p> 
 */

public class XDKNamespaceResolver implements NSResolver {
    private XMLNamespaceResolver xmlNamespaceResolver;

    public XDKNamespaceResolver(XMLNamespaceResolver xmlNamespaceResolver) {
        super();
        this.xmlNamespaceResolver = xmlNamespaceResolver;
    }

    public String resolveNamespacePrefix(String namespacePrefix) {
        return xmlNamespaceResolver.resolveNamespacePrefix(namespacePrefix);
    }
}
