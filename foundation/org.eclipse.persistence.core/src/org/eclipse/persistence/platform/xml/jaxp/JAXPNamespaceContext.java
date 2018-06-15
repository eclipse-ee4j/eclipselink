/*
 * Copyright (c) 1998, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * SPDX-License-Identifier: EPL-2.0
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

    @Override
    public String getNamespaceURI(String prefix) {
        return xmlNamespaceResolver.resolveNamespacePrefix(prefix);
    }

    @Override
    public String getPrefix(String namespaceURI) {
        throw ValidationException.operationNotSupported("getPrefix");
    }

    @Override
    public Iterator getPrefixes(String namespaceURI) {
        throw ValidationException.operationNotSupported("getPrefixes");
    }

}
