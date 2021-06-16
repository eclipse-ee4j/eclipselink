/*
 * Copyright (c) 1998, 2021 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
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
