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

    @Override
    public String resolveNamespacePrefix(String namespacePrefix) {
        return xmlNamespaceResolver.resolveNamespacePrefix(namespacePrefix);
    }
}
