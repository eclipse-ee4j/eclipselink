/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

// Contributors:
//     Oracle - initial API and implementation from Oracle TopLink
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
